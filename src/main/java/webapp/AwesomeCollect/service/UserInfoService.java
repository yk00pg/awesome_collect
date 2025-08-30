package webapp.AwesomeCollect.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.dto.user.UserBasicInfoDto;
import webapp.AwesomeCollect.dto.user.UserInfoDto;
import webapp.AwesomeCollect.dto.user.UserPasswordDto;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.exception.DuplicateType;
import webapp.AwesomeCollect.entity.UserInfo;
import webapp.AwesomeCollect.exception.IncorrectPasswordException;
import webapp.AwesomeCollect.repository.UserInfoRepository;
import webapp.AwesomeCollect.security.SecurityConfig;

/**
 * ユーザー情報を扱うサービスクラス。
 */
@Service
public class UserInfoService {

  private final UserInfoRepository userInfoRepository;
  private final SecurityConfig securityConfig;
  private final UserProgressService userProgressService;

  public UserInfoService(
      UserInfoRepository userInfoRepository, SecurityConfig securityConfig,
      UserProgressService userProgressService){

    this.userInfoRepository= userInfoRepository;
    this.securityConfig = securityConfig;
    this.userProgressService = userProgressService;
  }

  // IDでユーザ情報を検索
  public UserInfo findUserInfoById(int id){
    return userInfoRepository.findUserInfoById(id);
  }

  // IDを基に取得したユーザー情報をデータオブジェクトに変換して返す
  public UserBasicInfoDto prepareUserInfoDto(int id){
    return UserBasicInfoDto.fromEntity(findUserInfoById(id));
  }

  /**
   * DTOをエンティティに変換し、ログインIDとメールアドレスの重複確認を行う。<br>
   * どちらかが重複している場合は例外を投げ、そうでない場合はDBに登録し、進捗管理データを作成する。
   *
   * @param dto ユーザー情報のデータオブジェクト
   * @throws DuplicateException ログインIDまたはメールアドレスが重複している場合
   */
  @Transactional
  public void registerNewUser(UserInfoDto dto)
      throws DuplicateException {

    UserInfo userInfo = dto.toEntity(securityConfig.passwordEncoder());
    // 新規ユーザのためid=0として重複確認
    checkDuplication(userInfo, 0);

    userInfoRepository.registerNewUserInfo(userInfo);
    userProgressService.createUserProgress(userInfo.getId());
  }

  /**
   * DTOをエンティティに変換し、ログインIDとメールアドレスの重複確認を行う。<br>
   * どちらかが重複している場合は例外を投げ、そうでない場合は更新処理を行う。
   *
   * @param dto ユーザーの基本情報を扱うデータオブジェクト
   * @param id  ID
   * @throws DuplicateException ログインIDまたはメールアドレスが重複している場合
   */
  @Transactional
  public void updateUserInfo(UserBasicInfoDto dto, int id)
      throws DuplicateException {

    UserInfo userInfo = dto.toEntityWithId(id);
    checkDuplication(userInfo, id);

    userInfoRepository.updateUserInfo(userInfo);
  }

  // パスワードの更新
  @Transactional
  public void updatePassword(UserPasswordDto dto, int id)
      throws IncorrectPasswordException {

    if(isPasswordIncorrect(dto, id)){
      throw new IncorrectPasswordException();
    }

    UserInfo userInfo =
        dto.toEntityWithIdAndPassword(id, securityConfig.passwordEncoder());
    userInfoRepository.updatePassword(userInfo);
  }

  // ログインIDとメールアドレスの重複を確認
  private void checkDuplication(UserInfo userInfo, int id) {
    if(isDuplicateLoginId(userInfo.getLoginId(), id)) {
      throw new DuplicateException(DuplicateType.USER_ID);
    }

    if (isDuplicateEmail(userInfo.getEmail(), id)){
      throw new DuplicateException(DuplicateType.EMAIL);
    }
  }

  // ログインIDが重複しているか確認
  private boolean isDuplicateLoginId(String loginId, int id){
    Integer recordId = userInfoRepository.findIdByLoginId(loginId);
    return recordId != null && !recordId.equals(id);
  }

  // メールアドレスが重複しているか確認
  private boolean isDuplicateEmail(String email, int id){
    Integer recordId = userInfoRepository.findIdByEmail(email);
    return recordId != null && !recordId.equals(id);
  }

  // 現在のパスワードが間違っているか確認
  private boolean isPasswordIncorrect(UserPasswordDto dto, int id){
    String inputPassword = dto.getCurrentPassword();
    String currentPassword = findUserInfoById(id).getPassword();

    return (inputPassword == null ||
        !securityConfig.passwordEncoder().matches(inputPassword, currentPassword));
  }
}
