package webapp.AwesomeCollect.dto.action;

import java.util.List;
import lombok.Data;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.Goal;

/**
 * 目標の表示用データオブジェクト。
 */
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

  // 進捗状況を日本語ラベルに変換する（Thymeleaf用）。
  public String getStatusLabel() {
    return switch (status) {
      case DOING -> DOING_LABEL;
      case ACHIEVED -> ACHIEVED_LABEL;
      default -> UNSET_LABEL;
    };
  }

  /**
   * DBから取得した目標を一覧ページ用データオブジェクトに変換する。
   *
   * @param goal  目標
   * @return  一覧ページ用データオブジェクト
   */
  public static GoalResponseDto fromEntityForList(Goal goal){
    GoalResponseDto dto = new GoalResponseDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.status = goal.isAchieved() ? ACHIEVED : DOING;
    return dto;
  }

  /**
   * DBから取得した目標を詳細ページ用データオブジェクトに変換する。
   *
   * @param goal  目標
   * @return  詳細ページ用データオブジェクト
   */
  public static GoalResponseDto fromEntityForDetail(Goal goal){
    GoalResponseDto dto = new GoalResponseDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.content = goal.getContent();
    dto.status = goal.isAchieved() ? ACHIEVED : DOING;
    dto.registeredAt = DateTimeFormatUtil.formatDateTime(goal.getRegisteredAt());
    dto.updatedAt = DateTimeFormatUtil.formatDateTime(goal.getUpdatedAt());
    return dto;
  }
}
