package com.example.spring.ldap.model;

import javax.naming.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

@Entry(objectClasses = {"person", "top"}, base = "ou=test_person_data")
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public final class Person {

  @Id
  private Name dn;

  @Attribute(name = "cn")
  @DnAttribute(value = "cn", index = 1)
  private String fullName;

  // No @Attribute annotation means this will be bound to the LDAP attribute
  // with the same value
//  @Attribute(name = "description")
  private String description;

  @DnAttribute(value = "ou", index = 0)
  @Transient
  private String company;

  @Attribute(name = "sn")
  private String surname;

}