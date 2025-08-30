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
import webapp.AwesomeCollect.common.TaggingManager;
import webapp.AwesomeCollect.common.ActionViewPreparator;
import webapp.AwesomeCollect.dto.action.MemoDto;
import webapp.AwesomeCollect.entity.action.Memo;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.action.MemoService;
import webapp.AwesomeCollect.service.junction.MemoTagJunctionService;
import webapp.AwesomeCollect.service.TagService;

@Controller
public class MemoController {

  private final MemoService memoService;
  private final TagService tagService;
  private final ActionViewPreparator actionViewPreparator;
  private final MemoTagJunctionService memoTagJunctionService;
  private final TaggingManager taggingManager;
  private final UserProgressService userProgressService;

  public MemoController(
      MemoService memoService, TagService tagService, ActionViewPreparator actionViewPreparator,
      MemoTagJunctionService memoTagJunctionService, TaggingManager taggingManager,
      UserProgressService userProgressService){

    this.memoService = memoService;
    this.tagService = tagService;
    this.actionViewPreparator = actionViewPreparator;
    this.memoTagJunctionService = memoTagJunctionService;
    this.taggingManager = taggingManager;
    this.userProgressService = userProgressService;
  }

  /**
   * 「メモ」の一覧画面を表示する。<br>
   * ログイン中のユーザーのユーザーIDを取得し、ユーザーIDを基にDBからメモリストを取得する。<br>
   * メモリストが空（未登録）の場合は空のデータオブジェクトをモデルに追加し、
   * そうでない場合は登録済みデータを入れたデータオブジェクトをモデルに追加する。
   *
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「メモ」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  memo.html
   */
  @GetMapping(value = "/memo")
  public String showMemo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      MemoDto dto, Model model){

    int userId = customUserDetails.getId();
    List<Memo> memoList = memoService.searchMemo(userId);
    if(!memoList.isEmpty()){
      actionViewPreparator.prepareCurrentDataListView(
          dto, model, memoList, memoTagJunctionService);
    }
    return "/memo";
  }

  /**
   * ID別の「メモ」の詳細画面を表示する。
   *
   * @param id  メモID
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「メモ」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  memo/detail.html
   * @throws AccessDeniedException  登録していないIDのページを開こうとした場合
   */
  @GetMapping(value = "memo/detail/{id}")
  public String showMemoDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      MemoDto dto, Model model) throws AccessDeniedException {

    showMemoDetailView(id, customUserDetails, dto, model);
    return "memo/detail";
  }

  /**
   * ID別の「メモ」の編集画面を表示する。
   *
   * @param id  メモID
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「メモ」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  memo/detail/edit.html
   * @throws AccessDeniedException  登録していないIDのページを開こうとした場合
   */
  @GetMapping(value = "memo/detail/edit/{id}")
  public String showMemoForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      MemoDto dto, Model model) throws AccessDeniedException {

    showMemoDetailView(id, customUserDetails, dto, model);
    return "memo/detail/edit";
  }

  /**
   * ログイン中のユーザーのユーザーIDを取得し、ユーザーIDを基にDBからタグリストを取得する。<br>
   * メモIDが0（新規登録）の場合は空のデータオブジェクトとタグリストをモデルに追加し、
   * そうでない場合は、目標IDとユーザーIDを基にDBを検索し、レコードがない場合は例外処理をする。<br>
   * レコードがある場合は登録済みデータを入れたデータオブジェクトとタグリストをモデルに追加する。
   *
   * @param id  メモID
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「メモ」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @throws AccessDeniedException  登録していないIDのページを開こうとした場合
   */
  private void showMemoDetailView(
      int id, CustomUserDetails customUserDetails,
      MemoDto dto, Model model) throws AccessDeniedException {

    int userId = customUserDetails.getId();
    List<String> tagNameList = tagService.getTagNameList(userId);

    if(id == 0){
      actionViewPreparator.prepareBlankDoneView(id, dto, model, tagNameList);
    }else{
      Memo memo = memoService.findMemoByIds(id,userId);
      if (memo == null){
        throw new AccessDeniedException("不正なアクセスです");
      }else{
        actionViewPreparator.prepareCurrentDataView(
            dto, model, memo, tagNameList, memoTagJunctionService);
      }
    }
  }

  @PostMapping(value = "/memo/detail/edit/{id}")
  public String editMemo(
      @PathVariable int id,
      @Valid @ModelAttribute("memoDto") MemoDto dto, BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails, HttpSession httpSession) {

    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());

    int userId = customUserDetails.getId();
    if(id == 0){
      if(dto.isTitleEmpty()){
        result.rejectValue("title", "title.empty", "タイトルを入力してください");
      }
      Memo memo = dto.toMemo(userId);
      memoService.registerMemo(memo);
      id = memo.getId();
      taggingManager.resolveTagsAndRelations(
          id, pureTagList, userId, MemoTagJunction::new, memoTagJunctionService);
      userProgressService.updateUserProgress(userId);
      httpSession.setAttribute("hasNewRecord", true);
    }else{ // TODO: 削除機能は内容の空欄判断ではなく、詳細画面で削除ボタンをつけるようにしたい
      if(dto.isTitleEmpty()){
        memoService.deleteMemo(id);
        memoTagJunctionService.deleteRelationByActionId(id);
        httpSession.setAttribute("hasNewRecord", true);
        return "redirect:/memo";
      }else{
        taggingManager.updateTagsAndRelations(
            id, pureTagList, userId, MemoTagJunction::new, memoTagJunctionService);
      }
    }
    return "redirect:/memo/detail/" + id;
  }

  @DeleteMapping(value = "/memo/detail/{id}")
  public String deleteMemo(@PathVariable int id, HttpSession httpSession) {

    memoTagJunctionService.deleteRelationByActionId(id);
    memoService.deleteMemo(id);
    httpSession.setAttribute("hasNewRecord", true);

    return "redirect:/memo";
  }
}
