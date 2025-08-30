package webapp.AwesomeCollect.service.action;

import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.mapper.action.MemoMapper;
import webapp.AwesomeCollect.entity.action.Memo;

@Service
public class MemoService {

  private final MemoMapper mapper;

  public MemoService(MemoMapper mapper){
    this.mapper = mapper;
  }

  public List<Memo> searchMemo(int userId){
    return mapper.selectMemo(userId);
  }

  public Memo findMemoByIds(int id, int userId){
    return mapper.selectMemoByIds(id, userId);
  }

  public int countMemo(int userId){
    return mapper.countMemo(userId);
  }

  public void registerMemo(Memo memo){
    mapper.insertMemo(memo);
  }

  public void updateMemo(Memo memo){
    mapper.updateMemo(memo);
  }

  public void deleteMemo(int id){
    mapper.deleteMemo(id);
  }
}
