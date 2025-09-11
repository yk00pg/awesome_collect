package webapp.AwesomeCollect.dto.action;

import java.util.List;
import lombok.Data;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.Goal;

@Data
public class GoalResponseDto {

  private static final String ACHIEVED = "achieved";
  private static final String DOING = "doing";
  private static final String ACHIEVED_LABEL = "達成！";
  private static final String DOING_LABEL = "取組中";
  private static final String UNSET_LABEL = "未設定";

  private int id;
  private String title;
  private String content;
  private String status;
  private List<String> tagList;
  private String registeredAt;
  private String updatedAt;

  public String getStatusLabel() {
    return switch (status) {
      case DOING -> DOING_LABEL;
      case ACHIEVED -> ACHIEVED_LABEL;
      default -> UNSET_LABEL;
    };
  }

  public static GoalResponseDto fromEntity(Goal goal){
    GoalResponseDto dto = new GoalResponseDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.status = goal.isAchieved() ? ACHIEVED : DOING;
    return dto;
  }

  public static GoalResponseDto fromEntityWithId(Goal goal){
    GoalResponseDto dto = new GoalResponseDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.status = goal.isAchieved() ? ACHIEVED : DOING;
    dto.registeredAt = DateTimeFormatUtil.formatDateTime(goal.getRegisteredAt());
    dto.updatedAt = DateTimeFormatUtil.formatDateTime(goal.getUpdatedAt());
    return dto;
  }
}
