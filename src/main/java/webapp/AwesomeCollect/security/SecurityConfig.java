package webapp.AwesomeCollect.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import webapp.AwesomeCollect.common.constant.ViewNames;

/**
 * Spring Securityのセキュリティ設定を管理するクラス。<br>
 * <p>
 * ログイン認証をログインIDに変更するなどのカスタマイズや、
 * 認証後の遷移ページ、アクセス制御の設定を行う。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;

  private static final String CSS_FILE = "/css/**";
  private static final String IMAGE_FILE = "/image/**";
  private static final String JS_FILE = "/js/**";

  private static final String LOGIN_ID = "loginId";

  public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
    this.customUserDetailsService = customUserDetailsService;
  }

  // ログイン認証をカスタマイズする。
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authenticationProvider(daoAuthenticationProvider())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(CSS_FILE, IMAGE_FILE, JS_FILE).permitAll()
            .requestMatchers(ViewNames.LOGIN_PAGE, ViewNames.GUEST_LOGIN, ViewNames.SIGNUP_PAGE).permitAll()
            .anyRequest().authenticated())
        .formLogin(login -> login
            .loginPage(ViewNames.LOGIN_PAGE)
            .loginProcessingUrl(ViewNames.LOGIN_PAGE)
            .usernameParameter(LOGIN_ID)
            .failureUrl(ViewNames.LOGIN_ERROR_PAGE)
            .defaultSuccessUrl(ViewNames.HOME_PAGE))
        .logout(logout -> logout
            .logoutUrl(ViewNames.LOGOUT_PAGE)
            .logoutSuccessUrl(ViewNames.LOGIN_PAGE));
    return httpSecurity.build();
  }

  // 認証処理のエントリーポイントを設定する。
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authConfig) throws Exception {

    return authConfig.getAuthenticationManager();
  }

  // パスワードをハッシュ化するためのエンコーダーを用意する。
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 認証を行うプロバイダに引き渡すサービスとエンコーダーを設定する。
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }
}
