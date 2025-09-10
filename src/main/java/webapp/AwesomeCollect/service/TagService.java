package webapp.AwesomeCollect.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.Tag;
import webapp.AwesomeCollect.entity.junction.ActionTagJunction;
import webapp.AwesomeCollect.repository.TagRepository;
import webapp.AwesomeCollect.service.junction.BaseActionTagJunctionService;

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

  /**
   * DBの登録状況に応じてタグIDを取得し、中間テーブルに存在しない場合は登録する。
   *
   * @param actionId アクションID
   * @param pureTagList 文字列変換後のタグリスト
   * @param userId  ユーザーID
   * @param relationFactory 中間テーブル用のエンティティを生成するインターフェース
   * @param junctionService 中間テーブルのサービスクラス
   * @param <T> ジェネリクス
   */
  public <T extends ActionTagJunction> void resolveTagsAndRelations(
      int actionId, List<String> pureTagList, int userId,
      BiFunction<Integer, Integer, T> relationFactory,
      BaseActionTagJunctionService<T> junctionService){

    if(pureTagList == null){
      return;
    }

    for(String tagName : pureTagList){
      int tagId = resolveTagId(new Tag(userId,tagName));
      T relation = relationFactory.apply(actionId, tagId);
      junctionService.registerRelationIfNotExist(relation);
    }
  }

  /**
   * 新しく登録された入力されたタグのIDリストと、DBに登録されていたタグIDリストを比較し、<br>
   * 差分をDBの中間テーブルに保存（登録・削除）する。
   *
   * @param actionId  アクションID
   * @param pureTagList 文字列変換後のタグリスト
   * @param userId  ユーザーID
   * @param relationFactory 中間テーブル用のタグリスト
   * @param junctionService 中間テーブルのサービスクラス
   * @param <T> ジェネリクス
   */
  public <T extends ActionTagJunction> void updateTagsAndRelations(
      int actionId, List<String> pureTagList, int userId,
      BiFunction<Integer, Integer, T> relationFactory,
      BaseActionTagJunctionService<T> junctionService){

    if(pureTagList == null){
      return;
    }

    List<Integer> newTagIdList = resolveTagIdList(userId, pureTagList);
    List<Integer> currentTagIdList = junctionService.prepareTagIdLitByActionId(actionId);

    registerNewRelations(
        actionId, newTagIdList, currentTagIdList, relationFactory, junctionService);
    deleteOldRelations(
        actionId, currentTagIdList, newTagIdList, relationFactory, junctionService);
  }

  // タグIDを取得（登録されていなければ登録して取得）
  @Transactional
  private int resolveTagId(Tag tag) {
    Integer tagId = tagRepository.searchTagIdByUserIdAndTagName(tag);
    if (tagId == null) {
      tagRepository.registerTag(tag);
      return tag.getId();
    } else {
      return tagId;
    }
  }

  // タグIDをリスト形式で取得（登録されていなければ登録して取得）
  private List<Integer> resolveTagIdList(int userId, List<String> pureTagList){
    return pureTagList.stream()
        .mapToInt(tagName -> resolveTagId(new Tag(userId, tagName)))
        .boxed()
        .collect(Collectors.toList());
  }

  // 新しく登録されたタグリストのうち、DBに登録されていないものを中間テーブルに登録
  private <T extends ActionTagJunction> void registerNewRelations(
      int actionId, List<Integer> newTagIdList, List<Integer> currentTagIdList,
      BiFunction<Integer, Integer, T> relationFactory,
      BaseActionTagJunctionService<T> junctionService){

    Set<Integer> toAddTagList = new HashSet<>(newTagIdList);
    currentTagIdList.forEach(toAddTagList :: remove);

    toAddTagList.stream()
        .map(tagId -> relationFactory.apply(actionId, tagId))
        .forEach(junctionService :: registerRelationIfNotExist);
  }

  // DBに登録されていたタグリストのうち、新しく登録されたタグリストに存在しないものを差分を中間テーブルから削除
  private <T extends ActionTagJunction> void deleteOldRelations(
      int actionId, List<Integer> currentTagIdList, List<Integer> newTagIdList,
      BiFunction<Integer, Integer, T> relationFactory,
      BaseActionTagJunctionService<T> junctionService){

    Set<Integer> toRemoveList = new HashSet<>(currentTagIdList);
    newTagIdList.forEach(toRemoveList :: remove);

    toRemoveList.stream()
        .map(tagId -> relationFactory.apply(actionId, tagId))
        .forEach(junctionService :: deleteRelationByRelatedId);
  }
}
