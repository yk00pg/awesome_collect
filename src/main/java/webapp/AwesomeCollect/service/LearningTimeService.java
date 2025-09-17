package webapp.AwesomeCollect.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.util.LearningTimeConverter;
import webapp.AwesomeCollect.dto.LearningTimeDto;
import webapp.AwesomeCollect.entity.AvgLearningTime;
import webapp.AwesomeCollect.entity.TotalLearningTime;
import webapp.AwesomeCollect.entity.TagLearningTime;
import webapp.AwesomeCollect.repository.LearningTimeRepository;

@Service
public class LearningTimeService {

  private final LearningTimeRepository learningTimeRepository;
  private final SessionManager sessionManager;

  private static final String UNCATEGORIZED = "(未設定)";

  public LearningTimeService(
      LearningTimeRepository learningTimeRepository, SessionManager sessionManager){

    this.learningTimeRepository = learningTimeRepository;
    this.sessionManager = sessionManager;
  }

  public LearningTimeDto prepareLearningTimeDto(int userId){

    Boolean hasUpdatedTime = sessionManager.hasUpdatedTime();
    LearningTimeDto learningTimeDto = sessionManager.getCachedLearningTimeDto();

    if(hasUpdatedTime == null || hasUpdatedTime || learningTimeDto == null){
      int totalTime = learningTimeRepository.getTotalHours(userId);
      int totalHours = LearningTimeConverter.toHoursPart(totalTime);
      int totalMinutes = LearningTimeConverter.toMinutesPart(totalTime);

      LocalDate today = LocalDate.now();
      List<TotalLearningTime> sevenDaysTimeList = getDailyTotalTimeList(userId, today);
      List<AvgLearningTime> dayOfWeekTimeList = getDayOfWeekAvgTimeList(userId);
      List<TotalLearningTime> sixMonthTimeList = getMonthlyTotalTimeList(userId, today);
      List<TagLearningTime> tagTotalTimeList = getTagTotalTimeList(userId);
      List<TagLearningTime> topTenTagTotalTimeList = tagTotalTimeList.stream().limit(10).toList();

      LearningTimeDto dto = new LearningTimeDto(
          totalHours, totalMinutes, sevenDaysTimeList, dayOfWeekTimeList,
          sixMonthTimeList, topTenTagTotalTimeList, tagTotalTimeList);

      sessionManager.setLearningTimeDto(dto);
      sessionManager.setHasUpdateTime(false);
      return dto;
    }else{
      return sessionManager.getCachedLearningTimeDto();
    }
  }

  public List<TagLearningTime> prepareTagTotalTimeList(){
    return sessionManager.getCachedLearningTimeDto().getTagTotalTimeList();
  }

  private @NotNull List<TotalLearningTime> getDailyTotalTimeList(
      int userId, LocalDate today) {

    LocalDate fromDate = today.minusDays(6);

    List<LocalDate> dateList =
        IntStream.rangeClosed(0, 6)
            .mapToObj(i -> today.minusDays(6 - i))
            .toList();

    List<TotalLearningTime> dailyTimeList =
        learningTimeRepository.getDailyTotalHours(userId, fromDate, today);

    Map<LocalDate, Integer> timeMap = dailyTimeList.stream()
        .collect(Collectors.toMap(TotalLearningTime :: getDate, TotalLearningTime :: getTotalTime));

    return dateList.stream()
        .map(d -> {
          int time = timeMap.getOrDefault(d, 0);
          return new TotalLearningTime(d, time);
        })
        .toList();
  }

  private @NotNull List<AvgLearningTime> getDayOfWeekAvgTimeList(int userId) {
    List<AvgLearningTime> dayOfWeekAvgTimeList =
        learningTimeRepository.getAvgDayOfWeekTime(userId);

    List<Integer> dayOfWeekList = IntStream.rangeClosed(0, 7).boxed().toList();

    Map<Integer, Integer> avgTimeMap = dayOfWeekAvgTimeList.stream()
        .collect(Collectors.toMap(
            avgLearningTime -> (avgLearningTime.getDayOfWeek() - 1) % 7,
            AvgLearningTime::getAvgTime));

    return dayOfWeekList.stream()
        .map(d -> {
          int avgTime =avgTimeMap.getOrDefault(d, 0);
          return new AvgLearningTime(d, avgTime);
        })
        .toList();
  }

  private @NotNull List<TotalLearningTime> getMonthlyTotalTimeList(
      int userId, LocalDate today) {

    LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
    LocalDate fromDate = firstDayOfThisMonth.minusMonths(5);

    List<LocalDate> monthList =
        IntStream.rangeClosed(0, 5)
            .mapToObj(i -> firstDayOfThisMonth.minusMonths(5 - i))
            .toList();

    List<TotalLearningTime> monthlyTimeList =
        learningTimeRepository.getMonthlyTotalHours(userId, fromDate, today);

    Map<LocalDate, Integer> timeMap = monthlyTimeList.stream()
        .collect(Collectors.toMap(TotalLearningTime :: getDate, TotalLearningTime :: getTotalTime));

    return monthList.stream()
        .map(m -> {
          int time = timeMap.getOrDefault(m, 0);
          return new TotalLearningTime(m, time);
        })
        .toList();
  }

  private @NotNull List<TagLearningTime> getTagTotalTimeList(int userId) {
    List<TagLearningTime> tagTimeList =
        learningTimeRepository.getTotalHoursByTag(userId);

    return tagTimeList.stream()
        .sorted(
            Comparator
                .comparing((TagLearningTime t) -> UNCATEGORIZED.equals(t.getTagName()))
                .thenComparing(Comparator.comparingInt(TagLearningTime::getTotalTime).reversed())
        )
        .toList();
  }
}
