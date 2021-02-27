 package com.lisasmith.findAGig.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.repository.InstrumentRepository;

@Service
public class InstrumentService {
	
	private static final Logger logger = LogManager.getLogger(InstrumentService.class);
	public static Long currentInstrumentId;

	@Autowired
	private InstrumentRepository repo;
	
	public Iterable<Instrument> getInstruments() {
			logger.info("Finding all Instruments.");
			return repo.findAll();
	}
	
	public Instrument getInstrument(Long id) {
		logger.info("Finding one Instrument by id.");
		return repo.findOne(id);
}
	
	public Instrument createInstrument(Instrument newInstrument) throws Exception {
		try {
			Instrument createNewInstrument = new Instrument();
			logger.info("Creating new Instrument.");
			//create an instrument
			logger.info("In createInstrument");
			if (!doesInstrumentExist(newInstrument)) {
				logger.info("Instrument does not exist");
				createNewInstrument.setName(newInstrument.getName());
				logger.info("create instrument: " + createNewInstrument.getName());
				createNewInstrument = repo.save(createNewInstrument);
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

	public List<Instrument> createInstruments(List<Instrument> newInstruments) throws Exception {		
		
		List<Instrument> newCreations  = new ArrayList<Instrument>();
		try {
			logger.info("Creating new Instruments.");
			//create an instrument
			logger.info("In createInstruments");
			for (Instrument instrument : newInstruments) {
				if (!doesInstrumentExist(instrument)) {
					Instrument createNewInstrument = new Instrument();
					logger.info("Instrument does not exist");
					createNewInstrument.setName(instrument.getName());
					logger.info("create instrument: " + createNewInstrument.getName());
					newCreations.add(repo.save(createNewInstrument));
					logger.info("New Instrument: " + createNewInstrument.getName() + " is saved!");
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
	
	
	public boolean doesInstrumentExist(Instrument newInstrument) {
		boolean exists = false;
	
		 // Retrieve all instruments from the database
		 //	1.  check to see if this instrument is in the database.  
		 // 2.  if found, 
		 //			a.  Set exists to true
		 //			b.  Set the static variable to the instrument_id
		logger.info("In doesInstrumentExist()");

		 Iterable<Instrument> allInstruments = repo.findAll();
		 for (Instrument instrument : allInstruments ) {
			 if (instrument.getName().equals(newInstrument.getName()) || (instrument.getInstrumentId().equals(newInstrument.getInstrumentId()))) {
				 logger.info("Instrument matches");
				 exists = true;
				 currentInstrumentId = instrument.getInstrumentId();
				 return exists;
			 }
		 }	
		return exists;
	}
	
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
	
	public void removeInstrument(Long id) throws Exception  {
		try {
			repo.delete(id);
			logger.info("Deleting Instrument: " + id);
		} catch (Exception e) {
			logger.error("Exception occurred while deleting instrument: " + id, e);
			throw new Exception("Unable to delete instrument: " + id);
		}
		
	}
}
