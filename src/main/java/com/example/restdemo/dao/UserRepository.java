package com.example.restdemo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restdemo.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	List<User>findByName(String name, Pageable pageable);
}
