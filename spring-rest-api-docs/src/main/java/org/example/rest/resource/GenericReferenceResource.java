package org.example.rest.resource;

import java.util.List;

import org.example.rest.model.GenericReference;
import org.example.rest.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

@RestController
@RequestMapping("genericReference")
public class GenericReferenceResource extends AbstractResource {
	private List<User> users;
	
	public GenericReferenceResource() {
		super();
		
		users = Lists.newArrayList(
				new User("1", "User 1", "user1@mail.org"),
				new User("2", "User 2", "user2@mail.org"),
				new User("3", "User 3", "user3@mail.org")
			);
	}

	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<GenericReference<User>> getReference() {
		GenericReference<User> genericReference = new GenericReference<User>(users.get(0));
		return ResponseEntity.ok(genericReference);
	}

}
