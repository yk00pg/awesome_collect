package com.awesomecollect.service.action;

import com.awesomecollect.common.SaveResult;
import com.awesomecollect.common.util.JsonConverter;
import com.awesomecollect.dto.action.request.ArticleRequestDto;
import com.awesomecollect.dto.action.response.ArticleResponseDto;
import com.awesomecollect.dto.dummy.DummyArticleStockDto;
import com.awesomecollect.entity.action.ArticleStock;
import com.awesomecollect.entity.junction.ArticleTagJunction;
import com.awesomecollect.exception.DuplicateException;
import com.awesomecollect.exception.DuplicateType;
import com.awesomecollect.repository.action.ArticleStockRepository;
import com.awesomecollect.service.TagService;
import com.awesomecollect.service.junction.ArticleTagJunctionService;
import com.awesomecollect.service.user.UserProgressService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 記事ストックのサービスクラス。
 */
@Service
public class ArticleStockService {

  private final ArticleStockRepository articleStockRepository;
  private final ArticleTagJunctionService articleTagJunctionService;
  private final TagService tagService;
  private final UserProgressService userProgressService;

  public ArticleStockService(
      ArticleStockRepository articleStockRepository,
      ArticleTagJunctionService articleTagJunctionService,
      TagService tagService, UserProgressService userProgressService) {

    this.articleStockRepository = articleStockRepository;
    this.articleTagJunctionService = articleTagJunctionService;
    this.tagService = tagService;
    this.userProgressService = userProgressService;
  }

  /**
   * 一覧画面用データを用意する。<br>
   * ユーザーIDを基にDBを確認し、記事ストックが登録されていない場合は空のリストを、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトのリストを用意する。
   *
   * @param userId ユーザーID
   * @return 記事ストック表示用データオブジェクト
   */
  @Transactional(readOnly = true)
  public List<ArticleResponseDto> prepareResponseDtoListForList(int userId) {
    List<ArticleStock> articleStockList =
        articleStockRepository.searchArticleStock(userId);
    if (articleStockList == null || articleStockList.isEmpty()) {
      return new ArrayList<>();
    } else {
      return assembleCurrentResponseDtoList(articleStockList);
    }
  }

  /**
   * 詳細画面用データを用意する。<br>
   * 記事ストックIDとユーザーIDを基にDBを確認し、記事ストックが登録されていない場合はnullを返し、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを用意する。
   *
   * @param articleId 記事ストックID
   * @param userId    ユーザーID
   * @return 記事ストック表示用データオブジェクト
   */
  @Transactional(readOnly = true)
  public ArticleResponseDto prepareResponseDto(int articleId, int userId) {
    ArticleStock articleStock =
        articleStockRepository.findArticleStockByIds(articleId, userId);
    if (articleStock == null) {
      return null;
    } else {
      return assembleCurrentResponseDto(
          ArticleResponseDto.fromEntityForDetail(articleStock), articleId);
    }
  }

  /**
   * 編集画面用データを用意する。<br>
   * 記事ストックIDが0の場合は空の入力用データオブジェクトを用意する。<br>
   * そうでない場合は、記事ストックIDとユーザーIDを基にDBを確認し、記事ストックが登録されていない場合は
   * nullを返し、登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param articleId 記事ストックID
   * @param userId    ユーザーID
   * @return 記事ストック入力用データオブジェクト
   */
  @Transactional(readOnly = true)
  public ArticleRequestDto prepareRequestDto(int articleId, int userId) {
    if (articleId == 0) {
      return new ArticleRequestDto();
    }

    ArticleStock articleStock =
        articleStockRepository.findArticleStockByIds(articleId, userId);
    if (articleStock == null) {
      return null;
    } else {
      return assembleCurrentRequestDto(articleId, articleStock);
    }
  }

  /**
   * 記事ストックリストを一覧ページの表示用データオブジェクトに変換し、紐付けられたタグ名リストを設定する。
   *
   * @param articleStockList 記事ストックリスト
   * @return 記事ストック表示用データオブジェクトのリスト
   */
  private @NotNull List<ArticleResponseDto> assembleCurrentResponseDtoList(
      List<ArticleStock> articleStockList) {

    List<ArticleResponseDto> dtoList = new ArrayList<>();
    for (ArticleStock articleStock : articleStockList) {
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
   * @param dto       記事ストック表示用データオブジェクト
   * @param articleId 記事ストックID
   * @return 記事ストック表示用データオブジェクト
   */
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
   * @param articleId    記事ストックID
   * @param articleStock 記事ストック
   * @return 記事ストック入力用データオブジェクト
   */
  private @NotNull ArticleRequestDto assembleCurrentRequestDto(
      int articleId, ArticleStock articleStock) {

    List<Integer> tagIdList =
        articleTagJunctionService.prepareTagIdListByActionId(articleId);
    String tagNames =
        tagService.prepareCombinedTagName(tagIdList);

    ArticleRequestDto dto = ArticleRequestDto.fromEntity(articleStock);
    dto.setTags(tagNames);
    return dto;
  }

  /**
   * DBに記事ストック、タグ、紐付けられたタグとの関係情報を保存する。<br>
   * 記事ストックIDが0の場合は登録処理を、そうでない場合は更新処理を行う。
   *
   * @param userId ユーザーID
   * @param dto    記事ストック入力用データオブジェクト
   * @return 保存結果
   */
  @Transactional
  public SaveResult saveArticleStock(int userId, ArticleRequestDto dto) {
    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());
    List<Integer> tagIdList = tagService.resolveTagIdList(userId, pureTagList);

    int articleId = dto.getId();
    SaveResult saveResult;
    if (articleId == 0) {
      saveResult = registerArticleStock(userId, dto, tagIdList);
    } else {
      saveResult = updateArticleStock(userId, dto, tagIdList, articleId);
    }

    return saveResult;
  }

  /**
   * 指定のIDの記事ストック、紐付けられたタグとの関係情報をDBから削除する。
   *
   * @param articleId 記事ストックID
   */
  @Transactional
  public void deleteArticleStock(int articleId) {
    articleTagJunctionService.deleteRelationByActionId(articleId);
    articleStockRepository.deleteArticleStock(articleId);
  }

  /**
   * CSVファイルから読み込んだダミーデータをDBに登録する。
   *
   * @param guestUserId ゲストユーザーID
   * @param recordList  CSVファイルから読み込んだレコードリスト
   */
  @Transactional
  public void registerDummyArticleStock(int guestUserId, List<DummyArticleStockDto> recordList){
    recordList.forEach(dto ->{
      ArticleStock articleStock = dto.toEntity(guestUserId);
      articleStockRepository.registerArticleStock(articleStock);

      List<Integer> tagIdList =
          tagService.resolveTagIdList(guestUserId, dto.getTagList());
      articleTagJunctionService.registerNewRelations(
          articleStock.getId(), ArticleTagJunction::new, tagIdList);
    });
  }

  /**
   * DTOをエンティティに変換してDBに登録し、タグ情報を登録してユーザーの進捗情報を更新する。
   *
   * @param userId    ユーザーID
   * @param dto       記事ストック入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @return 保存結果オブジェクト
   * @throws DuplicateException 同ユーザーが同じタイトルまたはURLをすでに登録している場合
   */
  private SaveResult registerArticleStock(
      int userId, ArticleRequestDto dto, List<Integer> tagIdList)
      throws DuplicateException {

    if (isDuplicateTitle(dto.getId(), userId, dto.getTitle())) {
      throw new DuplicateException(DuplicateType.TITLE);
    }

    if (isDuplicateUrl(dto.getId(), userId, dto.getUrl())) {
      throw new DuplicateException(DuplicateType.URL);
    }

    ArticleStock articleStock = dto.toArticleStockForRegistration(userId);
    articleStockRepository.registerArticleStock(articleStock);
    articleTagJunctionService.registerNewRelations(
        articleStock.getId(), ArticleTagJunction :: new, tagIdList);

    userProgressService.updateUserProgress(userId);

    return new SaveResult(articleStock.getId(), false);
  }

  /**
   * DTOをエンティティに変換してDBの記事ストック、タグとの関係情報を更新する。
   *
   * @param userId    ユーザーID
   * @param dto       記事ストック入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @param articleId 記事ストックID
   * @return 保存結果オブジェクト
   * @throws DuplicateException 同ユーザーが同じタイトルまたはURLをすでに登録している場合
   */
  private SaveResult updateArticleStock(
      int userId, ArticleRequestDto dto, List<Integer> tagIdList, int articleId)
      throws DuplicateException {

    if (isDuplicateTitle(articleId, userId, dto.getTitle())) {
      throw new DuplicateException(DuplicateType.TITLE);
    }

    if (isDuplicateUrl(articleId, userId, dto.getUrl())) {
      throw new DuplicateException(DuplicateType.URL);
    }

    ArticleStock articleStock = dto.toArticleStockForUpdate(userId);
    boolean isFinishedUpdate = checkUpdatedStatus(userId, articleId, articleStock);

    articleStockRepository.updateArticleStock(articleStock);
    articleTagJunctionService.updateRelations(articleId, ArticleTagJunction :: new, tagIdList);

    return new SaveResult(articleId, isFinishedUpdate);
  }

  // 記事ストックタイトルが重複しているか確認する。
  private boolean isDuplicateTitle(int articleId, int userId, String title) {
    Integer recordId = articleStockRepository.findIdByUserIdAndTitle(userId, title.strip());
    return recordId != null && !recordId.equals(articleId);
  }

  // 記事のURLが重複しているか確認する。
  private boolean isDuplicateUrl(int articleId, int userId, String url) {
    Integer recordId = articleStockRepository.findIdByUserIdAndUrl(userId, url.strip());
    return recordId != null && !recordId.equals(articleId);
  }

  // 閲覧状況が更新されたか確認し、読了へ更新された場合はステータス更新日時を設定する。
  private boolean checkUpdatedStatus(int userId, int articleId, ArticleStock articleStock) {
    boolean isFinishedUpdate = false;
    ArticleStock currentRecord = articleStockRepository.findArticleStockByIds(articleId, userId);
    if (currentRecord.getStatusUpdatedAt() == null
        && !currentRecord.isFinished() && articleStock.isFinished()) {

      articleStock.setStatusUpdatedAt(LocalDateTime.now());
      isFinishedUpdate = true;
    }
    return isFinishedUpdate;
  }
}
