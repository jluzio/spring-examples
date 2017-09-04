package org.example.rest.resource;

import java.util.List;

import org.example.rest.exception.ApiException;
import org.example.rest.exception.NotFoundException;
import org.example.rest.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("users")
public class UserResource extends AbstractResource {
	private List<User> users;
	
	public UserResource() {
		super();
		
		users = Lists.newArrayList(
				new User("1", "User 1", "user1@mail.org"),
				new User("2", "User 2", "user2@mail.org"),
				new User("3", "User 3", "user3@mail.org")
			);
	}

	@RequestMapping(method=RequestMethod.GET)
	@ApiOperation(notes = "Returns users", value = "Returns users", tags = {"Users"}, nickname = "listUsers")
	public ResponseEntity<List<User>> listUsers() {
		return ResponseEntity.ok(users);
	}

	@RequestMapping(value="/{id}", method=RequestMethod.GET)
    @ApiOperation(notes = "Returns a user when 0 < ID <= 10.  ID > 10 or non-integers will simulate API error conditions", 
    	value = "Find user by ID", nickname = "getUserById",
    	tags = {"Users"} )
	@ApiResponses({
	    @ApiResponse(code = 200, message = "Nice!", response = User.class),
	    @ApiResponse(code = 400, message = "Invalid ID supplied", response = ApiResponse.class),
	    @ApiResponse(code = 404, message = "User not found", response = ApiResponse.class)
	})
	public ResponseEntity<User> getUserById(
			@ApiParam(value = "ID of user that needs to be fetched", allowableValues = "range[1,10]", required = true) 
			@PathVariable("id") 
			String id) 
		throws ApiException
	{
		User user = users.stream()
				.filter(u -> u.getId().equals(id))
				.findFirst()
				.orElse(null);
		if (user != null) {
			return ResponseEntity.ok().body(user);
		} else {
			throw new NotFoundException(org.example.rest.model.ApiResponse.ERROR, "User " + id + " not found");
		}
		
	}

}
