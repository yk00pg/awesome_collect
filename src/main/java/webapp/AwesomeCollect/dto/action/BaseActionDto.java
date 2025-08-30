package webapp.AwesomeCollect.dto.action;

import java.util.List;

public abstract class BaseActionDto<E, D extends BaseActionDto<E, D>> {

  public abstract int getId();
  public abstract void setId(int actionId);
  public abstract void setTags(String tags);
  public abstract List<D> fromEntityList(List<E> actionList);
  public abstract D fromEntity(E entity);
}
