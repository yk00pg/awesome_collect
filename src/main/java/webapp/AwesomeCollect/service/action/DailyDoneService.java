package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.dto.action.request.DoneRequestDto;
import webapp.AwesomeCollect.dto.action.response.DoneResponseDto;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.repository.action.DailyDoneRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.user.UserProgressService;
import webapp.AwesomeCollect.service.junction.DoneTagJunctionService;

/**
 * できたことのサービスクラス。
 */
@Service
public class DailyDoneService {

  private final DailyDoneRepository dailyDoneRepository;
  private final DoneTagJunctionService doneTagJunctionService;
  private final TagService tagService;
  private final UserProgressService userProgressService;
  private final SessionManager sessionManager;

  public DailyDoneService(
      DailyDoneRepository dailyDoneRepository,
      DoneTagJunctionService doneTagJunctionService, TagService tagService,
      UserProgressService userProgressService, SessionManager sessionManager){

    this.dailyDoneRepository = dailyDoneRepository;
    this.doneTagJunctionService = doneTagJunctionService;
    this.tagService = tagService;
    this.userProgressService = userProgressService;
    this.sessionManager = sessionManager;
  }

  /**
   * DBにできたことが登録されていない場合は空の表示用データオブジェクトを、<br>
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを用意する。
   *
   * @param userId  ユーザーID
   * @param date  日付
   * @return  できたこと表示用データオブジェクト
   */
  public DoneResponseDto prepareResponseDto(int userId, LocalDate date){
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);
    if(dailyDoneList == null || dailyDoneList.isEmpty()){
      return DoneResponseDto.createBlankDto(date);
    }else{
      return assembleCurrentResponseDto(dailyDoneList);
    }
  }

  /**
   * DBにできたことが登録されていない場合は空の入力用データオブジェクトを、<br>
   * 登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param userId  ユーザーID
   * @param date  日付
   * @return  できたこと入力用データオブジェクト
   */
  public DoneRequestDto prepareRequestDto(int userId, LocalDate date){
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);
    if(dailyDoneList == null || dailyDoneList.isEmpty()){
      return DoneRequestDto.createBlankDto(date);
    }else{
      return assembleCurrentRequestDto(dailyDoneList);
    }
  }

  /**
   * できたことリストを表示用データオブジェクトに変換し、紐付けられたタグ名リストを設定する。
   *
   * @param dailyDoneList できたことリスト
   * @return  できたこと表示用データオブジェクト
   */
  @Transactional
  private @NotNull DoneResponseDto assembleCurrentResponseDto(
      List<DailyDone> dailyDoneList) {

    List<List<String>> tagNamesList = new ArrayList<>();
    for(DailyDone done : dailyDoneList){
      List<Integer> tagIdList =
          doneTagJunctionService.prepareTagIdListByActionId(done.getId());
      tagNamesList.add(tagService.prepareTagNameListByTagIdList(tagIdList));
    }

    DoneResponseDto dto = DoneResponseDto.fromDailyDone(dailyDoneList);
    dto.setTagsList(tagNamesList);
    return dto;
  }

  /**
   * できたことリストを入力用データオブジェクトに変換し、紐付けられたタグ名リストを設定する。
   *
   * @param dailyDoneList できたことリスト
   * @return  できたこと入力用データオブジェクト
   */
  @Transactional
  private @NotNull DoneRequestDto assembleCurrentRequestDto(
      List<DailyDone> dailyDoneList) {

    List<String> tagNameList = new ArrayList<>();
    for(DailyDone done : dailyDoneList){
      List<Integer> tagIdList =
          doneTagJunctionService.prepareTagIdListByActionId(done.getId());
      tagNameList.add(tagService.prepareCombinedTagName(tagIdList));
    }

    DoneRequestDto dto = DoneRequestDto.fromDailyDone(dailyDoneList);
    dto.setTagsList(tagNameList);
    return dto;
  }

  /**
   * データの種類に応じてDBに保存（登録・更新・削除）する。
   *
   * @param userId  ユーザーID
   * @param dto できたこと入力用データオブジェクト
   */
  public void saveDailyDone(int userId, DoneRequestDto dto){
    List<List<String>> pureTagsList = JsonConverter.extractValues(dto.getTagsList());

    for(int i = 0; i < dto.getContentList().size(); i++) {
      String content = dto.getContentList().get(i);
      if(content == null || content.isBlank()){
        continue;
      }

      int doneId = dto.getIdList().get(i);
      List<String> pureTagList = pureTagsList.get(i);
      List<Integer> tagIdList = tagService.resolveTagIdList(userId, pureTagList);

      if(doneId == 0){
        registerDailyDone(userId, dto, tagIdList, i);
      }else{
        if(dto.isDeletable(i)){
          deleteDailyDone(doneId);
        }else{
          updateDailyDone(userId, dto, doneId, i, tagIdList);
        }
      }
    }
  }

  /**
   * DTOをエンティティに変換してDBに登録し、タグ情報を登録する。<br>
   * セッションのレコード数更新情報、学習時間更新情報を変更し、
   * 日ごとの初回登録時のみユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto できたこと入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @param index リストのインデックス番号
   */
  @Transactional
  private void registerDailyDone(
      int userId, DoneRequestDto dto, List<Integer> tagIdList, int index){

    DailyDone dailyDone = dto.toDailyDoneForRegistration(userId, index);
    dailyDoneRepository.registerDailyDone(dailyDone);
    doneTagJunctionService.registerNewRelations(
          dailyDone.getId(), DoneTagJunction::new, tagIdList);

    sessionManager.setHasUpdatedRecordCount(true);
    sessionManager.setHasUpdateTime(true);

    if(index == 0){
      userProgressService.updateUserProgress(userId);
    }
  }

  /**
   * できたことIDを基にDBからタグレコード、できたことレコードを削除し、
   * セッションのレコード数更新情報、学習時間更新情報を変更する。
   *
   * @param doneId  できたことID
   */
  @Transactional
  private void deleteDailyDone(int doneId) {
    doneTagJunctionService.deleteRelationByActionId(doneId);
    dailyDoneRepository.deleteDailyDoneById(doneId);
    sessionManager.setHasUpdatedRecordCount(true);
    sessionManager.setHasUpdateTime(true);
  }

  /**
   * DTOをエンティティに変換してDBのできたことレコードとタグレコードを更新し、
   * セッションのレコード数更新情報を変更する。
   *
   * @param userId  ユーザーID
   * @param dto できたこと入力用データオブジェクト
   * @param doneId できたことID
   * @param index リストのインデックス番号
   * @param tagIdList タグIDリスト
   */
  @Transactional
  private void updateDailyDone(
      int userId, DoneRequestDto dto, int doneId, int index, List<Integer> tagIdList) {

    DailyDone dailyDone = dto.toDailyDoneForUpdate(userId, index);
    dailyDoneRepository.updateDailyDone(dailyDone);
    doneTagJunctionService.updateRelations(doneId, DoneTagJunction::new, tagIdList);

    sessionManager.setHasUpdateTime(true);
  }

  /**
   * 指定の日付のできたことをすべて削除し、セッションのレコード数更新情報、学習時間更新情報を変更する。
   *
   * @param userId  ユーザーID
   * @param date  日付
   */
  @Transactional
  public void deleteDailyAllDoneByDate(int userId, LocalDate date){
    doneTagJunctionService.deleteRelationByDate(userId, date);
    dailyDoneRepository.deleteDailyDoneByDate(userId, date);

    sessionManager.setHasUpdatedRecordCount(true);
    sessionManager.setHasUpdateTime(true);
  }
}
