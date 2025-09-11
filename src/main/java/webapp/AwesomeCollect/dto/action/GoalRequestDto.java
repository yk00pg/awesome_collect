package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import webapp.AwesomeCollect.entity.action.Goal;

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

  @Size(max = TITLE_MAX_SIZE, message = "{title.size}")
  private String title;

  @NotBlank(message = "{content.blank}")
  @Size(max = CONTENT_MAX_SIZE, message = "{content.size}")
  private String content;

  @NotBlank(message = "{status.valid}")
  @Size(min = 3, max = 3, message = "{status.valid}")
  private String status;

  private String tags;

  public String getStatusLabel() {
    return switch (status) {
      case "doing" -> "取組中";
      case "achieved" -> "達成！";
      default -> "未設定";
    };
  }

  public static GoalRequestDto fromEntityWithId(Goal goal){
    GoalRequestDto dto = new GoalRequestDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.content = goal.getContent();
    dto.status = goal.isAchieved() ? ACHIEVED : DOING;
    return dto;
  }
}
