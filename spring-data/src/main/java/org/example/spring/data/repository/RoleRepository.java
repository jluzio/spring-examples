package org.example.spring.data.repository;

import java.util.Optional;
import org.example.spring.data.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository extends CrudRepository<Role, Long> {

  Optional<Role> findByValue(@Param("value") String value);

}