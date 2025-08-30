package webapp.AwesomeCollect.awesome;

import lombok.Getter;

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

  public abstract boolean isApplicable(int currentStreak);
}
