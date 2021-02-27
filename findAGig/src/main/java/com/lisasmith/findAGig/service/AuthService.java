package com.lisasmith.findAGig.service;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Credentials;
import com.lisasmith.findAGig.entity.User;
import com.lisasmith.findAGig.repository.UserRepository;


@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	//Check the database to authenticate the user
	//Is the password the same??
	
	public User register(Credentials cred) throws AuthenticationException {
		// Create a new instance of user
		User user = new User();
		// Set properties for the user -- use setUsername() and setHash()
		user.setUsername(cred.getUsername());
		user.setHash(BCrypt.hashpw(cred.getPassword(),BCrypt.gensalt()));
		// Save the new users.
		try {
			userRepository.save(user);
			return user;
		} catch (DataIntegrityViolationException e) {
			throw new AuthenticationException("Username is not available.");
		}
	}	
	
	public User login(Credentials cred) throws AuthenticationException {
	
			User user = userRepository.findByUsername(cred.getUsername());
			if ((user != null) && (BCrypt.checkpw(cred.getPassword(), user.getHash()))) {
				return user;
			} 
			throw new AuthenticationException("Username or password is invalid");
		
		
	}
}
