package com.lisasmith.findAGig.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.GigStatus;
import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.entity.User;
import com.lisasmith.findAGig.repository.GigStatusRepository;
import com.lisasmith.findAGig.repository.InstrumentRepository;
import com.lisasmith.findAGig.repository.UserRepository;


@Service
public class UserService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private InstrumentRepository instrumentRepo;
	
	@Autowired
	private GigStatusRepository gigStatusRepo;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private InstrumentService instrumentService;
	
	// READ:  retrieve all Users
	public Iterable<User> getUsers() {
		return userRepo.findAll();
	}
	
	// READ:  retrieve one User by Id
	public User getUser(Long userId) {
		return userRepo.findOne(userId);
	}

	// CREATE:  Create a new User
	public User createUser(User newUser) throws Exception {
		try {
			User user = new User();
			user.setAddress(addressService.createAddress(newUser.getAddress()));
 			user.setFirstName(newUser.getFirstName());
			user.setLastName(newUser.getLastName());
			user.setUserType(newUser.getUserType());
			user.setSystemAdmin(newUser.isSystemAdmin());
			user.setUsername(newUser.getUsername());			
			user.setInstruments(instrumentService.createInstruments(newUser.getInstruments()));
			user = userRepo.save(user);
			addUsertoInstruments(user);		
			logger.info("Created a new User.");			
			return user;

		} catch (Exception e) {
			logger.error("Exception occurred while trying to create a user.",e);
			throw new Exception(e.getMessage());
		}
	}
	
	//  Fix the references in the User record to include these instruments
	private void addUsertoInstruments(User user) throws Exception {
		List<Instrument> instruments = user.getInstruments();
		for (Instrument instrument : instruments) {
			Instrument addInstrument = instrumentRepo.findByName(instrument.getName());
			List<User> musicianList = new ArrayList<User>();
			musicianList = addInstrument.getMusicians();
			if (!(musicianList == null)) {
				addInstrument.getMusicians().add(user);
				instrumentRepo.save(addInstrument);
			} else {
				// If there is not a list of musicians associated with this instrument;
				User newUser = new User();

				List<User> newMusicians = new ArrayList<User>();				
				newUser = user;
				addInstrument.setMusicians(newMusicians);
				if ((addInstrument.getMusicians() != null) && (addInstrument.getMusicians().add(newUser))) {
					instrumentRepo.save(addInstrument);
				}
				addInstrument = instrumentRepo.findOne(instrument.getInstrumentId());
			}
		}	
	}
	
	//  Fix the references in the User record to include these instruments
	private void removeUserfromInstruments(User user) throws Exception {
		List<Instrument> instruments = user.getInstruments();
		for (Instrument instrument : instruments) {
			Instrument removeInstrument = instrumentRepo.findByName(instrument.getName());
			List<User> musicianList = new ArrayList<User>();
			musicianList = removeInstrument.getMusicians();
			if (!(musicianList == null)) {
				removeInstrument.getMusicians().remove(user);
				instrumentRepo.save(removeInstrument);
			} 
		}	
	}
	
	
	// UPDATE:  update a User record
	public User updateUser(User user, Long id) throws Exception {
		try {
			User oldUser = userRepo.findOne(id);
			oldUser.setFirstName(user.getFirstName());
			oldUser.setLastName(user.getLastName());
			oldUser.setSystemAdmin(user.isSystemAdmin());
			oldUser.setUserType(user.getUserType());
			//Address update
			oldUser.setAddress(addressService.createAddress(user.getAddress()));
			
			// if no instruments are passed in, remove the ones that exist
			if (user.getInstruments() == null) {
				//remove any instruments that exist for this user
				logger.info("Remove instruments from this User if they exist.");
				removeUserfromInstruments(oldUser);
				
			} else {
				removeUserfromInstruments(oldUser);
				oldUser.setInstruments(user.getInstruments());
				//attempt to get the PUT to work with new instruments
				addUsertoInstruments(oldUser);
			}
			logger.info("Updated user: " + id);
			return userRepo.save(oldUser);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to update user: " + id,e);
			throw new Exception(e.getMessage());
		}
	}
	
	// DELETE:  delete a User
	public void deleteUser(Long id) throws Exception {
		// If this user has been assigned to a Gig, do not delete!
		List<GigStatus> gigStatuses = (List<GigStatus>)gigStatusRepo.findAll();
		for (GigStatus gigStatus : gigStatuses) {
			if ((gigStatus.getMusicianId() != null) && (gigStatus.getMusicianId().equals(id))) {
				logger.error("User: " + id +  " can not be deleted, they are assigned to a Gig!  ");
				throw new UnsupportedOperationException ("User: " + id +  " can not be deleted.  They are assigned to a Gig!");
			}
		}		
		try {		
			// Check to see if there are instruments stored for this user.  
			//  	If so, remove them before deleting the user. 
			User user = userRepo.findOne(id);
			if (user.getInstruments() != null) {			
				removeUserfromInstruments(user);
			}
			userRepo.delete(id);   
			logger.info("Deleted user: " + id);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to delete user:" + id,e);
			throw new Exception(e.getMessage());
		}
	}
	
}
