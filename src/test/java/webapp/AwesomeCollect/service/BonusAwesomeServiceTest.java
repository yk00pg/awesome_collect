package webapp.AwesomeCollect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webapp.AwesomeCollect.entity.BonusAwesome;
import webapp.AwesomeCollect.repository.BonusAwesomeRepository;

@ExtendWith(MockitoExtension.class)
class BonusAwesomeServiceTest {

  @Mock
  BonusAwesomeRepository bonusAwesomeRepository;

  @InjectMocks
  BonusAwesomeService bonusAwesomeService;

  private static final int USER_ID = 1;
  private static final LocalDate TODAY = LocalDate.now();

  @Test
  void calculateTotalBonusCount_ユーザーIDを基にボーナスえらいポイントの合計を返す() {
    List<BonusAwesome> mockList = createMockList();

    when(bonusAwesomeRepository.searchBonusAwesome(USER_ID))
        .thenReturn(mockList);

    int result = bonusAwesomeService.calculateTotalBonusCount(USER_ID);

    assertEquals(15, result, "ボーナスえらいポイントの合計が返る");
    verify(bonusAwesomeRepository).searchBonusAwesome(USER_ID);
  }

  private List<BonusAwesome> createMockList() {
    BonusAwesome bonusAwesome1 = new BonusAwesome();
    bonusAwesome1.setUserId(USER_ID);
    bonusAwesome1.setAwesomePoint(10);

    BonusAwesome bonusAwesome2 = new BonusAwesome();
    bonusAwesome2.setUserId(USER_ID);
    bonusAwesome2.setAwesomePoint(5);

    return List.of(bonusAwesome1, bonusAwesome2);
  }

  @Test
  void registerBonusAwesome_連続記録日数が1日の場合は登録件数として1件を返す() {
    int currentStreak = 1;
    int result = bonusAwesomeService.registerBonusAwesome(USER_ID, currentStreak, TODAY);

    assertEquals(
        1,
        result,
        "アクション登録のみのため1件登録される");
    verify(bonusAwesomeRepository, times(1))
        .registerBonusAwesome(any(BonusAwesome.class));
  }

  @Test
  void registerBonusAwesome_連続記録日数が7日の場合は登録件数として2件を返す() {
    int currentStreak = 7;
    int result = bonusAwesomeService.registerBonusAwesome(USER_ID, currentStreak, TODAY);

    assertEquals(
        2,
        result,
        "アクション登録＋7日連続のため2件登録される");

    verify(bonusAwesomeRepository, times(2))
        .registerBonusAwesome(any(BonusAwesome.class));
  }

  @Test
  void registerBonusAwesome_連続日数が30日の場合は登録件数として3件を返す() {
    int currentStreak = 30;
    int result = bonusAwesomeService.registerBonusAwesome(USER_ID, currentStreak, TODAY);

    assertEquals(3,
        result,
        "アクション登録＋3日連続（3の倍数）＋30日連続のため3件登録される");

    verify(bonusAwesomeRepository, times(3))
        .registerBonusAwesome(any(BonusAwesome.class));
  }

}