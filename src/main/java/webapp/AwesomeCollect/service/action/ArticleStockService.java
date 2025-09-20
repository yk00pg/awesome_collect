package webapp.AwesomeCollect.service.action;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.SaveResult;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.dto.action.ArticleRequestDto;
import webapp.AwesomeCollect.dto.action.ArticleResponseDto;
import webapp.AwesomeCollect.entity.junction.ArticleTagJunction;
import webapp.AwesomeCollect.entity.action.ArticleStock;
import webapp.AwesomeCollect.repository.ArticleStockRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.junction.ArticleTagJunctionService;

/**
 * 記事ストックのサービスクラス。
 */
@Service
public class ArticleStockService {

  private final ArticleStockRepository articleStockRepository;
  private final ArticleTagJunctionService articleTagJunctionService;
  private final TagService tagService;
  private final UserProgressService userProgressService;
  private final SessionManager sessionManager;

  public ArticleStockService(
      ArticleStockRepository articleStockRepository,
      ArticleTagJunctionService articleTagJunctionService,
      TagService tagService, UserProgressService userProgressService,
      SessionManager sessionManager){

    this.articleStockRepository = articleStockRepository;
    this.articleTagJunctionService = articleTagJunctionService;
    this.tagService = tagService;
    this.userProgressService = userProgressService;
    this.sessionManager = sessionManager;
  }

  /**
   * DBに記事ストックが登録されていない場合は空のリストを、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトのリストを用意する。
   *
   * @param userId  ユーザーID
   * @return  記事ストック表示用データオブジェクト
   */
  public List<ArticleResponseDto> prepareResponseDtoList(int userId){
    List<ArticleStock> articleStockList =
        articleStockRepository.searchArticleStock(userId);
    if(articleStockList == null || articleStockList.isEmpty()){
      return new ArrayList<>();
    }else{
      return assembleCurrentResponseDtoList(articleStockList);
    }
  }

  /**
   * DBに指定のIDの記事ストックが登録されていない場合はエラー画面に遷移させるためにnullを返し、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを用意する。
   *
   * @param articleId 記事ストックID
   * @param userId  ユーザーID
   * @return  記事ストック表示用データオブジェクト
   */
  public ArticleResponseDto prepareResponseDto(int articleId, int userId){
    ArticleStock articleStock =
        articleStockRepository.findArticleStockByIds(articleId, userId);
    if(articleStock == null){
      return null;
    }else{
      return assembleCurrentResponseDto(
          ArticleResponseDto.fromEntityForDetail(articleStock), articleId);
    }
  }

  /**
   * 記事ストックIDが0の場合は空の入力用データオブジェクトを用意する。<br>
   * そうでない場合は、DBに指定の記事ストックIDとユーザーIDの組み合わせが登録されていない場合は
   * エラー画面に遷移させるためにnullを返し、
   * 登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param articleId 記事ストックID
   * @param userId  ユーザーID
   * @return  記事ストック入力用データオブジェクト
   */
  public ArticleRequestDto prepareRequestDto(int articleId, int userId){
    if(articleId == 0){
      return new ArticleRequestDto();
    }

    ArticleStock articleStock =
        articleStockRepository.findArticleStockByIds(articleId, userId);
    if(articleStock == null){
      return null;
    }else{
      return assembleCurrentRequestDto(articleId, articleStock);
    }
  }

  /**
   * 記事ストックリストを一覧ページ用の表示用データオブジェクトに変換し、紐付けられたタグ名リストを設定する。
   *
   * @param articleStockList  記事ストックリスト
   * @return  記事ストック表示用データオブジェクトのリスト
   */
  private @NotNull List<ArticleResponseDto> assembleCurrentResponseDtoList(
      List<ArticleStock> articleStockList) {

    List<ArticleResponseDto> dtoList = new ArrayList<>();
    for(ArticleStock articleStock : articleStockList){
      ArticleResponseDto dto =
          assembleCurrentResponseDto(
              ArticleResponseDto.fromEntityForList(articleStock), articleStock.getId());
      dtoList.add(dto);
    }
    return dtoList;
  }

  /**
   * 記事ストックに紐付けられたタグ名リストを表示用データオブジェクトに設定する。
   *
   * @param dto 記事ストック表示用データオブジェクト
   * @param articleId 記事ストックID
   * @return  記事ストック表示用データオブジェクト
   */
  @Transactional
  private @NotNull ArticleResponseDto assembleCurrentResponseDto(
      ArticleResponseDto dto, int articleId) {

    List<Integer> tagIdList =
        articleTagJunctionService.prepareTagIdListByActionId(articleId);
    List<String> tagNameList =
        tagService.prepareTagNameListByTagIdList(tagIdList);

    dto.setTagList(tagNameList);
    return dto;
  }

  /**
   * 記事ストックを入力用データオブジェクトに変換し、紐付けられたタグ名を設定する。
   *
   * @param articleId 記事ストックID
   * @param articleStock  記事ストック
   * @return  記事ストック入力用データオブジェクト
   */
  @Transactional
  private @NotNull ArticleRequestDto assembleCurrentRequestDto(
      int articleId, ArticleStock articleStock) {

    ArticleRequestDto dto = ArticleRequestDto.fromEntity(articleStock);

    List<Integer> tagIdList =
        articleTagJunctionService.prepareTagIdListByActionId(articleId);
    String tagNames =
        tagService.prepareCombinedTagName(tagIdList);

    dto.setTags(tagNames);
    return dto;
  }

  /**
   * データの種類に応じてDBに保存（登録・更新）し、セッションのレコード数更新情報を変更する。
   *
   * @param userId  ユーザーID
   * @param dto 記事ストック入力用データオブジェクト
   * @return  保存結果
   */
  public SaveResult saveArticleStock(int userId, ArticleRequestDto dto){
    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());
    List<Integer> tagIdList = tagService.resolveTagIdList(userId, pureTagList);

    int id = dto.getId();
    SaveResult saveResult;
    if(id == 0){
      saveResult = registerArticleStock(userId, dto, tagIdList);
    }else{
      saveResult = updateArticleStock(userId, dto, tagIdList, id);
    }
    sessionManager.setHasUpdatedRecordCount(true);

    return saveResult;
  }

  /**
   * DTOをエンティティに変換してDBに登録してタグ情報を登録し、ユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto 記事ストック入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @return  保存結果
   */
  @Transactional
  private SaveResult registerArticleStock(
      int userId, ArticleRequestDto dto, List<Integer> tagIdList) {

    ArticleStock articleStock = dto.toArticleStockForRegistration(userId);
    articleStockRepository.registerArticleStock(articleStock);
    articleTagJunctionService.registerNewRelations(
        articleStock.getId(), ArticleTagJunction::new, tagIdList);

    userProgressService.updateUserProgress(userId);

    return new SaveResult(articleStock.getId(), false);
  }

  /**
   * DTOをエンティティに変換してDBの記事ストックレコードとタグレコードを更新する。
   *
   * @param userId  ユーザーID
   * @param dto 記事ストック入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @param id  記事ストックID
   * @return  保存結果
   */
  @Transactional
  private SaveResult updateArticleStock(
      int userId, ArticleRequestDto dto, List<Integer> tagIdList, int id) {

    ArticleStock articleStock = dto.toArticleStockForUpdate(userId);
    boolean isFinishedUpdate =
        !articleStockRepository.findArticleStockByIds(id, userId).isFinished()
            && articleStock.isFinished();

    articleStockRepository.updateArticleStock(articleStock);
    articleTagJunctionService.updateRelations(id, ArticleTagJunction::new, tagIdList);

    return new SaveResult(id, isFinishedUpdate);
  }

  /**
   * 指定のIDの記事ストックを削除し、セッションのレコード数更新情報を変更する。
   *
   * @param articleId 記事ストックID
   */
  @Transactional
  public void deleteArticleStock(int articleId){
    articleTagJunctionService.deleteRelationByActionId(articleId);
    articleStockRepository.deleteArticleStock(articleId);

    sessionManager.setHasUpdatedRecordCount(true);
  }
}
