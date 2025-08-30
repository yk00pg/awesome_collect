package webapp.AwesomeCollect.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import org.springframework.stereotype.Component;
import webapp.AwesomeCollect.dto.TagDto;
import webapp.AwesomeCollect.entity.Tag;
import webapp.AwesomeCollect.entity.junction.ActionTagJunction;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.junction.BaseActionTagJunctionService;

@Component
public class TaggingManager {

  private final TagService tagService;

  public TaggingManager(TagService tagService){
    this.tagService = tagService;
  }

  /**
   * タグリストがnullじゃない場合は、タグのデータオブジェクトをエンティティに変換し、
   * DBからタグIDを取得（存在しなければ登録して取得）し、アクションIDとタグIDの組み合わせが
   * 中間テーブルに存在しない場合は登録する。
   *
   * @param actionId  アクションID
   * @param pureTagList 文字列変換後のタグリスト
   * @param userId  ユーザーID
   * @param relationFactory 中間テーブルのエンティティを生成するインターフェース
   * @param junctionService 中間テーブルとのやりとりを行うサービス
   * @param <T> ジェネリクス
   */
  public <T extends ActionTagJunction> void resolveTagsAndRelations(
      int actionId, List<String> pureTagList, int userId,
      BiFunction<Integer, Integer, T> relationFactory, BaseActionTagJunctionService<T> junctionService){

    if(pureTagList != null){
      for(String tagName : pureTagList){
        Tag tag = TagDto.toTag(userId, tagName);
        int tagId = tagService.resolveTagIds(tag);
        T relation = relationFactory.apply(actionId, tagId);
        junctionService.registerRelationIfNotExist(relation);
      }
    }
  }

  /**
   * 文字列変換後のタグリストがnullでない場合は、タグIDをDBから取得（存在しなければ登録して取得）し、
   * 更新後のタグIDリストを作成する。<br>
   * DBの中間テーブルから現在のタグIDリストを取得し、更新後のタグIDリストと比較して登録/削除処理を実行する。
   *
   * @param actionId  アクションID
   * @param pureTagList 文字列変換後のタグリスト
   * @param userId  ユーザーID
   * @param relationFactory 中間テーブルのエンティティを生成するインターフェース
   * @param junctionService 中間テーブルとのやりとりを行うサービス
   * @param <T> ジェネリクス
   */
  public <T extends ActionTagJunction> void updateTagsAndRelations(
      int actionId, List<String> pureTagList, int userId,
      BiFunction<Integer, Integer, T> relationFactory, BaseActionTagJunctionService<T> junctionService){

    if(pureTagList != null){
      List<Integer> newTagIdList = new ArrayList<>();
      resolveTagsAndAddToNewTagIdList(userId, pureTagList, newTagIdList);

      List<Integer> currentTagIdList = junctionService.searchTagIdsByActionId(actionId);
      registerNewRelations(actionId, newTagIdList, currentTagIdList, relationFactory, junctionService);
      deleteOldRelations(actionId, currentTagIdList, newTagIdList, relationFactory, junctionService);
    }
  }

  /**
   * タグのデータオブジェクトをエンティティに変換し、DBからタグIDを取得（存在しなければ登録して取得）し、
   * 更新後のタグIDリストに追加する。
   *
   * @param userId  ユーザーID
   * @param pureTagList 文字列変換後のタグリスト
   * @param newTagIdList  更新後のタグIDリスト
   */
  private void resolveTagsAndAddToNewTagIdList(
      int userId, List<String> pureTagList, List<Integer> newTagIdList){

    for(String tagName : pureTagList){
      Tag tag = TagDto.toTag(userId, tagName);
      int tagId = tagService.resolveTagIds(tag);
      newTagIdList.add(tagId);
    }
  }

  /**
   * 更新後のタグIDリストから現在のタグIDリストに含まれる値を削除し、
   * 差分をDBの中間テーブルにアクションIDと紐づけて登録する。
   *
   * @param actionId  アクションID
   * @param newTagIdList  更新後のタグIDリスト
   * @param currentTagIdList  現在のタグIDリスト
   * @param relationFactory 中間テーブルのエンティティを生成するインターフェース
   * @param junctionService 中間テーブルとのやりとりを行うサービス
   * @param <T> ジェネリクス
   */
  private <T extends ActionTagJunction> void registerNewRelations(
      int actionId, List<Integer> newTagIdList, List<Integer> currentTagIdList,
      BiFunction<Integer, Integer, T> relationFactory, BaseActionTagJunctionService<T> junctionService){

    Set<Integer> toAdd = new HashSet<>(newTagIdList);
    currentTagIdList.forEach(toAdd :: remove);
    for(Integer tagId : toAdd){
      T relation = relationFactory.apply(actionId, tagId);
      junctionService.registerRelationIfNotExist(relation);
    }
  }

  /**
   * 現在のタグIDリストから更新後のタグIDリストの値を削除し、
   * 差分のレコードをDBの中間テーブルから削除する。
   *
   * @param actionId  アクションID
   * @param currentTagIdList  現在のタグIDリスト
   * @param newTagIdList  更新後のタグIDリスト
   * @param relationFactory 中間テーブルのエンティティを生成するインターフェース
   * @param junctionService 中間テーブルとのやりとりを行うサービス
   * @param <T> ジェネリクス
   */
  private <T extends ActionTagJunction> void deleteOldRelations(
      int actionId, List<Integer> currentTagIdList, List<Integer> newTagIdList,
      BiFunction<Integer, Integer, T> relationFactory, BaseActionTagJunctionService<T> junctionService){

    Set<Integer> toRemove = new HashSet<>(currentTagIdList);
    newTagIdList.forEach(toRemove :: remove);
    for(Integer tagId : toRemove){
      T relation = relationFactory.apply(actionId, tagId);
      junctionService.deleteRelationByRelatedId(relation);
    }
  }
}
