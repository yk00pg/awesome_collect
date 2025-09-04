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

/**
 * Spring Securityのセキュリティ設定を管理するクラス。<br>
 *
 * ログイン認証をログインIDに変更するなどのカスタマイズや、
 * 認証後の遷移ページ、アクセス制御の設定を行う。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService){
    this.customUserDetailsService = customUserDetailsService;
  }

  // ログイン認証をカスタマイズ
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
    httpSecurity
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**", "/image/**", "/js/**").permitAll()
            .requestMatchers("/login").permitAll()
            .requestMatchers("/signup").permitAll()
            .anyRequest().authenticated())
        .formLogin(login -> login
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .usernameParameter("loginId")
            .failureUrl("/login?error")
            .defaultSuccessUrl("/home"))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login"));
    return httpSecurity.build();
  }

  // 認証処理のエントリーポイントを設定
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authConfig) throws Exception{

    return authConfig.getAuthenticationManager();
  }

  // パスワードをハッシュ化するためのエンコーダーを用意
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  // 認証を行うプロバイダに引き渡すサービスとエンコーダーを設定
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(){
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }
}
