package com.example.spring.ldap.repository;

import com.example.spring.ldap.model.User;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends LdapRepository<User> {

}
