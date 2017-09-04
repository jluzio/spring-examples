package org.example.rest.resource;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/*
 * Static imports
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  
 */

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserResourceTest {
	@Autowired
	private WebApplicationContext context;
	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");	 
//	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("/home/tmp/generated-snippets");	 
	@Autowired
	private UserResource userResource;
	
	private MockMvc mockMvc;
	 
	@Before
	public void setUp(){
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
	      .apply(documentationConfiguration(this.restDocumentation))
	      .build();
	}
	
	@Test
	public void users() throws Exception {
	    this.mockMvc.perform(
	      get("/users")
	        .accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isOk())
//	        .andExpect(jsonPath("_links.crud", is(notNullValue())))
	        .andDo(document("users"))
	        ;
	}
}
