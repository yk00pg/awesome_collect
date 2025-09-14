package webapp.AwesomeCollect.controller.action;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.common.ActionViewPreparator;
import webapp.AwesomeCollect.dto.action.ArticleStockDto;
import webapp.AwesomeCollect.entity.action.ArticleStock;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.action.ArticleStockService;
import webapp.AwesomeCollect.service.junction.ArticleTagJunctionService;

@Controller
public class ArticleStockController {

  private final ArticleStockService articleStockService;
  private final TagService tagService;
  private final ActionViewPreparator actionViewPreparator;
  private final ArticleTagJunctionService articleTagJunctionService;
  private final UserProgressService userProgressService;

  public ArticleStockController(
      ArticleStockService articleStockService, TagService tagService,
      ActionViewPreparator actionViewPreparator, ArticleTagJunctionService articleTagJunctionService,
      UserProgressService userProgressService){

    this.articleStockService = articleStockService;
    this.tagService = tagService;
    this.actionViewPreparator = actionViewPreparator;
    this.articleTagJunctionService = articleTagJunctionService;
    this.userProgressService = userProgressService;
  }

  /**
   * 「記事ストック」の一覧画面を表示する。<br>
   * ログイン中のユーザーのユーザーIDを取得し、ユーザーIDを基にDBから記事ストックリストを取得する。<br>
   * 記事ストックリストが空（未登録）の場合は空のデータオブジェクトをモデルに追加し。
   * そうでない場合は登録済みデータを入れたデータオブジェクトをモデルに追加する。
   *
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「記事ストック」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  article_stock.html
   */
  @GetMapping(value = "/article_stock")
  public String showArticleStock(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      ArticleStockDto dto, Model model){

    int userId = customUserDetails.getId();
    List<ArticleStock> articleStockList = articleStockService.searchArticleStock(userId);
    if(!articleStockList.isEmpty()){
      actionViewPreparator.prepareCurrentDataListView(
          dto, model, articleStockList, articleTagJunctionService);
    }
    return "/article_stock";
  }

  /**
   * ID別の「記事ストック」の編集画面を表示する。
   *
   * @param id  記事ストックID
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「記事ストック」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  article_stock/edit.html
   * @throws AccessDeniedException  登録していないIDのページを開こうとした場合
   */
  @GetMapping(value = "article_stock/edit/{id}")
  public String showArticleStockForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      ArticleStockDto dto, Model model) throws AccessDeniedException{

   int userId = customUserDetails.getId();
   List<String> tagNameList = tagService.prepareTagListByUserId(userId);

   if(id == 0){
     actionViewPreparator.prepareBlankDoneView(id, dto, model, tagNameList);
   }else{
     ArticleStock articleStock = articleStockService.findArticleStockByIds(id, userId);
     if(articleStock == null){
       throw new AccessDeniedException("不正なアクセスです");
     }else{
       actionViewPreparator.prepareCurrentDataView(
           dto, model, articleStock, tagNameList, articleTagJunctionService);
     }
   }
   return "/article_stock/edit";
  }

  /**
   * 登録状況に応じて分岐し、入力された内容を登録/更新/削除する。<br>
   * 入力されたハッシュタグ（JSON形式）を文字列に変換し、リスト形式で取得する。<br>
   * ログイン中のユーザーのユーザーIDを取得する。<br>
   * 記事ストックIDが0（新規登録）の場合は、URLが空であればデータバインディングの結果にエラーとして追加し、
   * そうでなければ記事ストックとタグの登録処理を実行する。<br>
   * 記事ストックIDが0以外（更新）の場合は、URLが空であれば削除処理を実行し、そうでなければ更新処理を実行する。
   *
   * @param id  記事ストックID
   * @param dto 「記事ストック」のデータオブジェクト
   * @param result  データバインディングの結果
   * @param customUserDetails ログイン中のユーザー情報
   * @return  article_stock/edit.html
   */
  @PostMapping(value = "/article_stock/edit/{id}")
  public String editArticleStock(
      @PathVariable int id,
      @Valid @ModelAttribute("articleStockDto")ArticleStockDto dto, BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails, HttpSession httpSession){

    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());

    int userId = customUserDetails.getId();
    if(id == 0){
      if(dto.isUrlEmpty()){
        result.rejectValue("url", "url.empty", "URLを入力してください");
      }else{
        ArticleStock articleStock = dto.toArticleStock(userId);
        articleStockService.registerArticleStock(articleStock);
        id = articleStock.getId();
        userProgressService.updateUserProgress(userId);
        httpSession.setAttribute("hasNewRecord", true);
      }
    }else{ // TODO: 削除機能は内容の空欄判断ではなく、一覧画面で削除ボタンをつけるようにしたい
      if(dto.isUrlEmpty()){
        articleStockService.deleteArticleStock(id);
        articleTagJunctionService.deleteRelationByActionId(id);
        httpSession.setAttribute("hasNewRecord", true);
      }else{
        articleStockService.updateArticleStock(dto.toArticleStockWithId(userId));
      }
    }
    return "redirect:/article_stock";
  }

  @DeleteMapping(value = "/article_stock/{id}")
  public String deleteStock(@PathVariable int id, HttpSession httpSession) {

    articleTagJunctionService.deleteRelationByActionId(id);
    articleStockService.deleteArticleStock(id);
    httpSession.setAttribute("hasNewRecord", true);

    return "redirect:/article_stock";
  }
}
