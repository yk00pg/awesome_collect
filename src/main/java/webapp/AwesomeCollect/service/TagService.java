package webapp.AwesomeCollect.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.Tag;
import webapp.AwesomeCollect.repository.TagRepository;

@Service
public class TagService {

  private final TagRepository tagRepository;

  public TagService(TagRepository tagRepository){
    this.tagRepository = tagRepository;
  }

  public List<String> prepareTagListByUserId(int userId){
    return tagRepository.searchTagNameList(userId);
  }

  public List<String> prepareTagListByTagIdList(List<Integer> tagIdList){
    return tagRepository.searchTagNameListByTagIdList(tagIdList);
  }

  public String getCombinedTagName(List<Integer> tagIdList){
    return String.join(",", prepareTagListByTagIdList(tagIdList));
  }

  @Transactional
  public int resolveTagId(Tag tag) {
    Integer tagId = tagRepository.searchTagIdByUserIdAndTagName(tag);
    if (tagId == null) {
      tagRepository.registerTag(tag);
      return tag.getId();
    } else {
      return tagId;
    }
  }
}
