package webapp.AwesomeCollect.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * CSVファイルを読み込むクラス。
 */
public class CsvLoader {

  /**
   * CSVファイルを読み込み、レコードをオブジェクトに変換してリスト化する。
   *
   * @param inputStream 外部ファイルからデータを読み込む抽象クラス
   * @param mapper CSVレコードをオブジェクトに変換するインターフェース
   * @return  オブジェクト変換後のCSVレコードリスト
   * @param <T> ジェネリクス
   */
  public static <T> List<T> load(InputStream inputStream, Function<CSVRecord, T> mapper) {
    List<T> resultList = new ArrayList<>();

    try (Reader reader = createUtf8ReaderSkippingBOM(inputStream)) {
      CSVFormat customCsvFormat = CSVFormat.Builder.create()
          .setIgnoreEmptyLines(true)
          .setIgnoreSurroundingSpaces(true)
          .setTrim(true)
          .setSkipHeaderRecord(true)
          .setIgnoreHeaderCase(true)
          .setHeader()
          .get();

      Iterable<CSVRecord> records = customCsvFormat.parse(reader);

      for (CSVRecord record : records) {
        resultList.add(mapper.apply(record));
      }

    } catch (IOException ex) {
      throw new RuntimeException("Failed to read CSV file", ex);
    }

    return resultList;
  }

  // BOMが含まれている場合は処理する。
  private static Reader createUtf8ReaderSkippingBOM(InputStream inputStream) throws IOException {
    PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 3);
    byte[] bom = new byte[3];
    int read = pushbackInputStream.read(bom, 0, 3);

    if (read == 3) {
      // UTF-8 BOM (EF BB BF) の場合はスキップ、それ以外は戻す
      if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
        pushbackInputStream.unread(bom, 0, 3);
      }
    } else if (read > 0) {
      // 3バイト未満なら全部戻す
      pushbackInputStream.unread(bom, 0, read);
    }

    return new InputStreamReader(pushbackInputStream, StandardCharsets.UTF_8);
  }
}
