package webapp.AwesomeCollect.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.dto.user.UserBasicInfoDto;
import webapp.AwesomeCollect.dto.user.UserInfoDto;
import webapp.AwesomeCollect.dto.user.UserPasswordDto;
import webapp.AwesomeCollect.entity.user.UserInfo;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.exception.DuplicateType;
import webapp.AwesomeCollect.exception.IncorrectPasswordException;
import webapp.AwesomeCollect.repository.user.UserInfoRepository;
import webapp.AwesomeCollect.security.SecurityConfig;

/**
 * ユーザー情報のサービスクラス。
 */
@Service
public class UserInfoService {

  private final UserInfoRepository userInfoRepository;
  private final SecurityConfig securityConfig;
  private final UserProgressService userProgressService;

  public UserInfoService(
      UserInfoRepository userInfoRepository, SecurityConfig securityConfig,
      UserProgressService userProgressService) {

    this.userInfoRepository = userInfoRepository;
    this.securityConfig = securityConfig;
    this.userProgressService = userProgressService;
  }

  /**
   * ユーザーIDを基にユーザー情報を取得し、基本情報データオブジェクトに変換する。
   *
   * @param id ユーザーID
   * @return ユーザー基本情報データオブジェクト
   */
  public UserBasicInfoDto prepareUserInfoDto(int id) {
    return UserBasicInfoDto.fromEntity(userInfoRepository.findUserInfoById(id));
  }

  /**
   * DTOをエンティティに変換し、ログインIDまたはメールアドレスが重複している場合は例外を投げ、
   * そうでない場合はDBに登録して進捗管理データを作成する。
   *
   * @param dto ユーザー情報統合データオブジェクト
   * @throws DuplicateException ログインIDまたはメールアドレスが重複している場合
   */
  @Transactional
  public void registerNewUser(UserInfoDto dto)
      throws DuplicateException {

    UserInfo userInfo = dto.toEntityForSignup(securityConfig.passwordEncoder());
    // 新規ユーザのためid=0
    if (isDuplicateLoginId(userInfo.getLoginId(), 0)) {
      throw new DuplicateException(DuplicateType.USER_ID);
    }

    if (userInfo.getEmail() != null) {
      if (isDuplicateEmail(userInfo.getEmail(), 0)) {
        throw new DuplicateException(DuplicateType.EMAIL);
      }
    }

    userInfoRepository.registerNewUserInfo(userInfo);
    userProgressService.createUserProgress(userInfo.getId());
  }

  /**
   * DTOをエンティティに変換し、ログインIDまたはメールアドレスが重複している場合は例外を投げ、
   * そうでない場合はDBのレコードを更新する。
   *
   * @param dto ユーザー基本情報データオブジェクト
   * @param id  ユーザーID
   * @throws DuplicateException ログインIDまたはメールアドレスが重複している場合
   */
  @Transactional
  public void updateUserInfo(UserBasicInfoDto dto, int id)
      throws DuplicateException {

    UserInfo userInfo = dto.toEntityForUpdate(id);
    if (isDuplicateLoginId(userInfo.getLoginId(), id)) {
      throw new DuplicateException(DuplicateType.USER_ID);
    }

    if (userInfo.getEmail() != null) {
      if (isDuplicateEmail(userInfo.getEmail(), id)) {
        throw new DuplicateException(DuplicateType.EMAIL);
      }
    }

    userInfoRepository.updateUserInfo(userInfo);
  }

  // ログインIDが重複しているか確認する。
  private boolean isDuplicateLoginId(String loginId, int id) {
    Integer recordId = userInfoRepository.findIdByLoginId(loginId);
    return recordId != null && !recordId.equals(id);
  }

  // メールアドレスが重複しているか確認する。
  private boolean isDuplicateEmail(String email, int id) {
    Integer recordId = userInfoRepository.findIdByEmail(email);
    return recordId != null && !recordId.equals(id);
  }

  /**
   * 入力された現在のパスワードがDBに登録されているパスワードと不一致の場合は例外を投げ、
   * そうでない場合はエンティティに変換してDBのレコードを更新する。
   *
   * @param dto ユーザーパスワード情報データオブジェクト
   * @param id  ユーザーID
   * @throws IncorrectPasswordException 入力された現在のパスワードと登録されているパスワードが不一致の場合
   */
  @Transactional
  public void updatePassword(UserPasswordDto dto, int id)
      throws IncorrectPasswordException {

    if (isPasswordIncorrect(dto, id)) {
      throw new IncorrectPasswordException();
    }

    UserInfo userInfo =
        dto.toEntityForUpdate(id, securityConfig.passwordEncoder());
    userInfoRepository.updatePassword(userInfo);
  }

  // 現在のパスワードが間違っているか確認する。
  private boolean isPasswordIncorrect(UserPasswordDto dto, int id) {
    String inputPassword = dto.getCurrentPassword();
    String currentPassword = userInfoRepository.findUserInfoById(id).getPassword();
    return (!securityConfig.passwordEncoder().matches(inputPassword, currentPassword));
  }
}
