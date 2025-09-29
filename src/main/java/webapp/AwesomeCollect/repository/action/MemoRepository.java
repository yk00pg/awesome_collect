package webapp.AwesomeCollect.repository.action;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.action.Memo;
import webapp.AwesomeCollect.mapper.action.MemoMapper;

/**
 * メモのリポジトリクラス。
 */
@Repository
public class MemoRepository {

  private final MemoMapper mapper;

  public MemoRepository(MemoMapper mapper) {
    this.mapper = mapper;
  }

  public List<Memo> searchMemo(int userId) {
    return mapper.selectMemo(userId);
  }

  public Memo findMemoByIds(int memoId, int userId) {
    return mapper.selectMemoByIds(memoId, userId);
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
}
