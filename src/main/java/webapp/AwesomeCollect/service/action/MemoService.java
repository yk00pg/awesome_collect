package webapp.AwesomeCollect.service.action;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.dto.action.request.MemoRequestDto;
import webapp.AwesomeCollect.dto.action.response.MemoResponseDto;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.entity.action.Memo;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.exception.DuplicateType;
import webapp.AwesomeCollect.repository.action.MemoRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.user.UserProgressService;
import webapp.AwesomeCollect.service.junction.MemoTagJunctionService;

/**
 * メモのサービスクラス。
 */
@Service
public class MemoService {

  private final MemoRepository memoRepository;
  private final MemoTagJunctionService memoTagJunctionService;
  private final TagService tagService;
  private final UserProgressService userProgressService;
  private final SessionManager sessionManager;

  public MemoService(
      MemoRepository memoRepository, MemoTagJunctionService memoTagJunctionService,
      TagService tagService, UserProgressService userProgressService,
      SessionManager sessionManager){

    this.memoRepository = memoRepository;
    this.memoTagJunctionService = memoTagJunctionService;
    this.tagService = tagService;
    this.userProgressService = userProgressService;
    this.sessionManager = sessionManager;
  }

  /**
   * ユーザーIDを基にDBを確認し、メモが登録されていない場合は空のリストを、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトのリストを用意する。
   *
   * @param userId  ユーザーId
   * @return  メモ表示用データオブジェクト
   */
  public List<MemoResponseDto> prepareResponseDtoList(int userId){
    List<Memo> memoList = memoRepository.searchMemo(userId);
    if(memoList == null || memoList.isEmpty()){
      return new ArrayList<>();
    }else{
      return assembleCurrentResponseDtoList(memoList);
    }
  }

  /**
   * メモIDとユーザーIDを基にDBを確認し、メモが登録されていない場合はnullを返し、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを用意する。
   *
   * @param memoId  メモID
   * @param userId  ユーザーID
   * @return  メモ表示用データオブジェクト
   */
  public MemoResponseDto prepareResponseDto(int memoId, int userId){
    Memo memo = memoRepository.findMemoByIds(memoId, userId);
    if(memo == null){
      return null;
    }else{
      return assembleCurrentResponseDto(
          MemoResponseDto.fromEntityForDetail(memo), memoId);
    }
  }

  /**
   * メモIDが0の場合は空の入力用データオブジェクトを用意する。<br>
   * そうでない場合は、メモIDとユーザーIDを基にDBを確認し、メモが登録されていない場合は
   * nullを返し、登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param memoId  メモID
   * @param userId  ユーザーID
   * @return  メモ入力用データオブジェクト
   */
  public MemoRequestDto prepareRequestDto(int memoId, int userId){
    if(memoId == 0){
      return new MemoRequestDto();
    }

    Memo memo = memoRepository.findMemoByIds(memoId, userId);
    if(memo == null){
      return null;
    }else{
      return assembleCurrentRequestDto(memoId, memo);
    }
  }

  /**
   * メモリストを一覧ページの表示用データオブジェクトに変換し、紐付けられたタグ名リストを設定する。
   *
   * @param memoList  メモリスト
   * @return  メモ表示用データオブジェクトのリスト
   */
  private @NotNull List<MemoResponseDto> assembleCurrentResponseDtoList(
      List<Memo> memoList) {

    List<MemoResponseDto> dtoList = new ArrayList<>();
    for(Memo memo : memoList){
      MemoResponseDto dto =
          assembleCurrentResponseDto(
              MemoResponseDto.fromEntityForList(memo), memo.getId());
      dtoList.add(dto);
    }
    return dtoList;
  }

  /**
   * メモに紐付けられたタグ名リストを表示用データオブジェクトに設定する。
   *
   * @param dto メモ表示用データオブジェクト
   * @param memoId  メモID
   * @return  メモ表示用データオブジェクト
   */
  @Transactional
  private @NotNull MemoResponseDto assembleCurrentResponseDto(
      MemoResponseDto dto, int memoId) {

    List<Integer> tagIdList =
        memoTagJunctionService.prepareTagIdListByActionId(memoId);
    List<String> tagNameList =
        tagService.prepareTagNameListByTagIdList(tagIdList);

    dto.setTagList(tagNameList);
    return dto;
  }


  /**
   * メモを入力用データオブジェクトに変換し、紐付けられたタグ名を設定する。
   *
   * @param memoId  メモID
   * @param memo  メモ
   * @return  メモ入力用データオブジェクト
   */
  @Transactional
  private @NotNull MemoRequestDto assembleCurrentRequestDto(int memoId, Memo memo) {
    List<Integer> tagIdList =
        memoTagJunctionService.prepareTagIdListByActionId(memoId);
    String tagName = tagService.prepareCombinedTagName(tagIdList);

    MemoRequestDto dto = MemoRequestDto.fromEntity(memo);
    dto.setTags(tagName);
    return dto;
  }

  /**
   * データの種類に応じてDBに保存（登録・更新）し、セッションのレコード数更新情報を変更する。
   *
   * @param userId  ユーザーID
   * @param dto メモ入力用データオブジェクト
   * @return  メモID
   */
  public int saveMemo(int userId, MemoRequestDto dto){
    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());
    List<Integer> tagIdList = tagService.resolveTagIdList(userId, pureTagList);

    int memoId = dto.getId();
    if(memoId == 0){
      memoId = registerMemo(userId, dto, tagIdList);
    }else{
      updateMemo(userId, dto, tagIdList, memoId);
    }

    return memoId;
  }

  /**
   * DTOをエンティティに変換してDBに登録し、タグ情報を登録する。<br>
   * セッションのレコード数更新情報を変更し、ユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto メモ入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @return  登録したメモID
   * @throws DuplicateException 同ユーザーが同じタイトルをすでに登録している場合
   */
  @Transactional
  private int registerMemo(
      int userId, MemoRequestDto dto, List<Integer> tagIdList) {

    if(isDuplicateTitle(dto.getId(), userId, dto.getTitle())){
      throw new DuplicateException(DuplicateType.TITLE);
    }

    Memo memo = dto.toMemoForRegistration(userId);
    memoRepository.registerMemo(memo);
    memoTagJunctionService.registerNewRelations(
        memo.getId(), MemoTagJunction::new, tagIdList);

    sessionManager.setHasUpdatedRecordCount(true);
    userProgressService.updateUserProgress(userId);

    return memo.getId();
  }

  /**
   * DTOをエンティティに変換してDBのメモ、紐付けられたタグとの関係情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto メモ入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @param memoId  メモID
   * @throws DuplicateException 同ユーザーが同じタイトルをすでに登録している場合
   */
  @Transactional
  private void updateMemo(
      int userId, MemoRequestDto dto, List<Integer> tagIdList, int memoId)
      throws DuplicateException {

    if(isDuplicateTitle(memoId, userId, dto.getTitle())){
      throw new DuplicateException(DuplicateType.TITLE);
    }

    Memo memo = dto.toMemoForUpdate(userId);
    memoRepository.updateMemo(memo);
    memoTagJunctionService.updateRelations(memoId, MemoTagJunction::new, tagIdList);
  }

  // メモタイトルが重複しているか確認する。
  private boolean isDuplicateTitle(int memoId, int userId, String title){
    Integer recordId = memoRepository.findIdByUserIdAndTitle(userId, title.strip());
    return recordId != null && !recordId.equals(memoId);
  }

  /**
   * 指定のIDのメモ、紐付けられたタグとの関係情報を削除し、
   * セッションのレコード数更新情報を変更する。
   *
   * @param memoId  メモID
   */
  @Transactional
  public void deleteMemo(int memoId){
    memoTagJunctionService.deleteRelationByActionId(memoId);
    memoRepository.deleteMemo(memoId);

    sessionManager.setHasUpdatedRecordCount(true);
  }
}
