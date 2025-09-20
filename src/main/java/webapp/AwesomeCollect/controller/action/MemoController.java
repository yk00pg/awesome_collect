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
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.MemoRequestDto;
import webapp.AwesomeCollect.dto.action.MemoResponseDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.action.MemoService;
import webapp.AwesomeCollect.service.TagService;

/**
 * メモのコントローラークラス。
 */
@Controller
public class MemoController {

  private final MemoService memoService;
  private final TagService tagService;
  private final MessageUtil messageUtil;

  public MemoController(
      MemoService memoService, TagService tagService, MessageUtil messageUtil){

    this.memoService = memoService;
    this.tagService = tagService;
    this.messageUtil = messageUtil;
  }

  // メモの一覧ページ（メモリスト）を表示する。
  @GetMapping(ViewNames.MEMO_PAGE)
  public String showMemo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(
        AttributeNames.MEMO_RESPONSE_DTO_LIST,
        memoService.prepareResponseDtoList(customUserDetails.getId()));

    return ViewNames.MEMO_PAGE;
  }

  // メモの詳細ページを表示する。
  @GetMapping(ViewNames.MEMO_DETAIL_BY_ID)
  public String showMemoDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    MemoResponseDto memoResponseDto =
        memoService.prepareResponseDto(id, customUserDetails.getId());

    if (memoResponseDto == null) {
      return RedirectUtil.redirectView(ViewNames.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.MEMO_RESPONSE_DTO, memoResponseDto);
      return ViewNames.MEMO_DETAIL_PAGE;
    }
  }

  // メモの編集ページを表示する。
  @GetMapping(ViewNames.MEMO_EDIT_BY_ID)
  public String showMemoForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();
    MemoRequestDto memoRequestDto = memoService.prepareRequestDto(id, userId);

    if(memoRequestDto == null){
      return RedirectUtil.redirectView(ViewNames.ERROR_NOT_ACCESSIBLE);
    }else{
      model.addAttribute(AttributeNames.MEMO_REQUEST_DTO, memoRequestDto);
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.prepareTagListByUserId(userId));

      return ViewNames.MEMO_EDIT_PAGE;
    }
  }

  /**
   * 入力されたデータにバインディングエラーが発生した場合はタグリストを詰め直して
   * 編集ページに戻り、そうでない場合はDBに保存（登録・更新）し、
   * 詳細ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
   * 
   * @param id  メモID
   * @param dto メモのデータオブジェクト
   * @param result  バインディングの結果
   * @param model データをViewに渡すオブジェクト
   * @param customUserDetails カスタムユーザー情報
   * @param redirectAttributes  リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return  メモ・詳細ページ
   */
  @PostMapping(ViewNames.MEMO_EDIT_BY_ID)
  public String editMemo(
      @PathVariable int id,
      @Valid @ModelAttribute(AttributeNames.MEMO_REQUEST_DTO) MemoRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if(result.hasErrors()){
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.prepareTagListByUserId(userId));

      return ViewNames.MEMO_EDIT_PAGE;
    }

    int memoId = memoService.saveMemo(userId, dto);
    addAttributeBySaveType(id, redirectAttributes);

    return RedirectUtil.redirectView(ViewNames.MEMO_DETAIL_PAGE, memoId);
  }

  // 新規登録か更新かを判定してサクセスメッセージを表示する。
  private void addAttributeBySaveType(int id, RedirectAttributes redirectAttributes) {
    boolean isRegistration = id == 0;
    if(isRegistration){
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.MEMO_AWESOME));
    }else{
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));
    }
  }

  // 指定のIDの目標を削除して一覧ページにリダイレクトする。
  @DeleteMapping(ViewNames.MEMO_DETAIL_BY_ID)
  public String deleteMemo(
      @PathVariable int id, RedirectAttributes redirectAttributes) {

    memoService.deleteMemo(id);
    
    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.MEMO_PAGE);
  }
}
