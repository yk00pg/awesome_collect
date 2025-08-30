package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.mapper.action.DailyTodoMapper;
import webapp.AwesomeCollect.entity.action.DailyTodo;

@Service
public class DailyTodoService {

  private final DailyTodoMapper mapper;

  public DailyTodoService(DailyTodoMapper mapper){
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
