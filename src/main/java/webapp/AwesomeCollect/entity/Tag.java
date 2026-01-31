package webapp.AwesomeCollect.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * タグ情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class Tag {

  private int id;
  private int userId;
  private String name;

  public Tag(int userId, String name){
    this.userId = userId;
    this.name = name;
  }
}
