package com.awesomecollect.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.awesomecollect.entity.BonusAwesome;
import com.awesomecollect.mapper.BonusAwesomeMapper;

/**
 * ボーナスえらい！のリポジトリクラス。
 */
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

  public void deleteAllBonusAwesomeByUserId(int userId){
    mapper.deleteAllBonusAwesomeByUserId(userId);
  }
}
