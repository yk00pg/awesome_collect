package webapp.AwesomeCollect.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.user.UserInfoDto;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.service.user.UserInfoService;
import webapp.AwesomeCollect.validation.SignupValidator;

/**
 * サインアップページのコントローラークラス。
 */

@Controller
public class SignupController {

  private final UserInfoService userInfoService;
  private final SignupValidator signupValidator;
  private final MessageUtil messageUtil;

  public SignupController(
      UserInfoService userInfoService, SignupValidator signupValidator,
      MessageUtil messageUtil){

    this.userInfoService = userInfoService;
    this.signupValidator = signupValidator;
    this.messageUtil = messageUtil;
  }

  // 新規登録フォームを表示する。
  @GetMapping(ViewNames.SIGNUP_PAGE)
  public String showSignUpForm(Model model){
    model.addAttribute(AttributeNames.USER_INFO_DTO, new UserInfoDto());
    return ViewNames.SIGNUP_PAGE;
  }

  // DTOのアノテーションで制御できないバリデーションを確認する。
  @InitBinder(AttributeNames.USER_INFO_DTO)
  public void initBinder(WebDataBinder dataBinder){
    dataBinder.addValidators(signupValidator);
  }

  /**
   * 入力されたデータにバインディングエラーまたはDB登録時の例外が発生した場合はサインアップページに戻り、
   * そうでない場合はDBに登録してログインページに遷移し、サクセスメッセージを表示する。
   *
   * @param dto ユーザー情報を扱うデータオブジェクト
   * @param result  バインディングの結果
   * @param redirectAttributes  リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @return  ログインページ
   */
  @PostMapping(ViewNames.SIGNUP_PAGE)
  public String registerUserInfo(
      @Valid @ModelAttribute(AttributeNames.USER_INFO_DTO) UserInfoDto dto,
      BindingResult result, RedirectAttributes redirectAttributes) {

    if(result.hasErrors()){
      return ViewNames.SIGNUP_PAGE;
    }

    try {
      userInfoService.registerNewUser(dto);
    }catch(DuplicateException ex){
      result.rejectValue(
          ex.getType().getCompositeFiledName(),
          ex.getType().getCompositeFiledName(),
          messageUtil.getMessage(ex.getType().getMessageKey()));

      return ViewNames.SIGNUP_PAGE;
    }

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.SIGNUP_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.LOGIN_PAGE);
  }
}
