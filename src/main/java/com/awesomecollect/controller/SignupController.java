package com.awesomecollect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
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
import com.awesomecollect.dto.user.UserInfoDto;
import com.awesomecollect.exception.DuplicateException;
import com.awesomecollect.service.user.UserInfoService;
import com.awesomecollect.validator.SignupValidator;

/**
 * サインアップページのコントローラークラス。
 */
@Controller
@RequiredArgsConstructor
public class SignupController {

  private final UserInfoService userInfoService;
  private final SignupValidator signupValidator;
  private final MessageUtil messageUtil;

  // 新規登録フォームを表示する。
  @GetMapping(MappingValues.SIGNUP)
  public String showSignUpForm(Model model) {
    model.addAttribute(AttributeNames.USER_INFO_DTO, new UserInfoDto());
    return TemplateNames.SIGNUP;
  }

  // DTOのアノテーションで制御できないバリデーションを確認する。
  @InitBinder(AttributeNames.USER_INFO_DTO)
  public void initBinder(WebDataBinder dataBinder) {
    dataBinder.addValidators(signupValidator);
  }

  /**
   * 入力されたデータにバインディングエラーまたは例外が発生した場合はサインアップページに戻って
   * エラーメッセージを表示し、そうでない場合はDBに登録してログインページに遷移し、
   * サクセスメッセージを表示する。
   *
   * @param dto                ユーザー情報を扱うデータオブジェクト
   * @param result             バインディングの結果
   * @param redirectAttributes リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @return ログインページ
   */
  @PostMapping(MappingValues.SIGNUP)
  public String registerUserInfo(
      @Valid @ModelAttribute(AttributeNames.USER_INFO_DTO) UserInfoDto dto,
      BindingResult result, RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return TemplateNames.SIGNUP;
    }

    try {
      userInfoService.registerNewUser(dto);
    } catch (DuplicateException ex) {
      result.rejectValue(
          ex.getType().getCompositeFiledName(),
          ex.getType().getCompositeFiledName(),
          messageUtil.getMessage(ex.getType().getMessageKey()));

      return TemplateNames.SIGNUP;
    }

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.SIGNUP_SUCCESS));

    return RedirectUtil.redirectView(MappingValues.LOGIN);
  }
}
