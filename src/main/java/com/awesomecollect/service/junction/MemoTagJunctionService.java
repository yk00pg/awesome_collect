package com.awesomecollect.service.junction;

import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.awesomecollect.entity.junction.MemoTagJunction;
import com.awesomecollect.repository.junction.MemoTagJunctionRepository;

/**
 * メモとタグの関係性のサービスクラス。
 */
@Service
public class MemoTagJunctionService extends BaseActionTagJunctionService<MemoTagJunction> {

  public MemoTagJunctionService(MemoTagJunctionRepository repository) {
    super(repository);
  }

  @Override
  public List<Integer> prepareTagIdListByActionId(int memoId) {
    return super.prepareTagIdListByActionId(memoId);
  }

  @Override
  public void registerNewRelations(
      int memoId, BiFunction<Integer, Integer, MemoTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.registerNewRelations(memoId, relationFactory, tagIdList);
  }

  @Override
  @Transactional
  public void updateRelations(
      int memoId, BiFunction<Integer, Integer, MemoTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.updateRelations(memoId, relationFactory, tagIdList);
  }

  @Override
  public void registerRelation(MemoTagJunction relation) {
    super.registerRelation(relation);
  }

  @Override
  public void deleteRelationByActionId(int memoId) {
    super.deleteRelationByActionId(memoId);
  }

  @Override
  public void deleteRelationByRelatedId(MemoTagJunction relation) {
    super.deleteRelationByRelatedId(relation);
  }
}
