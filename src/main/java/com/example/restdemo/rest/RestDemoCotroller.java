package com.example.restdemo.rest;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.restdemo.entity.User;
import com.example.restdemo.service.UserService;
import com.example.restdemo.utility.Utilities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;

@Tag(name = "CRUD demo operations")
@RestController
@RequestMapping("/api")
public class RestDemoCotroller {

	public static final int USER_AT_START = 50;
	private UserService userService;
	Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	public RestDemoCotroller(UserService userService) {
		this.userService = userService;
	}

	// This preferred for now, over initial.SQL scripts
	@PostConstruct
	public void initializeUsers() {
		logger.info("Creating test users for database");
		for (int i = 0; i <= USER_AT_START; i++) {
			userService.save(Utilities.generateRandomUser(i));
		}
	}

	@Operation(description = "Get all users and/or with name and/or page size with GET message", summary = "Get all users")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK", content = @Content),
			@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Error", content = @Content) })
	// GET return all users
	@GetMapping("/v1/users")
	public ResponseEntity<List<User>> getAllUsers(
			@Parameter(description = "Return all instances of this name") @RequestParam Optional<String> name,
			@Parameter(description = "Amount of users to be returned") @RequestParam Optional<Integer> pageSize) {
		List<User> findAllResponse;
		logger.info("Getting all users");
		if (name.isPresent()) {
			findAllResponse = userService.findAllByName(name.get(), pageSize);

		} else {
			findAllResponse = userService.findAll(pageSize);
		}
		return findAllResponse.isEmpty()
				? ResponseEntity.status(HttpStatus.NO_CONTENT).contentType(MediaType.APPLICATION_JSON)
						.body(findAllResponse)
				: ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(findAllResponse);
	}

	@Operation(description = "Get user with specified userId with GET message", summary = "Get user with specified userId")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content) })
	// GET user by Id
	@GetMapping("/v1/users/{userId}")
	public ResponseEntity<?> getUserById(
			@Parameter(description = "Id of user to be searched") @PathVariable final int userId) {
		User theEmployee = userService.findById(userId);
		logger.info("Getting user " + userId);
		if (theEmployee == null) {
			logger.info("User was not found " + userId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
					.body("User id - " + userId + " was not found");
		}
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(theEmployee);
	}

	@Operation(description = "Adds user with POST message", summary = "Add user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
			@ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = @Content) })
	// ADD user by POST message
	@PostMapping("/v1/users")
	public ResponseEntity<User> createUser(
			@Parameter(description = "Id of user to be searched") @RequestBody final User user) {
		// If id is in JSON .. we set id to 0. This force save in new item, instead of
		// update
		user.setId(0);
		User dbUser = userService.save(user);
		logger.info("New user have been added " + user);
		return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(dbUser);
	}

	@Operation(description = "Modify user with PUT message or saves a given entity if id is not present in database.", summary = "Modify user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
			@ApiResponse(responseCode = "201", description = "Created", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content), })
	// MODIFY by PUT message
	@PutMapping("/v1/users")
	public ResponseEntity<User> modifyUser(
			@Parameter(description = "User to be modified or to be added if not found in database") @RequestBody final User user) {
		// Modifying, need to input id
		int givenUserId = user.getId();
		User dbUser = userService.save(user);
		boolean userModified = dbUser.getId() == givenUserId;
		if (userModified) {
			logger.info("Modified user " + user);
		} else {
			logger.info("New user have been added " + user);
		}
		return userModified ? ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(dbUser)
				: ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(dbUser);
	}

	@Operation(description = "Delete user by userId with DELETE message", summary = "Delete user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content) })
	// DELETE by ID
	@DeleteMapping("/v1/users/{userId}")
	public ResponseEntity<String> deleteUser(
			@Parameter(description = "Id of user to be deleted") @PathVariable final int userId) {
		User tempUser = userService.findById(userId);
		if (tempUser == null) {
			logger.info("User was not found " + userId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
					.body("User id - " + userId + " was not found");
		}
		userService.deleteById(userId);
		logger.info("User deleted " + userId);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
				.body("Deleted user id - " + userId);
	}
}
