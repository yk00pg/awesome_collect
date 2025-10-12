package webapp.AwesomeCollect.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.Tag;
import webapp.AwesomeCollect.mapper.TagMapper;

/**
 * タグのリポジトリクラス。
 */
@Repository
public class TagRepository {

  private final TagMapper mapper;

  public TagRepository(TagMapper mapper) {
    this.mapper = mapper;
  }

  public List<String> searchTagNameList(int userId) {
    return mapper.selectTagNameListByUserId(userId);
  }

  public List<String> searchTagNameListByTagIdList(List<Integer> tagIdList) {
    return mapper.selectTagNameListByTagIdList(tagIdList);
  }

  public Integer searchTagIdByUserIdAndTagName(Tag tag) {
    return mapper.findTagIdByUserIdAndTagName(tag.getUserId(), tag.getName());
  }

  public void registerTag(Tag tag) {
    mapper.insertTag(tag);
  }

  public void deleteAllTagByUserId(int userId){
    mapper.deleteAllTagByUserId(userId);
  }
}
