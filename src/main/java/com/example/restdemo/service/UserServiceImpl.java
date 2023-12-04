package com.example.restdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restdemo.dao.UserRepository;
import com.example.restdemo.entity.User;

@Service
public class UserServiceImpl implements UserService{
	
	private UserRepository userRepository;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public List<User> findAll(Optional<Integer> optionalPageSize) {
		if (optionalPageSize.isPresent()) {
			Page<User> returnedUsers = userRepository.findAll(PageRequest.ofSize(optionalPageSize.get())); // If sort or indexes needed. Here is place
			return returnedUsers.toList();
		}
		return userRepository.findAll();
	}
	
	@Transactional
	@Override
	public User findById(int theId) {
		Optional<User> findById = userRepository.findById(theId);
		User theUser = null;
		if (findById.isPresent()) {
			theUser = findById.get();
		}
		return theUser;
	}

	@Transactional
	@Override
	public User save(User theEmployee) {
		return userRepository.save(theEmployee);
	}
	
	@Transactional
	@Override
	public void deleteById(int theId) {
		userRepository.deleteById(theId);
	}

	@Override
	public List<User> findAllByName(String name, Optional<Integer> pageSize) {
		if(pageSize.isPresent()) {
			return userRepository.findByName(name, PageRequest.ofSize(pageSize.get()));
		}
		
		return userRepository.findByName(name, Pageable.unpaged());
	}

	@Transactional
	@Override
	public void saveAll(List<User> toBeSaved) {
	       userRepository.saveAll(toBeSaved);
		
	}
}
