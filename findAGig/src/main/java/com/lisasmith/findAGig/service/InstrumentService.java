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
import com.lisasmith.findAGig.repository.InstrumentRepository;

@Service
public class InstrumentService {
	
	private static final Logger logger = LogManager.getLogger(InstrumentService.class);
	public static Long currentInstrumentId;

	@Autowired
	private InstrumentRepository repo;
	
	// READ:  retrieve all instruments
	public Iterable<Instrument> getInstruments() {
		return repo.findAll();
	}
	
	// READ:  retrieve one instrument by id
	public Instrument getInstrument(Long id) {
		return repo.findOne(id);
}
	
	// CREATE:  create an instrument	
	public Instrument createInstrument(Instrument newInstrument) throws Exception {
		try {
			Instrument createNewInstrument = new Instrument();
			//create an instrument
			if (!doesInstrumentExist(newInstrument)) {
				createNewInstrument.setName(newInstrument.getName());
				createNewInstrument = repo.save(createNewInstrument);
				logger.info("Created instrument: " + createNewInstrument.getName());
				return createNewInstrument;
			} else {
				createNewInstrument = repo.findOne(currentInstrumentId);
				return createNewInstrument;
			}	
		} catch (Exception e) {
			logger.error("Exception occurred while trying to create an instrument.");
			throw new Exception("Unable to create an instrument.");
		}
	}
	
	// CREATE:  create instruments
	public List<Instrument> createInstruments(List<Instrument> newInstruments) throws Exception {		
		
		List<Instrument> newCreations  = new ArrayList<Instrument>();
		try {
			//create an instrument
			for (Instrument instrument : newInstruments) {
				if (!doesInstrumentExist(instrument)) {
					Instrument createNewInstrument = new Instrument();
					createNewInstrument.setName(instrument.getName());
					newCreations.add(repo.save(createNewInstrument));
					logger.info("Created instrument: " + createNewInstrument.getName());
				} else {
					newCreations.add(repo.findOne(currentInstrumentId));
				}	
			}
			return newCreations;
		} catch (Exception e) {
			logger.error("Exception occurred while trying to create an instrument.");
			throw new Exception("Unable to create an instrument.");
		}
	}
	
	// Does this Instrument exist?  return true or false
	public boolean doesInstrumentExist(Instrument newInstrument) {
		boolean exists = false;
	
		 // Retrieve all instruments from the database
		 //	1.  check to see if this instrument is in the database.  
		 // 2.  if found, 
		 //			a.  Set exists to true
		 //			b.  Set the static variable to the instrument_id

		 Iterable<Instrument> allInstruments = repo.findAll();
		 for (Instrument instrument : allInstruments ) {
			 if (instrument.getName().equals(newInstrument.getName()) || (instrument.getInstrumentId().equals(newInstrument.getInstrumentId()))) {
				 exists = true;
				 currentInstrumentId = instrument.getInstrumentId();
				 return exists;
			 }
		 }	
		return exists;
	}
	
	// UPDATE:  update the name of an instrument
	public Instrument updateInstrument(Instrument newInstrument, Long id) throws Exception {
		try {
			Instrument oldInstrument = repo.findOne(id);
			logger.info("Updating Instrument: " + id);
			oldInstrument.setName(newInstrument.getName());
			return repo.save(oldInstrument);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to update instrument: " + id, e);
			throw new Exception("Unable to update instrument: " + id);
		}
	}
	// DELETE:  This is ONLY allowed if an instrument is not being referenced by any user or gig!
	//  		FUTURE:  This can be enhanced in the future to be allowed by an ADMIN	
	public void removeInstrument(Long id) throws Exception  {
		// If this instrument has been assigned to a Gig, do not delete!
		Instrument instrument = repo.findOne(id);
		List<GigStatus> instrumentGigStatuses = instrument.getGigStatuses();
		for (GigStatus gigStatus : instrumentGigStatuses) {
			if (gigStatus.getInstrumentId().equals(id)) {
				logger.error("Delete Not Allowed -- Instrument is assigned to Gig(s) or Musician(s)!");
				throw new Exception("Instrument: " + id +  " can not be deleted!");
			}
		}
		
		// If this instrument has been assigned to a User, do not delete!
		List<User> musicians = instrument.getMusicians();
		for (User user : musicians) {
			for (Instrument userInstrument : user.getInstruments()) {
				if (userInstrument.getInstrumentId().equals(id)) {
					logger.error("Delete Not Allowed -- Instrument is assigned to Musician(s)!");
					throw new Exception("Instrument: " + id +  " can not be deleted!");
				}
			}
		}					
		try {
			repo.delete(id);
			logger.info("Deleting Instrument: " + id);	
		} catch (Exception e) {
			logger.error("Exception occurred in delete operation: " + id, e);
			throw new Exception(e.getMessage());
		}
	}
}


