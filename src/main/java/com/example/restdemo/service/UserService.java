package com.example.restdemo.service;

import java.util.List;
import java.util.Optional;

import com.example.restdemo.entity.User;

public interface UserService {
	List<User> findAll(Optional<Integer> pageSize);
	List<User> findAllByName(String name, Optional<Integer> pageSize);
	User findById(int theId);
	User save(User theEmployee);
	void deleteById(int theId);
}



