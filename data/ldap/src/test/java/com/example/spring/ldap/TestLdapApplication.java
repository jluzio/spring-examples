package com.example.spring.ldap;

import org.springframework.boot.SpringApplication;

public class TestLdapApplication {

	public static void main(String[] args) {
		SpringApplication.from(LdapApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
