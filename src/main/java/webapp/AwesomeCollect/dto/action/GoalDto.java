package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.Goal;

@Getter
@Setter
public class GoalDto extends BaseActionDto<Goal, GoalDto> {

  private int id;

  @Size(max = 100, message = "タイトルは100字以内で入力してください")
  private String title;

  @NotBlank(message = "内容を入力してください")
  @Size(max = 500, message = "内容は500字以内で入力してください")
  private String content;

  @NotBlank(message = "ドロップダウンリストから選択してください")
  @Size(max = 3, message = "ドロップダウンリストから選択してください")
  private String status;

  private String tags;

  private String registeredAt;
  private String updatedAt;

  /**
   * ステータス情報を日本語に変換する。
   *
   * @return 日本語のステータス情報
   */
  public String getStatusLabel() {
    return switch (status) {
      case "doing" -> "取組中";
      case "achieved" -> "達成！";
      default -> "未設定";
    };
  }

  /**
   * 内容が入力されているか判定する。
   *
   * @return 内容が入力されているかどうか
   */
  public boolean isContentEmpty() {
    return content == null || content.trim().isEmpty();
  }

  /**
   * データオブジェクトをエンティティに変換する。
   *
   * @param userId ユーザーID
   * @return 変換後のエンティティ
   */
  public Goal toGoal(int userId) {
    Goal goal = new Goal();
    goal.setUserId(userId);
    goal.setTitle(this.title);
    goal.setContent(this.content);
    goal.setAchieved(this.status.equals("achieved"));
    goal.setRegisteredAt(LocalDateTime.now());
    return goal;
  }

  /**
   * データオブジェクトをID付きでエンティティに変換する。
   *
   * @param userId ユーザーID
   * @return 変換後のエンティティ（ID付き）
   */
  public Goal toGoalWithId(int userId) {
    Goal goal = new Goal();
    goal.setId(this.id);
    goal.setUserId(userId);
    goal.setTitle(this.title);
    goal.setContent(this.content);
    goal.setAchieved(this.status.equals("achieved"));
    goal.setUpdatedAt(LocalDateTime.now());
    return goal;
  }

  @Override
  public int getId(){
    return this.id;
  }

  @Override
  public void setId(int id){
    this.id = id;
  }

  @Override
  public void setTags(String tags){
    this.tags = tags;
  }

  @Override
  public List<GoalDto> fromEntityList(List<Goal> goalList) {
    List<GoalDto> dtoList = new ArrayList<>();
    for(Goal goal : goalList) {
      GoalDto dto = new GoalDto();
      dto.id = goal.getId();
      dto.title = goal.getTitle();
      dto.content = goal.getContent();
      dto.status = goal.isAchieved() ? "achieved" : "doing";
      dtoList.add(dto);
    }
    return dtoList;
  }

  @Override
  public GoalDto fromEntity(Goal goal) {
    GoalDto dto = new GoalDto();
    dto.id = goal.getId();
    dto.title = goal.getTitle();
    dto.content = goal.getContent();
    dto.status = goal.isAchieved() ? "achieved" : "doing";
    dto.registeredAt = DateTimeFormatUtil.formatDateTime(goal.getRegisteredAt());
    dto.updatedAt = DateTimeFormatUtil.formatDateTime(goal.getUpdatedAt());

    return dto;
  }
}
