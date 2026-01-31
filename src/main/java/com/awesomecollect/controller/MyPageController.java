package com.awesomecollect.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.awesomecollect.common.constant.AttributeNames;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.MessageKeys;
import com.awesomecollect.common.constant.TemplateNames;
import com.awesomecollect.common.util.MessageUtil;
import com.awesomecollect.common.util.RedirectUtil;
import com.awesomecollect.dto.user.UserBasicInfoDto;
import com.awesomecollect.dto.user.UserPasswordDto;
import com.awesomecollect.exception.DuplicateException;
import com.awesomecollect.exception.IncorrectPasswordException;
import com.awesomecollect.security.CustomUserDetails;
import com.awesomecollect.service.user.DeleteUserDataService;
import com.awesomecollect.service.user.UserInfoService;
import com.awesomecollect.validator.MyPageValidator;

/**
 * マイページのコントローラークラス。
 */
@Controller
@RequiredArgsConstructor
public class MyPageController {

  private final UserInfoService userInfoService;
  private final MyPageValidator myPageValidator;
  private final MessageUtil messageUtil;
  private final DeleteUserDataService deleteUserDataService;

  // ユーザー登録情報を表示する。
  @GetMapping(MappingValues.MY_PAGE)
  public String showUserBasicInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.BASIC_INFO_DTO,
        userInfoService.prepareUserInfoDto(customUserDetails.getId()));

    return TemplateNames.MY_PAGE;
  }

  // ユーザー登録情報の編集フォームを表示する。
  @GetMapping(MappingValues.MY_PAGE_EDIT)
  public String showUserBasicInfoEditForm(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.BASIC_INFO_DTO,
        userInfoService.prepareUserInfoDto(customUserDetails.getId()));

    return TemplateNames.MY_PAGE_EDIT;
  }

  // パスワード変更フォームを表示する。
  @GetMapping(MappingValues.MY_PAGE_CHANGE_PASSWORD)
  public String showPasswordChangeForm(Model model) {
    model.addAttribute(AttributeNames.PASSWORD_DTO, new UserPasswordDto());
    return TemplateNames.MY_PAGE_CHANGE_PASSWORD;
  }

  // DTOアノテーションで制御できないバリデーションを確認する。
  @InitBinder(AttributeNames.PASSWORD_DTO)
  public void initBinder(WebDataBinder dataBinder) {
    dataBinder.addValidators(myPageValidator);
  }

  /**
   * 入力されたデータにバインディングエラーまたは例外が発生した場合は編集ページに戻って
   * エラーメッセージを表示し、そうでない場合はDBを更新し、マイページに遷移して
   * サクセスメッセージを表示する。
   *
   * @param dto                ユーザーの基本情報のデータオブジェクト
   * @param result             バインディングの結果
   * @param customUserDetails  カスタムユーザー情報
   * @param redirectAttributes リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @return マイページ
   */
  @PostMapping(MappingValues.MY_PAGE_EDIT)
  public String updateUserBasicInfo(
      @Valid @ModelAttribute(AttributeNames.BASIC_INFO_DTO) UserBasicInfoDto dto,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if (result.hasErrors() || userId == 1) {
      return TemplateNames.MY_PAGE_EDIT;
    }

    try {
      userInfoService.updateUserInfo(dto, customUserDetails.getId());
    } catch (DuplicateException ex) {
      result.rejectValue(
          ex.getType().getFieldName(), "duplicate",
          messageUtil.getMessage(ex.getType().getMessageKey()));

      return TemplateNames.MY_PAGE_EDIT;
    }

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.USERINFO_EDIT_SUCCESS));

    return RedirectUtil.redirectView(MappingValues.MY_PAGE);
  }

  /**
   * 入力されたデータにバインディングエラーまたは例外が発生した場合は変更ページに戻って
   * エラーメッセージを表示し、そうでない場合はDBを更新し、セッションを削除してログアウトし、
   * ログインページに遷移してサクセスメッセージを表示する。
   *
   * @param dto                パスワード情報を扱うデータオブジェクト
   * @param result             バインディングの結果
   * @param customUserDetails  カスタムユーザー情報
   * @param redirectAttributes リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @param request            クライアントからサーバーに送られたリクエスト情報を保持するオブジェクト
   * @return ログインページ
   */
  @PostMapping(MappingValues.MY_PAGE_CHANGE_PASSWORD)
  public String changePassword(
      @Valid @ModelAttribute(AttributeNames.PASSWORD_DTO) UserPasswordDto dto,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes, HttpServletRequest request) {

    int userId = customUserDetails.getId();

    if (result.hasErrors() || userId == 0) {
      return TemplateNames.MY_PAGE_CHANGE_PASSWORD;
    }

    try {
      userInfoService.updatePassword(dto, customUserDetails.getId());
    } catch (IncorrectPasswordException ex) {
      result.rejectValue(
          ex.getFieldName(), ex.getMessageKey(),
          messageUtil.getMessage(ex.getMessageKey()));

      return TemplateNames.MY_PAGE_CHANGE_PASSWORD;
    }

    SecurityContextHolder.clearContext();
    request.getSession().invalidate();

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.PASSWORD_CHANGE_SUCCESS));

    return RedirectUtil.redirectView(MappingValues.LOGIN);
  }

  // アカウントおよび登録データをすべて削除してログアウトし、ログイン画面に遷移する。
  @DeleteMapping(MappingValues.DELETE_ACCOUNT)
  public String deleteAccount(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      HttpServletRequest request){

    deleteUserDataService.deleteUserData(customUserDetails.getId());
    SecurityContextHolder.clearContext();
    request.getSession().invalidate();

    return RedirectUtil.redirectView(MappingValues.LOGIN);
  }
}