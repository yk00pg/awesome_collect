package webapp.AwesomeCollect.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
import webapp.AwesomeCollect.dto.user.UserBasicInfoDto;
import webapp.AwesomeCollect.dto.user.UserPasswordDto;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.exception.IncorrectPasswordException;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.UserInfoService;
import webapp.AwesomeCollect.validation.MyPageValidator;

/**
 * マイページのコントローラークラス。
 */
@Controller
public class MyPageController {

  private final UserInfoService userInfoService;
  private final MyPageValidator myPageValidator;
  private final MessageUtil messageUtil;

  public MyPageController(
      UserInfoService userInfoService, MyPageValidator myPageValidator,
      MessageUtil messageUtil) {

    this.userInfoService = userInfoService;
    this.myPageValidator = myPageValidator;
    this.messageUtil = messageUtil;
  }

  // ユーザー登録情報を表示
  @GetMapping(ViewNames.MY_PAGE)
  public String showUserBasicInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

    prepareUserInfoView(customUserDetails, model);
    return ViewNames.MY_PAGE;
  }

  // ユーザー登録情報の編集フォームを表示
  @GetMapping(ViewNames.MY_PAGE_EDIT)
  public String showUserBasicInfoEditForm(
      @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model){

    prepareUserInfoView(customUserDetails, model);
    return ViewNames.MY_PAGE_EDIT;
  }

  private void prepareUserInfoView(
      CustomUserDetails customUserDetails, Model model) {

    UserBasicInfoDto basicInfoDto =
        userInfoService.prepareUserInfoDto(customUserDetails.getId());
    model.addAttribute(AttributeNames.BASIC_INFO_DTO, basicInfoDto);
  }

  // パスワード変更フォームを表示
  @GetMapping(ViewNames.MY_PAGE_CHANGE_PASSWORD)
  public String showPasswordChangeForm(Model model){
    model.addAttribute(AttributeNames.PASSWORD_DTO, new UserPasswordDto());
    return ViewNames.MY_PAGE_CHANGE_PASSWORD;
  }

  @InitBinder(AttributeNames.PASSWORD_DTO)
  public void initBinder(WebDataBinder dataBinder){
    dataBinder.addValidators(myPageValidator);
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
  @PostMapping(ViewNames.MY_PAGE_EDIT)
  public String updateUserBasicInfo(
      @Valid @ModelAttribute(AttributeNames.BASIC_INFO_DTO) UserBasicInfoDto dto,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    if(result.hasErrors()){
      return ViewNames.MY_PAGE_EDIT;
    }

    // ログインIDまたはメールアドレスが重複している場合はエラーに追加
    try{
      userInfoService.updateUserInfo(dto, customUserDetails.getId());
    }catch(DuplicateException ex) {
        result.rejectValue(
            ex.getType().getFieldName(), "duplicate",
            messageUtil.getMessage(ex.getType().getMessageKey()));

      return ViewNames.MY_PAGE_EDIT;
    }

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.USERINFO_EDIT_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.MY_PAGE);
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
  @PostMapping(ViewNames.MY_PAGE_CHANGE_PASSWORD)
  public String changePassword(
      @Valid @ModelAttribute(AttributeNames.PASSWORD_DTO) UserPasswordDto dto,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes, HttpServletRequest request) {

    if(result.hasErrors()){
      return ViewNames.MY_PAGE_CHANGE_PASSWORD;
    }

    // 現在のパスワードが間違っている場合はエラーに追加
    try{
      userInfoService.updatePassword(dto, customUserDetails.getId());
    }catch(IncorrectPasswordException ex){
      result.rejectValue(
          ex.getFieldName(), ex.getMessageKey(),
          messageUtil.getMessage(ex.getMessageKey()));

      return ViewNames.MY_PAGE_CHANGE_PASSWORD;
    }

    SecurityContextHolder.clearContext();
    request.getSession().invalidate();

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.PASSWORD_CHANGE_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.LOGIN_PAGE);
  }
}