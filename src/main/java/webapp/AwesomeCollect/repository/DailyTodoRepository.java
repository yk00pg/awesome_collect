package webapp.AwesomeCollect.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.action.DailyTodo;
import webapp.AwesomeCollect.mapper.action.DailyTodoMapper;

@Repository
public class DailyTodoRepository {

  private final DailyTodoMapper mapper;

  public DailyTodoRepository(DailyTodoMapper mapper){
    this.mapper = mapper;
  }

  public List<DailyTodo> searchDailyTodo(int userId, LocalDate date){
    return mapper.selectDailyTodo(userId, date);
  }

  public int countDailyTodo(int userId){
    return mapper.countDailyTodo(userId);
  }

  public void registerDailyTodo(DailyTodo todo){
    mapper.insertTodo(todo);
  }

  public void updateDailyTodo(DailyTodo todo){
    mapper.updateTodo(todo);
  }

  public void deleteDailyTodoById(int id){
    mapper.deleteTodoById(id);
  }

  public void deleteDailyTodoByDate(int userId, LocalDate date){
    mapper.deleteTodoByDate(userId, date);
  }
}
