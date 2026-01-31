package webapp.AwesomeCollect.dto.action.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import webapp.AwesomeCollect.entity.action.Goal;

/**
 * 目標入力用データオブジェクト。
 */
@Data
public class GoalRequestDto {

  private static final int TITLE_MAX_SIZE = 100;
  private static final int CONTENT_MAX_SIZE = 500;

  private static final String ACHIEVED = "achieved";
  private static final String DOING = "doing";
  private static final String ACHIEVED_LABEL = "達成！";
  private static final String DOING_LABEL = "取組中";
  private static final String UNSET_LABEL = "未設定";

  private int id;

  @NotBlank(message = "{title.blank}")
  @Size(max = TITLE_MAX_SIZE, message = "{title.size}")
  private String title;

  @NotBlank(message = "{content.blank}")
  @Size(max = CONTENT_MAX_SIZE, message = "{content.size}")
  private String content;

  @NotBlank(message = "{status.blank}")
  private String status;

  private String tags;

  // 進捗状況を日本語ラベルに変換する（Thymeleaf用）。
  public String getStatusLabel() {
    return switch (status) {
      case DOING -> DOING_LABEL;
      case ACHIEVED -> ACHIEVED_LABEL;
      default -> UNSET_LABEL;
    };
  }

  /**
   * 入力されたデータを新規登録用のエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @return  新規登録用のエンティティ
   */
  public Goal toGoalForRegistration(int userId){
    Goal goal = new Goal();
    goal.setUserId(userId);
    goal.setTitle(title);
    goal.setContent(content);
    goal.setAchieved(status.equals(ACHIEVED));
    goal.setRegisteredAt(LocalDateTime.now());
    return goal;
  }

  /**
   * 入力されたデータを更新用のエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @return  更新用のエンティティ
   */
  public Goal toGoalForUpdate(int userId){
    Goal goal = new Goal();
    goal.setId(id);
    goal.setUserId(userId);
    goal.setTitle(title);
    goal.setContent(content);
    goal.setAchieved(status.equals(ACHIEVED));
    goal.setUpdatedAt(LocalDateTime.now());
    return goal;
  }

  /**
   * DBから取得した目標を入力用データオブジェクトに変換する。
   *
   * @param goal  目標
   * @return  入力用データオブジェクト
   */
  public static GoalRequestDto fromEntity(Goal goal){
    GoalRequestDto dto = new GoalRequestDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.content = goal.getContent();
    dto.status = goal.isAchieved() ? ACHIEVED : DOING;
    return dto;
  }
}
