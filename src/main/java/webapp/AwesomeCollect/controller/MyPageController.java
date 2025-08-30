package webapp.AwesomeCollect.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.user.UserBasicInfoDto;
import webapp.AwesomeCollect.dto.user.UserInfoDto;
import webapp.AwesomeCollect.dto.user.UserPasswordDto;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.exception.IncorrectPasswordException;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.UserInfoService;

/**
 * マイページのコントローラークラス。
 */
@Controller
public class MyPageController {

  private final UserInfoService userInfoService;
  private final MessageUtil messageUtil;

  public MyPageController(UserInfoService userInfoService, MessageUtil messageUtil) {
    this.userInfoService = userInfoService;
    this.messageUtil = messageUtil;
  }

  // ユーザー登録情報を表示
  @GetMapping(value = "/mypage")
  public String showUserBasicInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

    prepareUserInfoView(customUserDetails, model);
    return "/mypage";
  }

  // ユーザー登録情報の編集フォームを表示
  @GetMapping(value = "/mypage/edit")
  public String showUserBasicInfoEditForm(
      @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model){

    prepareUserInfoView(customUserDetails, model);
    return "/mypage/edit";
  }


  private void prepareUserInfoView(
      CustomUserDetails customUserDetails, Model model) {

    UserBasicInfoDto basicInfoDto =
        userInfoService.prepareUserInfoDto(customUserDetails.getId());
    model.addAttribute("basicInfoDto", basicInfoDto);
  }

  // パスワード変更フォームを表示
  @GetMapping(value = "/mypage/change_password")
  public String showPasswordChangeForm(Model model){
    model.addAttribute("passwordDto", new UserPasswordDto());
    return "/mypage/change_password";
  }

  /**
   * 入力されたデータを確認し、ユーザーの基本情報を更新する。<br>
   * バインディングエラーまたはDB更新時の例外が発生した場合はエラーメッセージを表示し、
   * そうでない場合はマイページに遷移し、サクセスメッセージを表示する。
   *
   * @param dto ユーザーの基本情報のデータオブジェクト
   * @param result  バインディングの結果
   * @param customUserDetails カスタムユーザー情報
   * @param redirectAttributes  リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @return  マイページ
   */
  @PostMapping(value = "/mypage/edit")
  public String updateUserBasicInfo(
      @Valid @ModelAttribute("basicInfoDto") UserBasicInfoDto dto,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    if(result.hasErrors()){
      return "/mypage/edit";
    }

    try{
      userInfoService.updateUserInfo(dto, customUserDetails.getId());
    }catch(DuplicateException ex) {
        result.rejectValue(
            ex.getType().getFieldName(), "duplicate",
            messageUtil.getMessage(ex.getType().getMessageKey()));

      return "/mypage/edit";
    }

    redirectAttributes.addFlashAttribute(
        "successMessage", messageUtil.getMessage("userInfo.edit.success"));

    return "redirect:/mypage";
  }

  /**
   * 入力されたデータを確認し、パスワードを変更する。<br>
   * バインディングエラーまたはパスワード照合エラーが発生した場合はエラーメッセージを表示し、
   * そうでない場合はログアウトしてログインページに遷移し、サクセスメッセージを表示する。
   *
   * @param dto パスワード情報を扱うデータオブジェクト
   * @param result  バインディングの結果
   * @param customUserDetails カスタムユーザー情報
   * @param redirectAttributes  リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @param request クライアントからサーバーに送られたリクエスト情報を保持するオブジェクト
   * @return  ログインページ
   */
  @PostMapping(value = "/mypage/change_password")
  public String changePassword(
      @Valid @ModelAttribute("passwordDto") UserPasswordDto dto, BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes, HttpServletRequest request) {

    if(result.hasErrors()){
      return "/mypage/change_password";
    }

    // 現在のパスワードには@NotBlankをつけていないので念のため確認し、空欄の場合はエラーに追加
    if(dto.isBlankCurrentPassword()){
      result.rejectValue("currentPassword", "currentPassword.blank");
      return "mypage/change_password";
    }

    // パスワードと確認用パスワードが一致しない場合はエラーに追加
    if(dto.isPasswordMismatch()){
      result.rejectValue(
          "password", "mismatchPassword",
          messageUtil.getMessage("password.mismatch"));
      result.rejectValue(
          "confirmPassword", "mismatchPassword",
          messageUtil.getMessage("password.mismatch"));

      return "/mypage/change_password";
    }

    try{
      userInfoService.updatePassword(dto, customUserDetails.getId());
    }catch(IncorrectPasswordException ex){
      result.rejectValue(
          ex.getFieldName(), ex.getMessageKey(),
          messageUtil.getMessage(ex.getMessageKey()));

      return "/mypage/change_password";
    }

    // セキュリティ情報を消去し、セッションを破棄してログアウトする
    SecurityContextHolder.clearContext();
    request.getSession().invalidate();

    redirectAttributes.addFlashAttribute(
        "successMessage",
        messageUtil.getMessage("password.change.success"));

    return "redirect:/login";
  }
}