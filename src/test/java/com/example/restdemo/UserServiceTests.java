package com.example.restdemo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import com.example.restdemo.entity.User;
import com.example.restdemo.rest.RestDemoController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTests {
	/*
	 * Test db is created on when tests are ran in Eclipse
	 * 
	 * @Transactional roll back after every test
	 */
	String apiVersionUnderTest = "v1";
	String apiUrlUnderTest;

	@Autowired
	private MockMvc mockMvc;

	@Container
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
	}

	@BeforeAll
	static void beforeAll() { // Before all, need to start container
		mySQLContainer.start();
	}

	@AfterAll
	static void afterAll() { // After all, need to stop container
		mySQLContainer.stop();
	}

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(RestDemoController.class).build();
	}

	@Test
	@Transactional
	public void postAddNewUserTest() throws Exception {
		User user = new User(1, "Pekka", "1111111111");
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		mockMvc.perform(MockMvcRequestBuilders.post(apiUrlUnderTest).contentType("application/json")
				.content(asJsonString(user))).andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());

	}

	@Test
	@Transactional
	public void postAddNewUserUnsupportedTest() throws Exception {
		User user = new User(1, "Pekka", "1111111111");
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		mockMvc.perform(MockMvcRequestBuilders.post(apiUrlUnderTest).contentType(MediaType.IMAGE_GIF)
				.content(asJsonString(user))).andExpect(status().isUnsupportedMediaType());
	}

	@Test
	@Transactional
	public void getAllUsersTest() throws Exception {
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		mockMvc.perform(MockMvcRequestBuilders.get(apiUrlUnderTest)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.*").exists()) // Should not return empty value
				.andExpect(jsonPath("$.length()", is(RestDemoController.USER_AT_START)));
	}

	@Test
	@Transactional
	public void getAllUsersWithImpossibleNameTest() throws Exception {
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users/";
		
		// Removing users, just make sure that its not present
		User user = new User(551, "ImpossibleName", "7777777"); //Should not exist in db
		mockMvc.perform(MockMvcRequestBuilders.delete(apiUrlUnderTest + "{id}", user.getId()))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().string("User id - " + user.getId() + " was not found")); // Delete should not find any patient with such id

		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		
		mockMvc.perform(MockMvcRequestBuilders.get(apiUrlUnderTest).param("name", user.getName()))
				.andExpect(status().isNoContent()).andExpect(MockMvcResultMatchers.jsonPath("$.*").doesNotExist()); //There should not be any findings with this name 
	}

	@Test
	@Transactional
	public void getAllUsersWithNameTest() throws Exception {
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		User user = new User(12345, "nameToBeFound", "666");
		mockMvc.perform(MockMvcRequestBuilders.post(apiUrlUnderTest).contentType("application/json")
				.content(asJsonString(user))).andExpect(status().isCreated());	// Adding user with unique
																				// name

		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		mockMvc.perform(MockMvcRequestBuilders.get(apiUrlUnderTest).param("name", user.getName()))
				.andExpect(status().isOk()) // User with name found
				.andExpect(MockMvcResultMatchers.jsonPath("$.*").exists()); // Should not return empty value
	}

	@Test
	@Transactional
	public void getUserByIdTest() throws Exception {
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users/30";
		mockMvc.perform(MockMvcRequestBuilders.get(apiUrlUnderTest)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.*").exists()) // Should return some User
				.andExpect(status().isOk()) // and its OK
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(30)); // Should find id which was 
	}

	@Test
	@Transactional
	public void getUserByIdShouldNotBeFoundTest() throws Exception {
		//Delete user first
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users/";
		mockMvc.perform(MockMvcRequestBuilders.delete(apiUrlUnderTest + "{id}", 50))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Deleted user id - " + 50));  
		//Then test, not found
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users/50"; 	//Should not find with such id
		mockMvc.perform(MockMvcRequestBuilders.get(apiUrlUnderTest)).andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.*").doesNotExist()); 
	}

	@Test
	@Transactional
	public void putModifyUserTest() throws Exception {
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		User user = new User(3, "PekkaModified", "12222222");
		mockMvc.perform(
				MockMvcRequestBuilders.put(apiUrlUnderTest).contentType("application/json").content(asJsonString(user)))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.*").exists()); // Should not
																										// return empty
																										// value
	}

	@Test
	@Transactional
	public void putModifyUserShouldCreateUserTest() throws Exception {
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";
		User user = new User(132323223, "PekkaShouldBeAdded", "133333");
		mockMvc.perform(
				MockMvcRequestBuilders.put(apiUrlUnderTest).contentType("application/json").content(asJsonString(user)))
				.andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.*").exists()); // Should
																											// exist and
																											// adding
																											// was made
	}

	@Test
	@Transactional
	public void deleteCreatedUserTest() throws Exception {
		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users";

		User user = new User(20, "PekkaForDelete", "66666");
		MvcResult addedUser = mockMvc
				.perform(MockMvcRequestBuilders.post(apiUrlUnderTest).contentType("application/json")
						.content(asJsonString(user))) // Adding with post
				.andExpect(status().isCreated()).andReturn(); // Add user to be deleted

		String content = addedUser.getResponse().getContentAsString();

		JsonObject convertedObject = new Gson().fromJson(content, JsonObject.class);
		JsonElement jsonElement = convertedObject.get("id");

		apiUrlUnderTest = "/api/" + apiVersionUnderTest + "/users/";
		mockMvc.perform(MockMvcRequestBuilders.delete(apiUrlUnderTest + "{id}", jsonElement.getAsInt()))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Deleted user id - " + jsonElement.getAsInt()));  // Delete was ok and returned delete code was the same as which was given
	}

	private String asJsonString(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

}
