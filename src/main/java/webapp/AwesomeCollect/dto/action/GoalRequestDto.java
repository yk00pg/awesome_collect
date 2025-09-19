package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import webapp.AwesomeCollect.entity.action.Goal;

/**
 * 目標の入力用データオブジェクト。
 */
@Data
public class GoalRequestDto {

  private static final int TITLE_MAX_SIZE = 100;
  private static final int CONTENT_MAX_SIZE = 500;
  private static final int STATUS_SIZE = 3;

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
  private String content;

  @NotBlank(message = "{status.blank}")
  private String status;

  private String tags;

  // 進捗状況を日本語ラベルに変換
  public String getStatusLabel() {
    return switch (status) {
      case DOING -> DOING_LABEL;
      case ACHIEVED -> ACHIEVED_LABEL;
      default -> UNSET_LABEL;
    };
  }

  // 登録用のデータを詰めたエンティティに変換
  public Goal toGoalForRegistration(int userId){
    Goal goal = new Goal();
    goal.setUserId(userId);
    goal.setTitle(title);
    goal.setContent(content);
    goal.setAchieved(status.equals(ACHIEVED));
    goal.setRegisteredAt(LocalDateTime.now());
    return goal;
  }

  // 更新用のデータを詰めたエンティティに変換
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

  // DBから取得した目標をデータオブジェクトに変換
  public static GoalRequestDto fromEntity(Goal goal){
    GoalRequestDto dto = new GoalRequestDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.content = goal.getContent();
    dto.status = goal.isAchieved() ? ACHIEVED : DOING;
    return dto;
  }
}
