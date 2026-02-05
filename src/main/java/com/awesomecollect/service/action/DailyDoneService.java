package com.awesomecollect.service.action;

import com.awesomecollect.common.util.JsonConverter;
import com.awesomecollect.dto.action.request.DoneRequestDto;
import com.awesomecollect.dto.action.response.DoneResponseDto;
import com.awesomecollect.dto.dummy.DummyDoneDto;
import com.awesomecollect.entity.action.DailyDone;
import com.awesomecollect.entity.junction.DoneTagJunction;
import com.awesomecollect.repository.action.DailyDoneRepository;
import com.awesomecollect.service.TagService;
import com.awesomecollect.service.junction.DoneTagJunctionService;
import com.awesomecollect.service.user.UserProgressService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * できたことのサービスクラス。
 */
@Service
public class DailyDoneService {

  private final DailyDoneRepository dailyDoneRepository;
  private final DoneTagJunctionService doneTagJunctionService;
  private final TagService tagService;
  private final UserProgressService userProgressService;

  public DailyDoneService(
      DailyDoneRepository dailyDoneRepository,
      DoneTagJunctionService doneTagJunctionService, TagService tagService,
      UserProgressService userProgressService) {

    this.dailyDoneRepository = dailyDoneRepository;
    this.doneTagJunctionService = doneTagJunctionService;
    this.tagService = tagService;
    this.userProgressService = userProgressService;
  }

  /**
   * 閲覧画面用データを用意する。<br>
   * ユーザーIDと日付を基にDBを確認し、できたことが登録されていない場合は空の表示用データオブジェクトを、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを返す。
   *
   * @param userId ユーザーID
   * @param date   日付
   * @return できたこと表示用データオブジェクト
   */
  @Transactional(readOnly = true)
  public DoneResponseDto prepareResponseDtoForList(int userId, LocalDate date) {
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);

    return dailyDoneList.isEmpty()
        ? DoneResponseDto.createBlankDto(date)
        : assembleCurrentResponseDto(dailyDoneList);
  }

  /**
   * 編集画面用データを用意する。<br>
   * ユーザーIDと日付を基にDBを確認し、できたことが登録されていない場合は空の入力用データオブジェクトを、
   * 登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param userId ユーザーID
   * @param date   日付
   * @return できたこと入力用データオブジェクト
   */
  @Transactional(readOnly = true)
  public DoneRequestDto prepareRequestDtoForEdit(int userId, LocalDate date) {
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);

    return dailyDoneList.isEmpty()
        ? DoneRequestDto.createBlankDto(date)
        : assembleCurrentRequestDto(dailyDoneList);
  }

  /**
   * できたことリストを表示用データオブジェクトに変換し、紐付けられたタグ名リストを設定する。
   *
   * @param dailyDoneList できたことリスト
   * @return できたこと表示用データオブジェクト
   */
  private @NotNull DoneResponseDto assembleCurrentResponseDto(
      List<DailyDone> dailyDoneList) {

    List<List<String>> tagNamesList = new ArrayList<>();
    for (DailyDone done : dailyDoneList) {
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
   * @return できたこと入力用データオブジェクト
   */
  private @NotNull DoneRequestDto assembleCurrentRequestDto(
      List<DailyDone> dailyDoneList) {

    List<String> tagNameList = new ArrayList<>();
    for (DailyDone done : dailyDoneList) {
      List<Integer> tagIdList =
          doneTagJunctionService.prepareTagIdListByActionId(done.getId());
      tagNameList.add(tagService.prepareCombinedTagName(tagIdList));
    }

    DoneRequestDto dto = DoneRequestDto.fromDailyDone(dailyDoneList);
    dto.setTagsList(tagNameList);
    return dto;
  }

  /**
   * データの種類に応じてDBに保存（登録・更新・削除、内容が空の場合はスキップ）する。
   *
   * @param userId ユーザーID
   * @param dto    できたこと入力用データオブジェクト
   */
  @Transactional
  public void saveDailyDone(int userId, DoneRequestDto dto) {
    List<List<String>> pureTagsList = JsonConverter.extractValues(dto.getTagsList());

    for (int i = 0; i < dto.getContentList().size(); i++) {
      String content = dto.getContentList().get(i);
      if (content.isBlank()) {
        continue;
      }

      // 可変行のID=0が効かないケースに備えてnullを回避
      int doneId =
          dto.getIdList().get(i) == null
              ? 0
              : dto.getIdList().get(i);

      List<Integer> tagIdList =
          tagService.resolveTagIdList(userId, pureTagsList.get(i));

      if (doneId == 0) {
        registerDailyDone(userId, dto, tagIdList, i);
      } else {
        if (dto.isDeletable(i)) {
          deleteDailyDone(doneId);
        } else {
          updateDailyDone(userId, dto, doneId, i, tagIdList);
        }
      }
    }
  }

  /**
   * 指定の日付のできたこと、紐付けられたタグとの関係情報をすべて削除する。
   *
   * @param userId ユーザーID
   * @param date   日付
   */
  @Transactional
  public void deleteDailyAllDoneByDate(int userId, LocalDate date) {
    doneTagJunctionService.deleteRelationByDate(userId, date);
    dailyDoneRepository.deleteDailyDoneByDate(userId, date);
  }

  /**
   * CSVファイルから読み込んだダミーデータをDBに登録する。
   *
   * @param guestUserId ゲストユーザーID
   * @param recordList  CSVファイルから読み込んだレコードリスト
   */
  @Transactional
  public void registerDummyDone(int guestUserId, List<DummyDoneDto> recordList){
    LocalDate referenceDate = LocalDate.now();
    for (int i = 0; i < recordList.size(); i++) {
      LocalDate date = referenceDate;
      DummyDoneDto dto = recordList.get(i);
      if(i == 0 || (!dto.getDate().equals(recordList.get(i - 1).getDate()))){
        date = referenceDate.minusDays(1);
      }

      DailyDone dailyDone = dto.toEntity(guestUserId, date);
      dailyDoneRepository.registerDailyDone(dailyDone);

      List<Integer> tagIdList = tagService.resolveTagIdList(guestUserId, dto.getTagList());
      doneTagJunctionService.registerNewRelations(
          dailyDone.getId(), DoneTagJunction :: new, tagIdList);

      referenceDate = date;
    }
  }

  /**
   * DTOをエンティティに変換してDBに登録し、タグ情報を登録する。<br>
   * 日ごとの初回登録時の場合は、ユーザーの進捗情報も併せて更新する。
   *
   * @param userId    ユーザーID
   * @param dto       できたこと入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @param index     リストのインデックス番号
   */
  private void registerDailyDone(
      int userId, DoneRequestDto dto, List<Integer> tagIdList, int index) {

    DailyDone dailyDone = dto.toDailyDoneForRegistration(userId, index);
    dailyDoneRepository.registerDailyDone(dailyDone);
    doneTagJunctionService.registerNewRelations(
        dailyDone.getId(), DoneTagJunction :: new, tagIdList);

    if (index == 0) {
      userProgressService.updateUserProgress(userId);
    }
  }

  /**
   * できたことIDを基にDBから紐付けられたタグとの関係情報、できたことを削除する。
   *
   * @param doneId できたことID
   */
  private void deleteDailyDone(int doneId) {
    doneTagJunctionService.deleteRelationByActionId(doneId);
    dailyDoneRepository.deleteDailyDoneById(doneId);
  }

  /**
   * DTOをエンティティに変換してDBのできたこと、紐付けれたタグとの関係情報を更新する。
   *
   * @param userId    ユーザーID
   * @param dto       できたこと入力用データオブジェクト
   * @param doneId    できたことID
   * @param index     リストのインデックス番号
   * @param tagIdList タグIDリスト
   */
  private void updateDailyDone(
      int userId, DoneRequestDto dto, int doneId, int index, List<Integer> tagIdList) {

    DailyDone dailyDone = dto.toDailyDoneForUpdate(userId, index);
    dailyDoneRepository.updateDailyDone(dailyDone);
    doneTagJunctionService.updateRelations(doneId, DoneTagJunction :: new, tagIdList);
  }
}
