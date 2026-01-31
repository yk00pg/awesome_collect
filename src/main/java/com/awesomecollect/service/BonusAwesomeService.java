package com.awesomecollect.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.awesomecollect.awesome.BonusType;
import com.awesomecollect.entity.BonusAwesome;
import com.awesomecollect.repository.BonusAwesomeRepository;

/**
 * ボーナスえらい！のサービスクラス。
 */
@Service
public class BonusAwesomeService {

  private final BonusAwesomeRepository bonusAwesomeRepository;

  public BonusAwesomeService(BonusAwesomeRepository bonusAwesomeRepository){
    this.bonusAwesomeRepository = bonusAwesomeRepository;
  }

  /**
   * ユーザーIDを基にDBからボーナスえらい！情報を取得し、獲得ポイントを合算する。
   *
   * @param userId  ユーザーID
   * @return  ボーナスえらい！の合計ポイント
   */
  public int calculateTotalBonusCount(int userId) {
    List<BonusAwesome> bonusAwesomeList =
        bonusAwesomeRepository.searchBonusAwesome(userId);
    return bonusAwesomeList.stream()
        .mapToInt(BonusAwesome::getAwesomePoint).sum();
  }

  /**
   * 現在の連続記録日数に応じて獲得できるボーナスえらい！をリスト化し、その内容をDBに登録する。
   *
   * @param userId  ユーザーID
   * @param currentStreak 現在の連続記録日数
   * @param today 今日の日付
   * @return  登録したボーナスえらい！の数
   */
  @Transactional
  public int registerBonusAwesome(int userId, int currentStreak, LocalDate today) {
    List<BonusType> applicableTypeList = findApplicableBonusTypes(currentStreak);
    List<BonusAwesome> bonusAwesomeList =
        createBonusAwesomeList(userId, today, applicableTypeList);

    bonusAwesomeList.forEach(bonusAwesomeRepository::registerBonusAwesome);

    return bonusAwesomeList.size();
  }

  /**
   * 現在の連続記録日数に合致するボーナスタイプをリスト化する。
   *
   * @param currentStreak 現在の連続記録日数
   * @return ボーナスタイプリスト
   */
  private List<BonusType> findApplicableBonusTypes(int currentStreak) {
    return Arrays.stream(BonusType.values())
        .filter(type -> type.isApplicable(currentStreak))
        .toList();
  }

  /**
   * ボーナスえらい！リスト（ユーザーID、ボーナスえらい！ポイント、獲得理由、獲得日）を作成する。
   *
   * @param userId  ユーザーID
   * @param today 今日の日付
   * @param applicableTypeList ボーナスタイプリスト
   * @return ボーナスえらい！リスト
   */
  private List<BonusAwesome> createBonusAwesomeList(
      int userId, LocalDate today, List<BonusType> applicableTypeList) {

    return applicableTypeList.stream()
        .map(type -> {
          BonusAwesome bonusAwesome = new BonusAwesome();
          bonusAwesome.setUserId(userId);
          bonusAwesome.setAwesomePoint(type.getAwesomePoint());
          bonusAwesome.setReason(type.getReason());
          bonusAwesome.setCollectedDate(today);
          return bonusAwesome;
        })
        .toList();
  }
}
