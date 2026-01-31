package webapp.AwesomeCollect.common.util;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * メッセージキーを基にメッセージを取得するクラス。
 */
@Component
public class MessageUtil {

  private final MessageSource messageSource;

  public MessageUtil(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public String getMessage(String key, Object[] args, Locale locale) {
    return messageSource.getMessage(key, args, locale);
  }

  public String getMessage(String key, Locale locale) {
    return getMessage(key, null, locale);
  }

  public String getMessage(String key) {
    return getMessage(key, Locale.getDefault());
  }
}
