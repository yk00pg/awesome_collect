package webapp.AwesomeCollect.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.Tag;
import webapp.AwesomeCollect.repository.TagRepository;

/**
 * タグのサービスクラス。
 */
@Service
public class TagService {

  private final TagRepository tagRepository;

  public TagService(TagRepository tagRepository){
    this.tagRepository = tagRepository;
  }

  /**
   * ユーザーIDを基にDBからタグ名リストを取得する。
   *
   * @param userId  ユーザーID
   * @return  タグ名リスト
   */
  public List<String> getTagNameListByUserId(int userId){
    return tagRepository.searchTagNameList(userId);
  }

  /**
   * タグIDリストがnullまたは空の場合は空のリストを、
   * そうでない場合はタグIDリストを基にDBからタグ名リストを取得する。
   *
   * @param tagIdList タグIDリスト
   * @return  タグ名リスト
   */
  public List<String> prepareTagNameListByTagIdList(List<Integer> tagIdList){
    return tagIdList == null || tagIdList.isEmpty()
        ? Collections.emptyList()
        : tagRepository.searchTagNameListByTagIdList(tagIdList);
  }

  /**
   * タグ名リストがnullまたは空の場合は空の文字列を、そうでない場合は
   * タグIDリストを基にDBからタグ名リストを取得し、コンマで結合した文字列を用意する。
   *
   * @param tagIdList タグIDリスト
   * @return
   */
  public String prepareCombinedTagName(List<Integer> tagIdList){
    return tagIdList == null || tagIdList.isEmpty()
        ? ""
        : String.join(",", prepareTagNameListByTagIdList(tagIdList));
  }

  /**
   * 変換後のタグリストがnullの場合はnullを返し、そうでない場合は
   * 変換後のタグリストの中身を確認し、タグIDリストを作成する。
   *
   * @param userId  ユーザーID
   * @param pureTagList 変換後のタグリスト
   * @return  タグIDリスト
   */
  public List<Integer> resolveTagIdList(int userId, List<String> pureTagList){
    return pureTagList == null
        ? null
        : pureTagList.stream()
        .map(tagName -> resolveTagId(userId, tagName))
        .collect(Collectors.toList());
  }

  /**
   * タグ情報を基にDBを確認し、登録されていなければ登録し、タグIDを取得する。
   *
   * @param userId  ユーザーID
   * @param tagName タグ名
   * @return  タグID
   */
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
