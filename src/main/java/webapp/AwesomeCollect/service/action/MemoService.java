package webapp.AwesomeCollect.service.action;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.dto.action.MemoRequestDto;
import webapp.AwesomeCollect.dto.action.MemoResponseDto;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.entity.action.Memo;
import webapp.AwesomeCollect.repository.MemoRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.UserProgressService;
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

  // DBの登録状況に応じた表示用データオブジェクトをリスト形式で用意
  public List<MemoResponseDto> prepareResponseDtoList(int userId){
    List<Memo> memoList = memoRepository.searchMemo(userId);
    if(memoList == null || memoList.isEmpty()){
      return new ArrayList<>();
    }else{
      return assembleCurrentResponseDtoList(memoList);
    }
  }

  // DBの登録内容を基に表示用データオブジェクトを組み立てる
  private @NotNull List<MemoResponseDto> assembleCurrentResponseDtoList(
      List<Memo> memoList) {

    List<MemoResponseDto> dtoList = new ArrayList<>();
    for(Memo memo : memoList){
      MemoResponseDto dto =
          assembleCurrentResponseDto(MemoResponseDto.fromEntityForList(memo), memo.getId());
      dtoList.add(dto);
    }
    return dtoList;
  }

  // DBの登録状況に応じた表示用データオブジェクトを用意
  public MemoResponseDto prepareResponseDto(int memoId, int userId){
    Memo memo = memoRepository.findMemoByIds(memoId, userId);
    if(memo == null){
      return null;
    }else{
      return assembleCurrentResponseDto(MemoResponseDto.fromEntityForDetail(memo), memoId);
    }
  }

  // DBの登録内容を基に表示用データオブジェクトを組み立てる
  private @NotNull MemoResponseDto assembleCurrentResponseDto(
      MemoResponseDto dto, int memoId) {

    List<Integer> tagIdList =
        memoTagJunctionService.prepareTagIdListByActionId(memoId);
    List<String> tagNameList =
        tagService.prepareTagNameListByTagIdList(tagIdList);

    dto.setTagList(tagNameList);
    return dto;
  }

  // DBの登録状況に応じた入力用データオブジェクトを用意
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

  // DBの登録内容を基に入力用データオブジェクトを組み立てる
  private @NotNull MemoRequestDto assembleCurrentRequestDto(int memoId, Memo memo) {
    MemoRequestDto dto = MemoRequestDto.fromEntity(memo);

    List<Integer> tagIdList =
        memoTagJunctionService.prepareTagIdListByActionId(memoId);
    String tagName = tagService.prepareCombinedTagName(tagIdList);

    dto.setTags(tagName);
    return dto;
  }

  /**
   * データの種類に応じてDBへの保存処理（登録・更新）を行う。
   *
   * @param userId  ユーザーID
   * @param dto メモのデータオブジェクト
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
   * DTOをエンティティに変換してDBに登録し、タグ情報を処理する。<br>
   * セッション情報を変更し、ユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto メモのデータオブジェクト
   * @param tagIdList タグIDリスト
   * @return  登録したメモID
   */
  private int registerMemo(
      int userId, MemoRequestDto dto, List<Integer> tagIdList) {

    Memo memo = dto.toMemoForRegistration(userId);
    memoRepository.registerMemo(memo);

    if(tagIdList != null){
      memoTagJunctionService.registerNewRelations(
          memo.getId(), MemoTagJunction :: new, tagIdList);
    }
    sessionManager.setHasUpdatedRecordCount(true);
    userProgressService.updateUserProgress(userId);

    return memo.getId();
  }

  /**
   * DTOをエンティティに変換し、DBのレコード、タグ情報、セッション情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto メモのデータオブジェクト
   * @param tagIdList タグIDリスト
   * @param memoId  メモID
   */
  private void updateMemo(
      int userId, MemoRequestDto dto, List<Integer> tagIdList, int memoId){

    Memo memo = dto.toMemoForUpdate(userId);
    memoRepository.updateMemo(memo);

    if(tagIdList != null){
      memoTagJunctionService.updateRelations(memoId, MemoTagJunction :: new, tagIdList);
    }
    sessionManager.setHasUpdatedRecordCount(true);
  }

  // 指定のIDのメモを削除
  public void deleteMemo(int memoId){
    memoTagJunctionService.deleteRelationByActionId(memoId);
    memoRepository.deleteMemo(memoId);

    sessionManager.setHasUpdatedRecordCount(true);
  }

  // 指定のユーザーのレコード数を取得
  public int countMemo(int userId){
    return memoRepository.countMemo(userId);
  }
}
