package com.lisasmith.findAGig.repository;

import org.springframework.data.repository.CrudRepository;

import com.lisasmith.findAGig.entity.User;

public interface UserRepository extends CrudRepository<User,Long>{

	public User findByUsername(String username);
}
