package webapp.AwesomeCollect.controller.action;

import jakarta.validation.Valid;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.AwesomeCollect.common.SaveResult;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MappingValues;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.TemplateNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.request.ArticleRequestDto;
import webapp.AwesomeCollect.dto.action.response.ArticleResponseDto;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.action.ArticleStockService;

/**
 * 記事ストックのコントローラークラス。
 */
@Controller
public class ArticleStockController {

  private final ArticleStockService articleStockService;
  private final TagService tagService;
  private final MessageUtil messageUtil;

  public ArticleStockController(
      ArticleStockService articleStockService, TagService tagService,
      MessageUtil messageUtil) {

    this.articleStockService = articleStockService;
    this.tagService = tagService;
    this.messageUtil = messageUtil;
  }

  // 記事ストックの一覧ページ（記事ストックリスト）を表示する。
  @GetMapping(MappingValues.ARTICLE_STOCK)
  public String showArticleStock(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.ARTICLE_RESPONSE_DTO_LIST,
        articleStockService.prepareResponseDtoList(customUserDetails.getId()));

    return TemplateNames.ARTICLE_STOCK;
  }

  // 記事ストックの詳細ページを表示する。
  @GetMapping(MappingValues.ARTICLE_STOCK_DETAIL_BY_ID)
  public String showArticleStockDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    ArticleResponseDto articleResponseDto =
        articleStockService.prepareResponseDto(id, customUserDetails.getId());

    if (articleResponseDto == null) {
      return RedirectUtil.redirectView(MappingValues.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.ARTICLE_RESPONSE_DTO, articleResponseDto);
      return TemplateNames.ARTICLE_STOCK_DETAIL;
    }
  }

  // 記事ストックの編集ページを表示する。
  @GetMapping(MappingValues.ARTICLE_STOCK_EDIT_BY_ID)
  public String showArticleStockForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();
    ArticleRequestDto articleRequestDto =
        articleStockService.prepareRequestDto(id, userId);

    if (articleRequestDto == null) {
      return RedirectUtil.redirectView(MappingValues.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.ARTICLE_REQUEST_DTO, articleRequestDto);
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

      return TemplateNames.ARTICLE_STOCK_EDIT;
    }
  }

  /**
   * 入力されたデータにバインディングエラーまたは例外が発生した場合はタグリストを詰め直して
   * 編集ページに戻ってエラーメッセージを表示し、そうでない場合はDBに保存（登録・更新）し、
   * 詳細ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
   *
   * @param id                 記事ストックID
   * @param dto                記事ストック入力用データオブジェクト
   * @param result             バインディングの結果
   * @param model              データをViewに渡すオブジェクト
   * @param customUserDetails  カスタムユーザー情報
   * @param redirectAttributes リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return 記事ストック・詳細ページ
   */
  @PostMapping(MappingValues.ARTICLE_STOCK_EDIT_BY_ID)
  public String editArticleStock(
      @PathVariable int id,
      @Valid @ModelAttribute(AttributeNames.ARTICLE_REQUEST_DTO) ArticleRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if (result.hasErrors()) {
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));
      return TemplateNames.ARTICLE_STOCK_EDIT;
    }

    SaveResult saveResult = trySaveArticleStock(dto, result, model, userId);
    if (saveResult == null) {
      return TemplateNames.ARTICLE_STOCK_EDIT;
    }

    addAttributeBySaveType(id, redirectAttributes, saveResult);

    return RedirectUtil.redirectView(
        MappingValues.ARTICLE_STOCK_DETAIL, saveResult.id());
  }

  // DBへの保存を試みて保存結果を取得する。
  private @Nullable SaveResult trySaveArticleStock(
      ArticleRequestDto dto, BindingResult result, Model model, int userId) {

    SaveResult saveResult;
    try {
      saveResult = articleStockService.saveArticleStock(userId, dto);
    } catch (DuplicateException ex) {
      result.rejectValue(
          ex.getType().getFieldName(), "duplicate",
          messageUtil.getMessage(ex.getType().getMessageKey("article")));

      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

      return null;
    }
    return saveResult;
  }

  // 登録か更新か、更新の場合は閲覧状況が更新されたかを判定してサクセスメッセージとポップアップウィンドウを表示する。
  private void addAttributeBySaveType(
      int id, RedirectAttributes redirectAttributes, SaveResult saveResult) {

    boolean isRegistration = id == 0;
    if (isRegistration) {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.ARTICLE_AWESOME));
    } else {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));

      if (saveResult.isUpdatedStatus()) {
        redirectAttributes.addFlashAttribute(
            AttributeNames.ACHIEVEMENT_POPUP,
            messageUtil.getMessage(MessageKeys.FINISHED_AWESOME));
      }
    }
  }

  // 指定のIDの目標を削除して一覧ページにリダイレクトする。
  @DeleteMapping(MappingValues.ARTICLE_STOCK_DETAIL_BY_ID)
  public String deleteArticleStock(
      @PathVariable int id, RedirectAttributes redirectAttributes) {

    articleStockService.deleteArticleStock(id);

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(MappingValues.ARTICLE_STOCK);
  }
}
