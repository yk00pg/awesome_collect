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
import webapp.AwesomeCollect.common.constant.MappingValues;

/**
 * Spring Securityのセキュリティ設定を管理するクラス。<br>
 * ログイン認証をログインIDに変更するなどのカスタマイズ、認証後の遷移ページ、アクセス制御の設定を行う。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final GuestLogoutSuccessHandler logoutSuccessHandler;
  private final CustomUserDetailsService customUserDetailsService;

  private static final String CSS = "/css/**";
  private static final String IMAGE = "/image/**";
  private static final String JS = "/js/**";

  private static final String LOGIN_ID = "loginId";

  public SecurityConfig(
      GuestLogoutSuccessHandler logoutSuccessHandler,
      CustomUserDetailsService customUserDetailsService) {

    this.logoutSuccessHandler = logoutSuccessHandler;
    this.customUserDetailsService = customUserDetailsService;
  }

  // ログイン認証をカスタマイズする。
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authenticationProvider(daoAuthenticationProvider())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(CSS, IMAGE, JS).permitAll()
            .requestMatchers(MappingValues.LOGIN, MappingValues.GUEST_LOGIN, MappingValues.SIGNUP).permitAll()
            .anyRequest().authenticated())
        .formLogin(login -> login
            .loginPage(MappingValues.LOGIN)
            .loginProcessingUrl(MappingValues.LOGIN)
            .usernameParameter(LOGIN_ID)
            .failureUrl(MappingValues.LOGIN_ERROR)
            .defaultSuccessUrl(MappingValues.TOP))
        .logout(logout -> logout
            .logoutSuccessHandler(logoutSuccessHandler)
            .logoutUrl(MappingValues.LOGOUT)
            .logoutSuccessUrl(MappingValues.LOGIN));
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
