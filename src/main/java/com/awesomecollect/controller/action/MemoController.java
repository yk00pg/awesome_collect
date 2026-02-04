package com.awesomecollect.controller.action;

import com.awesomecollect.common.SaveResult;
import com.awesomecollect.common.constant.AttributeNames;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.MessageKeys;
import com.awesomecollect.common.constant.TemplateNames;
import com.awesomecollect.common.util.MessageUtil;
import com.awesomecollect.common.util.RedirectUtil;
import com.awesomecollect.controller.web.SessionManager;
import com.awesomecollect.dto.action.request.MemoRequestDto;
import com.awesomecollect.dto.action.response.MemoResponseDto;
import com.awesomecollect.exception.DuplicateException;
import com.awesomecollect.security.CustomUserDetails;
import com.awesomecollect.service.TagService;
import com.awesomecollect.service.action.MemoService;
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

/**
 * メモのコントローラークラス。
 */
@Controller
public class MemoController {

  private final MemoService memoService;
  private final TagService tagService;
  private final MessageUtil messageUtil;
  private final SessionManager sessionManager;

  public MemoController(
      MemoService memoService, TagService tagService,
      MessageUtil messageUtil, SessionManager sessionManager) {

    this.memoService = memoService;
    this.tagService = tagService;
    this.messageUtil = messageUtil;
    this.sessionManager = sessionManager;
  }

  // メモの一覧ページ（メモリスト）を表示する。
  @GetMapping(MappingValues.MEMO)
  public String showMemo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.MEMO_RESPONSE_DTO_LIST,
        memoService.prepareResponseDtoListForList(customUserDetails.getId()));

    return TemplateNames.MEMO;
  }

  // メモの詳細ページを表示する。
  @GetMapping(MappingValues.MEMO_DETAIL_BY_ID)
  public String showMemoDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    MemoResponseDto memoResponseDto =
        memoService.prepareResponseDtoForDetails(id, customUserDetails.getId());

    if (memoResponseDto == null) {
      return RedirectUtil.redirectView(MappingValues.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.MEMO_RESPONSE_DTO, memoResponseDto);
      return TemplateNames.MEMO_DETAIL;
    }
  }

  // メモの編集ページを表示する。
  @GetMapping(MappingValues.MEMO_EDIT_BY_ID)
  public String showMemoForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();
    MemoRequestDto memoRequestDto = memoService.prepareRequestDtoForEdit(id, userId);

    if (memoRequestDto == null) {
      return RedirectUtil.redirectView(MappingValues.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.MEMO_REQUEST_DTO, memoRequestDto);
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

      return TemplateNames.MEMO_EDIT;
    }
  }

  /**
   * 入力されたデータにバインディングエラーまたは例外が発生した場合はタグリストを詰め直して
   * 編集ページに戻り、エラーメッセージを表示する。そうでない場合はDBに保存（登録・更新）し、
   * 詳細ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
   *
   * @param id                 メモID
   * @param dto                メモのデータオブジェクト
   * @param result             バインディングの結果
   * @param model              データをViewに渡すオブジェクト
   * @param customUserDetails  カスタムユーザー情報
   * @param redirectAttributes リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return メモ・詳細ページ
   */
  @PostMapping(MappingValues.MEMO_EDIT_BY_ID)
  public String editMemo(
      @PathVariable int id,
      @Valid @ModelAttribute(AttributeNames.MEMO_REQUEST_DTO) MemoRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if (result.hasErrors()) {
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));
      return TemplateNames.MEMO_EDIT;
    }

    SaveResult saveResult = trySaveMemo(dto, result, model, userId);
    if (saveResult == null) {
      return TemplateNames.MEMO_EDIT;
    }

    addAttributeBySaveType(id, redirectAttributes);

    return RedirectUtil.redirectView(MappingValues.MEMO_DETAIL, saveResult.id());
  }

  // DBへの保存を試みて保存結果を取得する。
  private @Nullable SaveResult trySaveMemo(
      MemoRequestDto dto, BindingResult result, Model model, int userId) {

    SaveResult saveResult;
    try {
      saveResult = memoService.saveMemo(userId, dto);
    } catch (DuplicateException ex) {
      result.rejectValue(
          ex.getType().getFieldName(), "duplicate",
          messageUtil.getMessage(ex.getType().getMessageKey("memo")));

      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

      return null;
    }
    return saveResult;
  }

  // 登録か更新かを判定してサクセスメッセージとポップアップウィンドウを表示する。
  private void addAttributeBySaveType(int id, RedirectAttributes redirectAttributes) {
    boolean isRegistration = id == 0;
    if (isRegistration) {
      sessionManager.disableCachedAwesomePoints();

      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.MEMO_AWESOME));
    } else {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));
    }
  }

  // 指定のIDのメモを削除してセッション情報を更新し、一覧ページにリダイレクトする。
  @DeleteMapping(MappingValues.MEMO_DETAIL_BY_ID)
  public String deleteMemo(
      @PathVariable int id, RedirectAttributes redirectAttributes) {

    memoService.deleteMemo(id);
    sessionManager.disableCachedAwesomePoints();

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(MappingValues.MEMO);
  }
}
