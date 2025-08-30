package webapp.AwesomeCollect.dto;

import lombok.Data;
import webapp.AwesomeCollect.entity.Tag;

@Data
public class TagDto {

  public static Tag toTag(int userId, String name){
    Tag tag = new Tag();
    tag.setUserId(userId);
    tag.setName(name);
    return tag;
  }
}
