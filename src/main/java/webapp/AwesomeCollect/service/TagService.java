package webapp.AwesomeCollect.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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

  public List<String> prepareTagNameListByTagIdList(List<Integer> tagIdList){
    return tagIdList == null || tagIdList.isEmpty()
        ? Collections.emptyList()
        : tagRepository.searchTagNameListByTagIdList(tagIdList);
  }

  public String prepareCombinedTagName(List<Integer> tagIdList){
    return tagIdList == null || tagIdList.isEmpty()
        ? ""
        : String.join(",", prepareTagNameListByTagIdList(tagIdList));
  }

  // タグIDをリスト形式で取得（登録されていなければ登録して取得）
  public List<Integer> resolveTagIdList(int userId, List<String> pureTagList){
    return pureTagList != null
        ? pureTagList.stream()
            .map(tagName -> resolveTagId(userId, tagName))
            .collect(Collectors.toList())
        : null;
  }

  // タグIDを取得（登録されていなければ登録して取得）
  @Transactional
  private int resolveTagId(int userId, String tagName) {
    Tag tag = new Tag(userId, tagName);
    Integer tagId = tagRepository.searchTagIdByUserIdAndTagName(tag);
    if (tagId == null) {
      tagRepository.registerTag(tag);
      return tag.getId();
    } else {
      return tagId;
    }
  }
}
