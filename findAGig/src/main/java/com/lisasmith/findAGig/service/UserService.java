package com.lisasmith.findAGig.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.entity.User;
import com.lisasmith.findAGig.repository.InstrumentRepository;
import com.lisasmith.findAGig.repository.UserRepository;



@Service
public class UserService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private InstrumentRepository instrumentRepo;
	
	//@Autowired 
	//private GigRepository gigRepo;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private InstrumentService instrumentService;
	
	
	public Iterable<User> getUsers() {
		logger.info("Finding all Users");
		return userRepo.findAll();
	}
	
	public User getUser(Long userId) {
		logger.info("Finding One User By Id: " + userId);
		return userRepo.findOne(userId);
	}

	public User createUser(User newUser) throws Exception {
		try {
			User user = new User();
			logger.info("before createAddress");
			user.setAddress(addressService.createAddress(newUser.getAddress()));
			logger.info("after createAddress");
 			user.setFirstName(newUser.getFirstName());
			user.setLastName(newUser.getLastName());
			user.setUserType(newUser.getUserType());
			user.setSystemAdmin(newUser.isSystemAdmin());
			user.setUsername(newUser.getUsername());

			logger.info("before createInstruments");
			
			user.setInstruments(instrumentService.createInstruments(newUser.getInstruments()));
			logger.info("after createInstruments");

			user = userRepo.save(user);
			logger.info("Creating a new User.");
			
			addUsertoInstruments(user);
			
			logger.info("Added user to Instruments.");
			//return userRepo.save(user);
			return user;

		} catch (Exception e) {
			logger.error("Exception occurred while trying to create a user.");
			throw new Exception("Unable to create a new user.");
		}
	}
	
	private void addUsertoInstruments(User user) throws Exception {
		List<Instrument> instruments = user.getInstruments();
		for (Instrument instrument : instruments) {
			logger.info("In Instrument loop of addUsertoInstruments");
			// Changed .findOne(instrument.getInstrumentId() -->  .findByName(instrument.getName()
			Instrument addInstrument = instrumentRepo.findByName(instrument.getName());
			logger.info("Instrument: " + addInstrument.getName() + "  Id: " + addInstrument.getInstrumentId());
			List<User> musicianList = new ArrayList<User>();
			musicianList = addInstrument.getMusicians();
			if (!(musicianList == null)) {
				logger.info("musicianList is NOT null!");
				addInstrument.getMusicians().add(user);
				logger.info("After adding newUser in addInstrument.getMusicians().add(user)");
				logger.info("Re-save addInstrument in the repo");
				instrumentRepo.save(addInstrument);
			} else {
				// If there is not a list of musicians associated with this instrument;
				logger.info("musicianList is NULL!");
				User newUser = new User();

				List<User> newMusicians = new ArrayList<User>();				
				newUser = user;
				logger.info("newUser id: " + newUser.getId() + " firstName: " + newUser.getFirstName() + " lastName: " + newUser.getLastName()
						+ " userType: " + newUser.getUserType());
					
				addInstrument.setMusicians(newMusicians);
				logger.info("newMusicians: " + addInstrument.getMusicians());
				logger.info("After addInstrument.setMusicians()");
				if ((addInstrument.getMusicians() != null) && (addInstrument.getMusicians().add(newUser))) {
					logger.info("After adding newUser in addInstrument.getMusicians().add(NewUser)");
					logger.info("Re-save addInstrument in the repo");
					instrumentRepo.save(addInstrument);
				}
				addInstrument = instrumentRepo.findOne(instrument.getInstrumentId());
			}
		}	
	}
	

	public User updateUser(User user, Long id) throws Exception {
		try {
			User oldUser = userRepo.findOne(id);
			oldUser.setFirstName(user.getFirstName());
			oldUser.setLastName(user.getLastName());
			oldUser.setSystemAdmin(user.isSystemAdmin());
			oldUser.setUserType(user.getUserType());
			oldUser.setInstruments(user.getInstruments());
			//attempt to get the PUT to work with new instruments
			addUsertoInstruments(oldUser);
			logger.info("Updating user: " + id);
			return userRepo.save(oldUser);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to update a user.");
			throw new Exception("Unable to update user id:" + id);
		}
	}
	
	public void deleteUser(Long id) throws Exception {
		try {
			userRepo.delete(id);
			logger.info("Deleted user: " + id);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to delete user:" + id);
			throw new Exception("Unable to delete user.");
		}
	}
	
}
