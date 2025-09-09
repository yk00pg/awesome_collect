package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.TaggingManager;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.dto.action.DoneRequestDto;
import webapp.AwesomeCollect.dto.action.DoneResponseDto;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.mapper.action.DailyDoneMapper;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.repository.DailyDoneRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.junction.DoneTagJunctionService;

@Service
public class DailyDoneService {

  private final DailyDoneRepository dailyDoneRepository;
  private final DoneTagJunctionService doneTagJunctionService;
  private final TagService tagService;
  private final TaggingManager taggingManager;
  private final UserProgressService userProgressService;
  private final SessionManager sessionManager;

  public DailyDoneService(
      DailyDoneRepository dailyDoneRepository,
      DoneTagJunctionService doneTagJunctionService, TagService tagService,
      TaggingManager taggingManager,
      UserProgressService userProgressService, SessionManager sessionManager){

    this.dailyDoneRepository = dailyDoneRepository;
    this.doneTagJunctionService = doneTagJunctionService;
    this.tagService = tagService;
    this.taggingManager = taggingManager;
    this.userProgressService = userProgressService;
    this.sessionManager = sessionManager;
  }

  // DBの登録状況に応じた閲覧用データオブジェクトを返す
  public DoneResponseDto prepareResponseDto(int userId, LocalDate date){
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);

    if(dailyDoneList == null || dailyDoneList.isEmpty()){
      return DoneResponseDto.createBlankDto(date);
    }else{
      return assembleCurrentResponseDto(dailyDoneList);
    }
  }

  // DBの登録状況に応じた編集用データオブジェクトを返す
  public DoneRequestDto prepareRequestDto(int userId, LocalDate date){
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);

    if(dailyDoneList == null || dailyDoneList.isEmpty()){
      return DoneRequestDto.createBlankDto(date);
    }else{
      return assembleCurrentRequestDto(dailyDoneList);
    }
  }

  // DBの登録内容を基に閲覧用データオブジェクトを組み立てて返す
  private @NotNull DoneResponseDto assembleCurrentResponseDto(
      List<DailyDone> dailyDoneList) {

    List<List<String>> tagNamesList = new ArrayList<>();
    for(DailyDone done : dailyDoneList){
      List<Integer> tagIdList =
          doneTagJunctionService.searchTagIdsByActionId(done.getId());

      tagNamesList.add(
          tagIdList == null || tagIdList.isEmpty()
              ? Collections.emptyList()
              : tagService.prepareTagListByTagIdList(tagIdList));
    }

    DoneResponseDto dto = DoneResponseDto.fromDailyDone(dailyDoneList);
    dto.setTagsList(tagNamesList);
    return dto;
  }

  // DBの登録内容を基に編集用データオブジェクトを組み立てて返す
  private @NotNull DoneRequestDto assembleCurrentRequestDto(
      List<DailyDone> dailyDoneList) {

    List<String> tagNameList = new ArrayList<>();
    for(DailyDone done : dailyDoneList){
      List<Integer> tagIdList =
          doneTagJunctionService.searchTagIdsByActionId(done.getId());

      tagNameList.add(
          tagIdList == null || tagIdList.isEmpty()
              ? ""
              : tagService.getCombinedTagName(tagIdList));
    }

    DoneRequestDto dto = DoneRequestDto.fromDailyDone(dailyDoneList);
    dto.setTagsList(tagNameList);
    return dto;
  }

  /**
   * データの種類に応じてDBへの保存処理（登録・削除・更新）を行う。
   *
   * @param userId  ユーザーID
   * @param dto できたことのデータオブジェクト
   */
  public void saveDailyDone(int userId, DoneRequestDto dto){
    List<List<String>> pureTagsList = JsonConverter.extractValues(dto.getTagsList());

    for(int i = 0; i < dto.getContentList().size(); i++) {
      String content = dto.getContentList().get(i);

      // 内容が空の場合はスキップ
      if(content == null || content.isBlank()){
        continue;
      }

      int id = dto.getIdList().get(i);
      List<String> pureTagList = pureTagsList.get(i);

      if(id == 0){
        registerDone(userId, dto, pureTagList, i);
      }else{
        // 削除チェックが入っているか確認
        if(dto.isDeletable(i)){
          doneTagJunctionService.deleteRelationByActionId(id);
          dailyDoneRepository.deleteDailyDoneById(id);
          sessionManager.setHasUpdatedRecordCount(true);
          sessionManager.setHasUpdateHours(true);
        }else{
          DailyDone dailyDone = dto.toDailyDoneWithId(userId, i);
          dailyDoneRepository.updateDailyDone(dailyDone);
          taggingManager.updateTagsAndRelations(
              id, pureTagList, userId, DoneTagJunction :: new, doneTagJunctionService);
          sessionManager.setHasUpdateHours(true);
        }
      }
    }
  }

  /**
   * DTOをエンティティに変換してDBに登録し、セッション情報を変更する。<br>
   * 初回登録時のみユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto できたことのデータオブジェクト
   * @param pureTagList コンバート済みタグリスト
   * @param index リストのインデックス番号
   */
  private void registerDone(
      int userId, DoneRequestDto dto, List<String> pureTagList, int index){

    DailyDone dailyDone = dto.toDailyDone(userId, index);
    dailyDoneRepository.registerDailyDone(dailyDone);
    // TODO: タグの処理
    taggingManager.resolveTagsAndRelations(
        dailyDone.getId(), pureTagList, userId, DoneTagJunction :: new, doneTagJunctionService);
    sessionManager.setHasUpdatedRecordCount(true);
    sessionManager.setHasUpdateHours(true);

    if(index == 0){
      userProgressService.updateUserProgress(userId);
    }
  }

  // 指定の日付のできたことをすべて削除
  public void deleteDailyDoneByDate(int userId, LocalDate date){
    dailyDoneRepository.deleteDailyDoneByDate(userId, date);
    sessionManager.setHasUpdatedRecordCount(true);
    sessionManager.setHasUpdateHours(true);
  }

  // 指定のユーザーのレコード数を取得
  public int countDailyDone(int userId){
    return dailyDoneRepository.countDailyDone(userId);
  }
}
