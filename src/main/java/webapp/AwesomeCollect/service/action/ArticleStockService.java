package webapp.AwesomeCollect.service.action;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
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

  // DBの登録状況に応じた表示用データオブジェクトをリスト形式で用意
  public List<ArticleResponseDto> prepareResponseDtoList(int userId){
    List<ArticleStock> articleStockList =
        articleStockRepository.searchArticleStock(userId);
    if(articleStockList == null || articleStockList.isEmpty()){
      return new ArrayList<>();
    }else{
      return assembleCurrentResponseDtoList(articleStockList);
    }
  }

  // DBの登録内容を基に表示用データオブジェクトを組み立てる
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

  // DBの登録状況に応じた表示用データオブジェクトを用意
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

  // DBの登録内容を基に表示用データオブジェクトを組み立てる
  private @NotNull ArticleResponseDto assembleCurrentResponseDto(
      ArticleResponseDto dto, int articleId) {

    List<Integer> tagIdList =
        articleTagJunctionService.prepareTagIdListByActionId(articleId);
    List<String> tagNameList =
        tagService.prepareTagNameListByTagIdList(tagIdList);

    dto.setTagList(tagNameList);
    return dto;
  }

  // DBの登録状況に応じた入力用データオブジェクトを用意
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

  // DBの登録内容を基に入力用データオブジェクトを組み立てる
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
   * データの種類に応じてDBへの保存処理（登録・更新）を行う。
   *
   * @param userId  ユーザーID
   * @param dto 記事ストックのデータオブジェクト
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
   * DTOをエンティティに変換してDBに登録し、タグ情報を処理する。<br>
   * セッション情報を変更し、ユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto 記事ストックのデータオブジェクト
   * @param tagIdList タグIDリスト
   * @return  保存結果
   */
  private SaveResult registerArticleStock(
      int userId, ArticleRequestDto dto, List<Integer> tagIdList) {

    ArticleStock articleStock = dto.toArticleStockForRegistration(userId);
    articleStockRepository.registerArticleStock(articleStock);

    if(tagIdList != null){
      articleTagJunctionService.registerNewRelations(
          articleStock.getId(), ArticleTagJunction :: new, tagIdList);
    }
    userProgressService.updateUserProgress(userId);

    return new SaveResult(articleStock.getId(), false);
  }

  /**
   * DTOをエンティティに変換し、DBのレコード、タグ情報、セッション情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto 記事ストックのデータオブジェクト
   * @param tagIdList タグIDリスト
   * @param id  記事ストックID
   * @return  保存結果
   */
  private SaveResult updateArticleStock(
      int userId, ArticleRequestDto dto, List<Integer> tagIdList, int id) {

    ArticleStock articleStock = dto.toArticleStockForUpdate(userId);
    // 閲覧状況が更新されているか確認
    boolean isFinishedUpdate =
        !articleStockRepository.findArticleStockByIds(id, userId).isFinished()
            && articleStock.isFinished();

    articleStockRepository.updateArticleStock(articleStock);

    if(tagIdList != null){
      articleTagJunctionService.updateRelations(id, ArticleTagJunction :: new, tagIdList);
    }

    return new SaveResult(id, isFinishedUpdate);
  }

  // 指定のIDの目標を削除
  public void deleteArticleStock(int articleId){
    articleTagJunctionService.deleteRelationByActionId(articleId);
    articleStockRepository.deleteArticleStock(articleId);

    sessionManager.setHasUpdatedRecordCount(true);
  }

  // 指定のユーザーのレコード数を取得
  public int countArticleStock(int userId){
    return articleStockRepository.countArticleStock(userId);
  }

  // 指定のユーザーの読了レコード数を取得
  public int countFinished(int userId){
    return articleStockRepository.countFinished(userId);
  }
}
