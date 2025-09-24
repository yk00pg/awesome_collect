package webapp.AwesomeCollect.common.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * 文字列をHTMLに変換するクラス。
 */
public final class MarkdownConverter {

  // ソフトブレークを改行とみなす
  private static final MutableDataSet options = new MutableDataSet()
      .set(HtmlRenderer.SOFT_BREAK, "<br />\n");

  private static final Parser parser = Parser.builder(options).build();
  private static final HtmlRenderer renderer = HtmlRenderer.builder(options).build();

  // リンク、見出し・段落、表は許容
  private static final PolicyFactory policy = Sanitizers.FORMATTING
      .and(Sanitizers.LINKS)
      .and(Sanitizers.BLOCKS)
      .and(Sanitizers.TABLES);

  /**
   * 文字列を安全なHTMLに変換する。
   *
   * @param markdown  マークダウン記法で書かれた文字列
   * @return  変換済みのHTML
   */
  public static String toSafeHtml(String markdown){
    if(markdown == null || markdown.isBlank()){
      return "";
    }
    String replaced = sanitizeImages(markdown);
    String rawHtml = renderer.render(parser.parse(replaced));
    return policy.sanitize(rawHtml);
  }

  // 画像はそのまま表示せず、文字列に変換して表示する。
  private static String sanitizeImages(String markdown){
    Pattern imgPattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
    Matcher matcher = imgPattern.matcher(markdown);
    return matcher.replaceAll(result -> {
      String alt = result.group(1).isBlank() ? "[画像]" : "[" + result.group(1) + "]";
      return alt + " (" + result.group(2) + ")";
    });
  }
}
