package com.example.spring.ldap.model;

import javax.naming.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Attribute.Type;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Entry(objectClasses = {"inetOrgPerson"}, base = "ou=users")
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public final class User {

  @Id
  private Name dn;
  @Attribute(name = "uid")
  private String username;
  @Attribute(name = "userPassword", type = Type.BINARY)
  private byte[] password;

}