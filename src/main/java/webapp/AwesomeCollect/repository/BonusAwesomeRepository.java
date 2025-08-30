package webapp.AwesomeCollect.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.BonusAwesome;
import webapp.AwesomeCollect.mapper.BonusAwesomeMapper;

@Repository
public class BonusAwesomeRepository {

  private final BonusAwesomeMapper mapper;

  public BonusAwesomeRepository(BonusAwesomeMapper mapper){
    this.mapper = mapper;
  }

  public List<BonusAwesome> searchBonusAwesome(int userId){
    return mapper.selectBonusAwesomeList(userId);
  }

  public void registerBonusAwesome(BonusAwesome bonusAwesome){
    mapper.insertBonusAwesome(bonusAwesome);
  }
}
