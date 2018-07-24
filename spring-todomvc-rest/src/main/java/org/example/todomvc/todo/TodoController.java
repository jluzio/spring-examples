package org.example.todomvc.todo;

import java.util.List;

import javax.ws.rs.PathParam;

import org.example.todomvc.ApiRequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiRequestMapping
public class TodoController {
	@Autowired
	private TodoRepository todoRepository;
	
	@GetMapping(path="/todo/{id}")
	public List<Todo> findByUser(@PathParam("id") Integer assigneeId) {
		return todoRepository.findByAssignee(assigneeId);
	}
	
	@GetMapping(path="/todoTypes")
	public List<TodoType> listTodoTypes() {
		return todoRepository.listTodoTypes();
	}

	@GetMapping(path="/greet")
	public String greet() {
		return "Hello world!";
	}

}
