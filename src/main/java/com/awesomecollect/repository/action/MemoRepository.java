package com.awesomecollect.repository.action;

import com.awesomecollect.entity.action.Memo;
import com.awesomecollect.mapper.action.MemoMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * メモのリポジトリクラス。
 */
@Repository
public class MemoRepository {

  private final MemoMapper mapper;

  public MemoRepository(MemoMapper mapper) {
    this.mapper = mapper;
  }

  public List<Integer> searchIdByUserId(int userId){
    return mapper.selectIdByUserId(userId);
  }

  public List<Memo> searchMemo(int userId) {
    return mapper.selectMemo(userId);
  }

  public Optional<Memo> findMemoByIds(int memoId, int userId) {
    return Optional.ofNullable(mapper.selectMemoByIds(memoId, userId));
  }

  public Integer findIdByUserIdAndTitle(int userId, String title) {
    return mapper.selectIdByUserIdAndTitle(userId, title);
  }

  public void registerMemo(Memo memo) {
    mapper.insertMemo(memo);
  }

  public void updateMemo(Memo memo) {
    mapper.updateMemo(memo);
  }

  public void deleteMemo(int memoId) {
    mapper.deleteMemo(memoId);
  }

  public void deleteAllMemoByUserId(int userId){
    mapper.deleteAllMemoByUserId(userId);
  }
}
