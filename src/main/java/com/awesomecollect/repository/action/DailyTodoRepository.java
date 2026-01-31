package com.awesomecollect.repository.action;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.awesomecollect.entity.action.DailyTodo;
import com.awesomecollect.mapper.action.DailyTodoMapper;

/**
 * やることのリポジトリクラス。
 */
@Repository
public class DailyTodoRepository {

  private final DailyTodoMapper mapper;

  public DailyTodoRepository(DailyTodoMapper mapper) {
    this.mapper = mapper;
  }

  public List<DailyTodo> searchDailyTodo(int userId, LocalDate date) {
    return mapper.selectDailyTodo(userId, date);
  }

  public void registerDailyTodo(DailyTodo todo) {
    mapper.insertTodo(todo);
  }

  public void updateDailyTodo(DailyTodo todo) {
    mapper.updateTodo(todo);
  }

  public void deleteDailyTodoById(int todoId) {
    mapper.deleteTodoById(todoId);
  }

  public void deleteDailyTodoByDate(int userId, LocalDate date) {
    mapper.deleteTodoByDate(userId, date);
  }

  public void deleteALlTodoByUserId(int userId){
    mapper.deleteAllTodoByUserId(userId);
  }
}
