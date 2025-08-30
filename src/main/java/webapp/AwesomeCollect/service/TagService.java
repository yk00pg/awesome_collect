package webapp.AwesomeCollect.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.mapper.TagMapper;
import webapp.AwesomeCollect.entity.Tag;

@Service
public class TagService {

  private final TagMapper mapper;

  public TagService(TagMapper mapper){
    this.mapper = mapper;
  }

  public List<String> getTagNameList(int userId){
    return mapper.selectTagList(userId);
  }

  public String getTagName(List<Integer> tagIds){
    List<String> tagNameList = new ArrayList<>();
    for(int tagId : tagIds){
      tagNameList.add(mapper.selectTagName(tagId));
    }
    return String.join(",",tagNameList);
  }

  @Transactional
  public int resolveTagIds(Tag tag) {
    Integer tagId = mapper.selectIdByUserIdAndTagName(tag.getUserId(), tag.getName());
    if (tagId == null) {
      mapper.insertTag(tag);
      return tag.getId();
    } else {
      return tagId;
    }
  }

  public void updateTag(Tag tag){
    mapper.updateTag(tag);
  }

  public void deleteTagById(Tag tag){
    mapper.deleteTag(tag);
  }
}
