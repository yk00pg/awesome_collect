package webapp.AwesomeCollect.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.user.UserInfoDto;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.service.UserInfoService;

/**
 * サインアップページのコントローラークラス。
 */

@Controller
public class SignupController {

  private final MessageUtil messageUtil;
  private final UserInfoService userInfoService;

  public SignupController(MessageUtil messageUtil, UserInfoService userInfoService){
    this.messageUtil = messageUtil;
    this.userInfoService = userInfoService;
  }

  // 新規登録フォームを表示
  @GetMapping(value = "/signup")
  public String showSignUpForm(Model model){
    model.addAttribute("userInfoDto", new UserInfoDto());
    return "/signup";
  }

  /**
   * 入力されたデータを確認し、ユーザー情報の新規登録を行う。<br>
   * バインディングエラーまたはDB登録時の例外が発生した場合はエラーメッセージを表示し、
   * そうでない場合はログインページに遷移し、サクセスメッセージを表示する。
   *
   * @param dto ユーザー情報を扱うデータオブジェクト
   * @param result  バインディングの結果
   * @param redirectAttributes  リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @return  ログインページ
   */
  @PostMapping(value = "/signup")
  public String registerUserInfo(
      @Valid @ModelAttribute("userInfoDto") UserInfoDto dto,
      BindingResult result, RedirectAttributes redirectAttributes) {

    if(result.hasErrors()){
      return "/signup";
    }

    // パスワードと確認用パスワードが一致しない場合はエラーに追加
    if(dto.getPasswordDto().isPasswordMismatch()){
      result.rejectValue(
          "passwordDto.password", "password.mismatch",
          messageUtil.getMessage("password.mismatch"));
      result.rejectValue(
          "passwordDto.confirmPassword", "password.mismatch",
          messageUtil.getMessage("password.mismatch"));

      return "/signup";
    }

    try {
      userInfoService.registerNewUser(dto);
    }catch(DuplicateException ex){
      result.rejectValue(
          ex.getType().getCompositeFiledName(),
          ex.getType().getCompositeFiledName(),
          messageUtil.getMessage(ex.getType().getMessageKey()));

      return "/signup";
    }

    redirectAttributes.addFlashAttribute(
        "successMessage",
        messageUtil.getMessage("signup.success"));

    return "redirect:/login";
  }
}
