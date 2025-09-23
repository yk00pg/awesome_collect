package webapp.AwesomeCollect.awesome;

import lombok.Getter;

/**
 * ボーナスえらい！の種類を扱うenum。
 */
public enum BonusType {
  DAILY(1, "アクション登録") {
    @Override
    public boolean isApplicable(int currentStreak) {
      return true;
    }
  },
  THREE_DAYS(3, "3日連続アクション登録") {
    @Override
    public boolean isApplicable(int currentStreak) {
      return currentStreak % 3 == 0;
    }
  },
  WEEKLY(7, "7日連続アクション登録") {
    @Override
    public boolean isApplicable(int currentStreak) {
      return currentStreak % 7 == 0;
    }
  },
  MONTHLY(10, "30日連続アクション登録") {
    @Override
    public boolean isApplicable(int currentStreak) {
      return currentStreak % 30 == 0;
    }
  };

  @Getter
  private final int awesomePoint;
  @Getter
  private final String reason;

  BonusType(int awesomePoint, String reason){
    this.awesomePoint = awesomePoint;
    this.reason = reason;
  }

  /**
   * 現在の連続記録日数がボーナスえらい！の獲得対象か確認する。
   *
   * @param currentStreak 現在の連続記録日数
   * @return  ボーナスえらい！の獲得対象かどうか
   */
  public abstract boolean isApplicable(int currentStreak);
}
