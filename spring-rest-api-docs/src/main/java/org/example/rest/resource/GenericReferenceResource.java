package org.example.rest.resource;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiResponse;
import java.util.List;
import org.example.rest.model.GenericReference;
import org.example.rest.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


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

  @ApiModel(description = "user ref")
  public static class RefResponse extends GenericReference<User> {

  }

  @RequestMapping(value = "v1", method = RequestMethod.GET)
  public ResponseEntity<GenericReference<User>> getReferenceV1() {
    GenericReference<User> genericReference = new GenericReference<User>(users.get(0));
    return ResponseEntity.ok(genericReference);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = "v2", method = RequestMethod.GET)
  @ApiResponse(response = RefResponse.class, code = 200, message = "success response")
  public ResponseEntity<GenericReference<Object>> getReferenceV2() {
    GenericReference<User> genericReference = new GenericReference<User>(users.get(0));
    return ResponseEntity.ok((GenericReference) genericReference);
  }

}
