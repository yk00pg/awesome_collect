package webapp.AwesomeCollect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ブラウザキャッシュを有効にする設定クラス。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private static final String IMAGE = "/static/images/";
  private static final String CSS = "/static/css/";
  private static final String JS = "/static/js/";

  private static final String ROOT_ALL_FILE = "**";
  private static final String CLASSPATH = "classpath:";

  private static final int ONE_YEAR = 31536000;
  private static final int HALF_DAY = 3600 * 12;

  // 画像は1年、CSSとJSは半日キャッシュ
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(IMAGE + ROOT_ALL_FILE)
        .addResourceLocations(CLASSPATH + IMAGE)
        .setCachePeriod(ONE_YEAR);

    registry.addResourceHandler(CSS + ROOT_ALL_FILE, JS + ROOT_ALL_FILE)
        .addResourceLocations(CLASSPATH + CSS, CLASSPATH + JS)
        .setCachePeriod(HALF_DAY);
  }
}
