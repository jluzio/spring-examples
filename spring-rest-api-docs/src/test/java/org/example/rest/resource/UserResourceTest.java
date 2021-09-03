package org.example.rest.resource;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/*
 * Static imports
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  
 */

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
public class UserResourceTest {
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private UserResource userResource;
	
	private MockMvc mockMvc;
	 
	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation){
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
	      .apply(documentationConfiguration(restDocumentation))
	      .build();
	}
	
	@Test
	public void users() throws Exception {
	    this.mockMvc.perform(
	      get("/users")
	        .accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isOk())
//	        .andExpect(jsonPath("_links.crud", is(notNullValue())))
	        .andDo(document("users"));
	}
}
