package org.example.todomvc.todo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TodoRepository extends CrudRepository<Todo, Integer> {
	
	@Query("SELECT type FROM TodoType type ORDER BY type.name")
    @Transactional(readOnly = true)
    List<TodoType> listTodoTypes();
	
	@Query("SELECT todo FROM Todo todo WHERE todo.assignee.id = :assigneeId ORDER BY todo.priority, todo.title")
	List<Todo> findByAssignee(@Param("assigneeId") Integer assigneeId);

}
