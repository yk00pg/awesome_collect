package webapp.AwesomeCollect.common.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class MarkdownConverter {

  private static final MutableDataSet options = new MutableDataSet()
      .set(HtmlRenderer.SOFT_BREAK, "<br />\n");

  private static final Parser parser = Parser.builder(options).build();
  private static final HtmlRenderer renderer = HtmlRenderer.builder(options).build();

  private static final PolicyFactory policy = Sanitizers.FORMATTING
      .and(Sanitizers.LINKS)
      .and(Sanitizers.BLOCKS)
      .and(Sanitizers.TABLES);

  public static String toSafeHtml(String markdown){
    if(markdown == null || markdown.isBlank()){
      return "";
    }
    String replaced = sanitizeImages(markdown);
    String rawHtml = renderer.render(parser.parse(replaced));
    return policy.sanitize(rawHtml);
  }

  private static String sanitizeImages(String markdown){
    Pattern imgPattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
    Matcher matcher = imgPattern.matcher(markdown);
    return matcher.replaceAll(result -> {
      String alt = result.group(1).isBlank() ? "[画像]" : "[" + result.group(1) + "]";
      return alt + " (" + result.group(2) + ")";
    });
  }
}
