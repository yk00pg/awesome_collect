package webapp.AwesomeCollect.controller.action;

import jakarta.validation.Valid;
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
import webapp.AwesomeCollect.SaveResult;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.ArticleRequestDto;
import webapp.AwesomeCollect.dto.action.ArticleResponseDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.action.ArticleStockService;

/**
 * 記事ストックページのコントローラークラス。
 */
@Controller
public class ArticleStockController {

  private final ArticleStockService articleStockService;
  private final TagService tagService;
  private final MessageUtil messageUtil;

  public ArticleStockController(
      ArticleStockService articleStockService, TagService tagService,
      MessageUtil messageUtil){

    this.articleStockService = articleStockService;
    this.tagService = tagService;
    this.messageUtil = messageUtil;
  }

  // 記事ストックリストの一覧ページを表示`
  @GetMapping(ViewNames.ARTICLE_STOCK_PAGE)
  public String showArticleStock(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(
        AttributeNames.ARTICLE_RESPONSE_DTO_LIST,
        articleStockService.prepareResponseDtoList(customUserDetails.getId()));

    return ViewNames.ARTICLE_STOCK_PAGE;
  }

  // 記事ストックの詳細ページを表示
  @GetMapping(ViewNames.ARTICLE_STOCK_DETAIL_BY_ID)
  public String showArticleStockDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    ArticleResponseDto articleResponseDto =
        articleStockService.prepareResponseDto(id, customUserDetails.getId());

    if(articleResponseDto == null){
      return RedirectUtil.redirectView(ViewNames.ERROR_NOT_ACCESSIBLE);
    }else{
      model.addAttribute(AttributeNames.ARTICLE_RESPONSE_DTO, articleResponseDto);
      return ViewNames.ARTICLE_STOCK_DETAIL_PAGE;
    }
  }

  // 記事ストックの編集ページを表示
  @GetMapping(ViewNames.ARTICLE_STOCK_EDIT_BY_ID)
  public String showArticleStockForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    ArticleRequestDto articleRequestDto =
        articleStockService.prepareRequestDto(id,customUserDetails.getId());

    if(articleRequestDto == null){
      return RedirectUtil.redirectView(ViewNames.ERROR_NOT_ACCESSIBLE);
    }else{
      model.addAttribute(AttributeNames.ARTICLE_REQUEST_DTO, articleRequestDto);
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST,
          tagService.prepareTagListByUserId(customUserDetails.getId()));

      return ViewNames.ARTICLE_STOCK_EDIT_PAGE;
    }
  }

  /**
   * 入力されたデータを確認し、記事ストックを編集する。<br>
   * バインディングエラーが発生した場合はタグリストを詰め直して編集ページに戻り、
   * そうでない場合はDBの登録・更新処理を行い、詳細ページに遷移してサクセスメッセージを表示する。
   *
   * @param id  記事ストックID
   * @param dto 記事ストックのデータオブジェクト
   * @param result  バインディングの結果
   * @param model データをViewに渡すオブジェクト
   * @param customUserDetails カスタムユーザー情報
   * @param redirectAttributes  リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return  記事ストック・詳細ページ
   */
  @PostMapping(ViewNames.ARTICLE_STOCK_EDIT_BY_ID)
  public String editArticleStock(
      @PathVariable int id,
      @Valid @ModelAttribute(AttributeNames.ARTICLE_REQUEST_DTO) ArticleRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST,
          tagService.prepareTagListByUserId(customUserDetails.getId()));

      return ViewNames.ARTICLE_STOCK_EDIT_PAGE;
    }

    SaveResult saveResult =
        articleStockService.saveArticleStock(customUserDetails.getId(), dto);

    // 新規登録か更新かを判定してサクセスメッセージを表示
    if (id == 0) {
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

      // 達成状況が更新された場合もポップアップを表示
      if (saveResult.isUpdatedStatus()) {
        redirectAttributes.addFlashAttribute(
            AttributeNames.ACHIEVEMENT_POPUP,
            messageUtil.getMessage(MessageKeys.FINISHED_AWESOME));
      }
    }
    return RedirectUtil.redirectView(ViewNames.ARTICLE_STOCK_DETAIL_PAGE, saveResult.id());
  }

  // 指定のIDの目標を削除して一覧ページにリダイレクト
  @DeleteMapping(ViewNames.ARTICLE_STOCK_DETAIL_BY_ID)
  public String deleteStock(
      @PathVariable int id, RedirectAttributes redirectAttributes) {

    articleStockService.deleteArticleStock(id);

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.ARTICLE_STOCK_PAGE);
  }
}
