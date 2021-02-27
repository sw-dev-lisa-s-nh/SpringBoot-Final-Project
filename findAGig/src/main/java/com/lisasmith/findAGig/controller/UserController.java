package com.lisasmith.findAGig.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lisasmith.findAGig.entity.Credentials;
import com.lisasmith.findAGig.entity.User;
import com.lisasmith.findAGig.service.AuthService;
import com.lisasmith.findAGig.service.GigService;
import com.lisasmith.findAGig.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	
	@Autowired
	private UserService service;
	
	@Autowired
	private GigService gigService;
	
	@Autowired
	private AuthService authService;
	
	// POST /register
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public ResponseEntity<Object> register(@RequestBody Credentials cred) {
		try {
			return new ResponseEntity<Object>(authService.register(cred), HttpStatus.CREATED);
		} catch (AuthenticationException e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// POST /login
	@RequestMapping(value="/login", method = RequestMethod.POST)
	public ResponseEntity<Object> login(@RequestBody Credentials cred) {
		try {
			return new ResponseEntity<Object>(authService.login(cred), HttpStatus.OK);
		} catch (AuthenticationException e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	// READ all users
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Object> getUsers() {
		return new ResponseEntity<Object>(service.getUsers(), HttpStatus.OK);
	}
	
	// READ one user by id
		@RequestMapping(value="/{id}", method=RequestMethod.GET)
		public ResponseEntity<Object> getUser(@PathVariable Long id) {
			return new ResponseEntity<Object>(service.getUser(id), HttpStatus.OK);
		}


	// CREATE a user (MUSICIAN or PLANNER)
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> createUser(@RequestBody User user) throws Exception {
		return new ResponseEntity<Object>(service.createUser(user), HttpStatus.CREATED);
	}
	
	// READ all gigs that match user by {id} -- USER HAS REQUESTED OR IS CONFIRMED FOR THAT GIG!
	@RequestMapping(value="/{id}/gigs",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigStatusesByUserId(@PathVariable Long id) throws Exception {
		return new ResponseEntity<Object>(gigService.getGigStatusesByUserId(id), HttpStatus.CREATED);
	}
			
	// UPDATE a user
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Object> updateUser(@RequestBody User user, @PathVariable Long id) {
		try {
			return new ResponseEntity<Object>(service.updateUser(user, id), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}		
	}
	
	// DELETE a user
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
		try {
			// Do I need to remove any references to this USER in GigStatus? (musicianId)  or in Gig? (plannerId) <-- (NULL)
			service.deleteUser(id);
			return new ResponseEntity<Object>("Successfully deleted user with id: " + id, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}
