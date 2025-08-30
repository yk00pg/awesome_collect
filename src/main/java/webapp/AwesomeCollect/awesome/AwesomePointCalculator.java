package webapp.AwesomeCollect.awesome;


import org.springframework.stereotype.Component;
import webapp.AwesomeCollect.service.BonusAwesomeService;
import webapp.AwesomeCollect.service.action.ArticleStockService;
import webapp.AwesomeCollect.service.action.DailyDoneService;
import webapp.AwesomeCollect.service.action.DailyTodoService;
import webapp.AwesomeCollect.service.action.GoalService;
import webapp.AwesomeCollect.service.action.MemoService;

@Component
public class AwesomePointCalculator {

  private final DailyTodoService dailyTodoService;
  private final DailyDoneService dailyDoneService;
  private final GoalService goalService;
  private final MemoService memoService;
  private final ArticleStockService articleStockService;
  private final BonusAwesomeService bonusAwesomeService;

  // 通常のえらい！ポイント
  private static final int TODO = 1;
  private static final int DONE = 3;
  private static final int GOAL = 5;
  private static final int ACHIEVED = 10;
  private static final int MEMO = 5;
  private static final int ARTICLE_STOCK = 3;
  private static final int FINISHED = 5;

  public AwesomePointCalculator(
      DailyTodoService dailyTodoService, DailyDoneService dailyDoneService,
      GoalService goalService, MemoService memoService, ArticleStockService articleStockService,
      BonusAwesomeService bonusAwesomeService){

    this.dailyTodoService = dailyTodoService;
    this.dailyDoneService = dailyDoneService;
    this.goalService = goalService;
    this.memoService = memoService;
    this.articleStockService = articleStockService;
    this.bonusAwesomeService = bonusAwesomeService;
  }

  public int calculateAwesomePoint(int userId){
    int todoCount = dailyTodoService.countDailyTodo(userId) * TODO;
    int doneCount = dailyDoneService.countDailyDone(userId) * DONE;
    int goalCount = goalService.countGoal(userId) * GOAL;
    int achievedCount = goalService.countAchieved(userId) * ACHIEVED;
    int memoCount = memoService.countMemo(userId) * MEMO;
    int articleStockCount = articleStockService.countArticleStock(userId) * ARTICLE_STOCK;
    int finishedCount = articleStockService.countFinished(userId) * FINISHED;

    int totalNormalCount =
        todoCount + doneCount + goalCount + achievedCount + memoCount + articleStockCount + finishedCount;
    int totalBonusCount = bonusAwesomeService.calculateTotalBonusCount(userId);

    return totalNormalCount + totalBonusCount;
  }

}
