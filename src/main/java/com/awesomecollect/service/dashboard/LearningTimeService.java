package com.awesomecollect.service.dashboard;

import com.awesomecollect.common.util.LearningTimeConverter;
import com.awesomecollect.dto.dashboard.LearningTimeDto;
import com.awesomecollect.entity.dashboard.AvgLearningTime;
import com.awesomecollect.entity.dashboard.TagLearningTime;
import com.awesomecollect.entity.dashboard.TotalLearningTime;
import com.awesomecollect.repository.dashboard.LearningTimeRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学習時間のサービスクラス。
 */
@Service
public class LearningTimeService {

  private final LearningTimeRepository learningTimeRepository;

  private static final String UNCATEGORIZED = "(未設定)";

  public LearningTimeService(LearningTimeRepository learningTimeRepository) {
    this.learningTimeRepository = learningTimeRepository;
  }

  /**
   * 学習時間更新フラグがfalseで学習時間のキャッシュDTOがnullでない場合は、キャッシュDTOをそのまま返す。<br>
   * そうでない場合は、累計学習時間（時間、分）、日別学習時間、曜日別平均学習時間、月別学習時間、
   * タグ別学習時間を算出し、DTOに詰めて返す。
   *
   * @param userId ユーザーID
   * @param hasUpdatedTime 学習時間更新フラグ
   * @param cachedLearningTimeDto 学習時間のキャッシュDTO
   * @return 学習時間表示用データオブジェクト
   */
  @Transactional
  public LearningTimeDto prepareLearningTimeDto(
      int userId, Boolean hasUpdatedTime, LearningTimeDto cachedLearningTimeDto) {

    if (!hasUpdatedTime && cachedLearningTimeDto != null) {
      return cachedLearningTimeDto;
    } else {
      int totalTime = learningTimeRepository.getTotalHours(userId);
      int totalHours = LearningTimeConverter.toHoursPart(totalTime);
      int totalMinutes = LearningTimeConverter.toMinutesPart(totalTime);

      LocalDate today = LocalDate.now();
      List<TotalLearningTime> sevenDaysTimeList = getDailyTotalTimeList(userId, today);
      List<AvgLearningTime> dayOfWeekTimeList = getDayOfWeekAvgTimeList(userId);
      List<TotalLearningTime> sixMonthTimeList = getMonthlyTotalTimeList(userId, today);
      List<TagLearningTime> tagTotalTimeList = getTagTotalTimeList(userId);
      // タグ別学習時間・上位抜粋用に上位10件を抽出
      List<TagLearningTime> topTenTagTotalTimeList =
          tagTotalTimeList.stream().limit(10).toList();

      return new LearningTimeDto(
          totalHours, totalMinutes, sevenDaysTimeList, dayOfWeekTimeList,
          sixMonthTimeList, topTenTagTotalTimeList, tagTotalTimeList);
    }
  }

  /**
   * 今日から6日前（7日間）の日別学習時間リストをDBから取得し、日付別にマップに詰め直す。<br>
   * 今日を含めた7日分の日付リストを作成し、マップと照らしてレコードのない日は学習時間を0として、
   * 7日分の日別学習時間リストを作成する。
   *
   * @param userId ユーザーID
   * @param today  今日の日付
   * @return 7日分の日別学習時間リスト
   */
  private @NotNull List<TotalLearningTime> getDailyTotalTimeList(
      int userId, LocalDate today) {

    LocalDate fromDate = today.minusDays(6);
    List<TotalLearningTime> dailyTimeList =
        learningTimeRepository.getDailyTotalHours(userId, fromDate, today);

    Map<LocalDate, Integer> timeMap = dailyTimeList.stream()
        .collect(Collectors
            .toMap(TotalLearningTime :: getDate, TotalLearningTime :: getTotalTime));

    List<LocalDate> dateList =
        IntStream.rangeClosed(0, 6)
            .mapToObj(i -> today.minusDays(6 - i))
            .toList();

    return dateList.stream()
        .map(d -> {
          int time = timeMap.getOrDefault(d, 0);
          return new TotalLearningTime(d, time);
        })
        .toList();
  }

  /**
   * 曜日別平均学習時間リストをDBから取得し、曜日別にマップに詰め直す。<br>
   * 曜日リストを作成し、マップと照らしてレコードのない曜日は学習時間を0として、
   * 曜日別平均学習時間リストを作成する。
   *
   * @param userId ユーザーID
   * @return 曜日別平均学習時間リスト
   */
  private @NotNull List<AvgLearningTime> getDayOfWeekAvgTimeList(int userId) {
    List<AvgLearningTime> dayOfWeekAvgTimeList =
        learningTimeRepository.getAvgDayOfWeekTime(userId);

    Map<Integer, Integer> avgTimeMap = dayOfWeekAvgTimeList.stream()
        .collect(Collectors
            .toMap(avgLearningTime ->
                (avgLearningTime.getDayOfWeek() - 1) % 7, AvgLearningTime :: getAvgTime));

    List<Integer> dayOfWeekList = IntStream.rangeClosed(0, 7).boxed().toList();

    return dayOfWeekList.stream()
        .map(d -> {
          int avgTime = avgTimeMap.getOrDefault(d, 0);
          return new AvgLearningTime(d, avgTime);
        })
        .toList();
  }

  /**
   * 今月から5ヶ月前（6ヶ月間）の月別学習時間リストをDBから取得し、月別にマップに詰め直す。<br>
   * 今月を含めた6ヶ月分の月間リストを作成し、マップと照らしてレコードのない月は学習時間を0として、
   * 6ヶ月分の月別学習時間リストを作成する。
   *
   * @param userId ユーザーID
   * @param today  今日
   * @return 6ヶ月分の月別学習時間リスト
   */
  private @NotNull List<TotalLearningTime> getMonthlyTotalTimeList(
      int userId, LocalDate today) {

    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate fromDate = firstDayOfThisMonth.minusMonths(5);
    List<TotalLearningTime> monthlyTimeList =
        learningTimeRepository.getMonthlyTotalHours(userId, fromDate, today);

    Map<LocalDate, Integer> timeMap = monthlyTimeList.stream()
        .collect(Collectors.
            toMap(TotalLearningTime :: getDate, TotalLearningTime :: getTotalTime));

    List<LocalDate> monthList =
        IntStream.rangeClosed(0, 5)
            .mapToObj(i -> firstDayOfThisMonth.minusMonths(5 - i))
            .toList();

    return monthList.stream()
        .map(m -> {
          int time = timeMap.getOrDefault(m, 0);
          return new TotalLearningTime(m, time);
        })
        .toList();
  }

  /**
   * DBからタグ別学習時間リストを取得し、学習時間が多い順（未設定は最後）に並び替える。
   *
   * @param userId ユーザーID
   * @return タグ別学習時間リスト
   */
  private @NotNull List<TagLearningTime> getTagTotalTimeList(int userId) {
    List<TagLearningTime> tagTimeList =
        learningTimeRepository.getTotalHoursByTag(userId);

    return tagTimeList.stream()
        .sorted(
            Comparator
                .comparing((TagLearningTime t) -> UNCATEGORIZED.equals(t.getTagName()))
                .thenComparing(
                    Comparator.comparingInt(TagLearningTime :: getTotalTime)
                        .reversed())
        )
        .toList();
  }
}
