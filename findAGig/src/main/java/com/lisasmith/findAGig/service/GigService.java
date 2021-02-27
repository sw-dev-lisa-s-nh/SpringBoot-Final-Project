package com.lisasmith.findAGig.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Gig;
import com.lisasmith.findAGig.entity.GigStatus;
import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.entity.User;
import com.lisasmith.findAGig.repository.GigRepository;
import com.lisasmith.findAGig.repository.GigStatusRepository;
import com.lisasmith.findAGig.repository.InstrumentRepository;
import com.lisasmith.findAGig.repository.UserRepository;
import com.lisasmith.findAGig.util.GenreType;
import com.lisasmith.findAGig.util.StatusType;
import com.lisasmith.findAGig.util.UserType;

@Service
public class GigService {

	private static final Logger logger = LogManager.getLogger(UserService.class);
	
	@Autowired
	private GigRepository repo;
	
	@Autowired
	private GigStatusRepository statusRepo;
	
	@Autowired
	private InstrumentRepository instrumentRepo;
	
	@Autowired
	private UserRepository  userRepo;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private InstrumentService instrumentService;
	
	
	// CREATE:  Create a Gig
		public Gig createGig(Gig newGig) throws Exception {
			try {
				Gig gig = new Gig();
				Gig savedGig = new Gig();
				gig.setGigDate(newGig.getGigDate());
				gig.setGigStartTime(newGig.getGigStartTime());
				gig.setGigDuration(newGig.getGigDuration());
				logger.info("before createAddress");
				gig.setAddress(addressService.createAddress(newGig.getAddress()));
				logger.info("after createAddress");
				gig.setPhone(newGig.getPhone());
				gig.setEvent(newGig.getEvent());
				gig.setGenre(newGig.getGenre());
				gig.setDescription(newGig.getDescription());
				gig.setSalary(newGig.getSalary());
				gig.setEventStatus(newGig.getEventStatus());
				gig.setPlannerId(newGig.getPlannerId());
				logger.info("Create a Gig");
				savedGig = repo.save(gig);
				return savedGig;

			} catch (Exception e) {
				logger.error("Exception occurred while trying to create a gig.");
				throw new Exception ("Unable to create a gig.");
			}
		}
		
	// READ:  Return all Gigs
	public Iterable<Gig> getGigs() {
		logger.info("Finding all Gigs");
		return repo.findAll();
	}
	
	// READ:  Return all Gigs & Instruments Information
	public Iterable<Gig> getGigsAndGigStatuses() {
		logger.info("Finding all Gigs, & Instruments for each Gig!");
		List<Gig> allGigs = (List<Gig>) repo.findAll();
		int counter= 1;
		for (Gig oneGig : allGigs) {
			logger.info("Gigstatus: " + counter++);
			 oneGig.setGigStatuses((List<GigStatus>) getGigStatuses(oneGig.getGigId()));
		}
		return allGigs;		
	}
	
	// READ:  Return all Gigs & Instruments that are OPEN positions
	public Iterable<Gig> getGigsAndGigStatusesByOpen() {
		logger.info("Finding all Gigs, & Instruments for each Gig if OPEN!");
		List<Gig> matchedGigs = new ArrayList<Gig>();
		List<Gig> allGigs = (List<Gig>) repo.findAll();
		int counter= 1;
		for (Gig oneGig : allGigs) {
			boolean found = false;
			if ((oneGig.getEventStatus() != null) && (oneGig.getEventStatus().equals(StatusType.OPEN))) {
				logger.info("Gig: " + counter++);
				 List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
				 List<GigStatus> openList = new ArrayList<GigStatus>();
				 int counter1 = 1;
				 for (GigStatus gigStatus : tempList) {
					logger.info("Gigstatus: " + counter1++);
				 // loop through all gigStatuses, and if status is OPEN, add to a temporary LIST<GigStatus>
					 if (gigStatus.getStatus().equals(StatusType.OPEN)) {
				        openList.add(gigStatus);
				        found = true;
					 }
				 }
				 // Before end of loop, set oneGig.setGigStatuses(openList)); ==> OPEN GigStatus that is part of this Gig
				 if (found) {
					 oneGig.setGigStatuses(openList);
					 matchedGigs.add(oneGig);
				 }
			}
		}
		return matchedGigs;		
	}
	// READ:  Return all Gigs & Instruments that are in a particular state  (OPEN or ALL)
	public Iterable<Gig> getGigsAndGigStatusesByState(String stateName, boolean isByOpen) {
		logger.info("Finding all Gigs, & Instruments for each Gig if in state: " + stateName);
		List<Gig> allGigs = (List<Gig>) repo.findAll();
		List<Gig> matchingGigs = new ArrayList<Gig>();
		int counter= 1;
		for (Gig oneGig : allGigs) {
			logger.info("Gig: " + counter++);
			// If the Gig state equals the stateName passed in, check to see if there are any open positions.
			if (oneGig.getAddress().getState().equals(stateName)) {
				List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
				List<GigStatus> openList = new ArrayList<GigStatus>();
				int counter1 = 1;
				for (GigStatus gigStatus : tempList) {
					logger.info("Gigstatus: " + counter1++);
				 // loop through all gigStatuses, and if status is OPEN, add to a temporary LIST<GigStatus>
					 if (isByOpen) {
						 if (gigStatus.getStatus().equals(StatusType.OPEN)) {
							 openList.add(gigStatus);
						 }
					 } else {
				        openList.add(gigStatus);
					 }
				 }		 
			 // Before end of loop, set oneGig.setGigStatuses(openList)); ==> OPEN GigStatus that is part of this Gig
			 oneGig.setGigStatuses(openList);
			 matchingGigs.add(oneGig);
			}
		}
		return matchingGigs;		
	}
	
	// READ:  Return all Gigs & Instruments that are in a particular instrument  (OPEN or ALL)
		public Iterable<Gig> getGigsAndGigStatusesByInstrumentName(String instName, boolean isByOpen) {
			logger.info("Finding all Gigs, & Instruments for each Gig if they need a: " + instName);
			Instrument searchInstrument = instrumentRepo.findByName(instName);
			List<Gig> allGigs = (List<Gig>) repo.findAll();
			List<Gig> matchingGigs = new ArrayList<Gig>();
			int counter= 1;
			for (Gig oneGig : allGigs) {
				logger.info("Gig: " + counter++);
				boolean match = false;
				// get the GigStatus records for this Gig, and check to see if that position is for instName, 
				// and if it is OPEN.
				
				List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
				List<GigStatus> openList = new ArrayList<GigStatus>();
				int counter1 = 1;
				for (GigStatus gigStatus : tempList) {
					logger.info("Gigstatus: " + counter1++);
				 // loop through all gigStatuses, and if status is OPEN, add to a temporary LIST<GigStatus>
					 if (isByOpen) {
						 if ((gigStatus.getStatus().equals(StatusType.OPEN)) 
							 && (gigStatus.getInstrumentId().equals(searchInstrument.getInstrumentId()))) {
							 openList.add(gigStatus);
							 match = true;
						 }
					 } else {
						 if (gigStatus.getInstrumentId().equals(searchInstrument.getInstrumentId())) {
							 openList.add(gigStatus);
							 match = true;
						 }
					 }
				 }	
						 
				 // Before end of loop, set oneGig.setGigStatuses(openList)); ==> OPEN GigStatus that is part of this Gig
				if (match) {				
					oneGig.setGigStatuses(openList);
					matchingGigs.add(oneGig);
				}
			}
			return matchingGigs;		
		}
	
		// READ:  Return all Gigs & Instruments that are a particular Genre Type (OPEN or ALL)
		public Iterable<Gig> getGigsAndGigStatusesByGenreType(GenreType genreType, boolean isByOpen) {
			logger.info("Finding all Gigs, & Instruments for each Gig if by genre: " + genreType);
			List<Gig> allGigs = (List<Gig>) repo.findAll();
			List<Gig> matchingGigs = new ArrayList<Gig>();
			int counter= 1;
			for (Gig oneGig : allGigs) {
				logger.info("Gig: " + counter++);
				// If the Gig state equals the stateName passed in, check to see if there are any open positions.
				if (oneGig.getGenre().equals(genreType)) {
					List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
					List<GigStatus> openList = new ArrayList<GigStatus>();
					int counter1 = 1;
					for (GigStatus gigStatus : tempList) {
						logger.info("Gigstatus: " + counter1++);
					 // loop through all gigStatuses, and if status is OPEN, add to a temporary LIST<GigStatus>
						 if (isByOpen) {
							 if (gigStatus.getStatus().equals(StatusType.OPEN)) {
								 openList.add(gigStatus);
							 }
						 } else {
					        openList.add(gigStatus);
						 }
					 }		 
				 // Before end of loop, set oneGig.setGigStatuses(openList)); ==> OPEN GigStatus that is part of this Gig
				 oneGig.setGigStatuses(openList);
				 matchingGigs.add(oneGig);
				}
			}
			return matchingGigs;		
		}
		
	
	// READ:  Find all instruments associated with a particular gigId
	public Iterable<GigStatus> getGigStatuses(Long gigId) {
		logger.info("Finding all Instruments for a particularGig!");
		return statusRepo.findByGigId(gigId);			 
	}
	
	// READ:  Find all musicians associated with a particular GigId
	public Iterable<User> getGigStatusesWithMusicianInfo(Long gigId) {
		logger.info("Finding all Musicians for a particularGig!");
		Iterable<GigStatus> matchingGigStatuses = statusRepo.findByGigId(gigId);
		List<User> matchingMusicians = new ArrayList<User>();
		for (GigStatus gigStatus : matchingGigStatuses) {
			if (gigStatus.getMusicianId() != null) {
				matchingMusicians.add(userRepo.findOne(gigStatus.getMusicianId()));
				logger.info("Matching Status #: " + gigStatus.getId());
			}
		}
		return matchingMusicians;
	}

	// READ:  Find all Gigs assigned to a UserId  (ANY STATUS IS FINE)
	public Iterable<GigStatus> getGigStatusesByUserId(Long userId) {
		logger.info("Finding all Gigs for a particular UserId!");
		return statusRepo.findByMusicianId(userId);
	}
	 

	
	
	// In process here...  Not sure how to create a relationship between
	// Gig and all GigStatus in memory... so that I can then retrieve them from that list.
	// I only pass in one "instruments" in the JSON.  
	public Gig createGigAndGigStatuses(Gig newGig) throws Exception {
		try {
			Gig gig = new Gig();
			Gig savedGig = new Gig();
			gig.setGigDate(newGig.getGigDate());
			gig.setGigStartTime(newGig.getGigStartTime());
			gig.setGigDuration(newGig.getGigDuration());
			logger.info("before createAddress");
			gig.setAddress(addressService.createAddress(newGig.getAddress()));
			logger.info("after createAddress");
			gig.setPhone(newGig.getPhone());
			gig.setEvent(newGig.getEvent());
			gig.setGenre(newGig.getGenre());
			gig.setDescription(newGig.getDescription());
			gig.setSalary(newGig.getSalary());
			gig.setPlannerId(newGig.getPlannerId());
			logger.info("Create a Gig");
			savedGig = repo.save(gig);
					
			int counter = 1;
			// How do I get to the "Instruments" part of the Gig record? 
			Iterable<GigStatus> relevantGigStatuses = newGig.getGigStatuses();
			for (GigStatus status : relevantGigStatuses) {
				logger.info("adding gigStatuses to Gig: ", counter++);
				savedGig.setGigStatuses((List<GigStatus>) createGigStatus(status,savedGig.getGigId()));
			}
			return savedGig;

		} catch (Exception e) {
			logger.error("Exception occurred while trying to create a gig.");
			throw new Exception ("Unable to create a gig.");
		}
	}

	//  CREATE:  Create relationship between GigStatus table and instruments table
	private void addGigStatustoInstruments(GigStatus gigStatus) {
		logger.info("In addGigStatustoInstruments instrumentId: " + gigStatus.getInstrumentId());
		
		// Changed .findOne(instrument.getInstrumentId() -->  .findByName(instrument.getName()	
		Instrument instrument = instrumentRepo.findOne(gigStatus.getInstrumentId());
		Instrument addInstrument = instrument;

				
		List<GigStatus> gigStatuses = new ArrayList<GigStatus>();
		gigStatuses = addInstrument.getGigStatuses();

		logger.info("Instrument name: " + addInstrument.getName() + " id: " + addInstrument.getInstrumentId());
		if (gigStatuses != null) {
			logger.info("gigStatuses is NOT NULL");
			addInstrument.getGigStatuses().add(gigStatus);
			instrumentRepo.save(addInstrument);
			logger.info("After add(gigStatus)");		
		} else {
			//create a list, and add the instrument
			logger.info("gigStatuses is NULL!");
			GigStatus newGigStatus = new GigStatus();
			
			List<GigStatus> newGigStatuses = new ArrayList<GigStatus>();
			newGigStatus = gigStatus;
			logger.info("newGigStatus id: " + newGigStatus.getId() + " gig_id: " + newGigStatus.getGigId());
			
			addInstrument.setGigStatuses(newGigStatuses);	
			logger.info("newGigStatuses: " + addInstrument.getGigStatuses());
			logger.info("After newInstrument.setGigStatuses()");
			if ((addInstrument.getGigStatuses() != null) && (addInstrument.getGigStatuses().add(newGigStatus))) {
				logger.info("After adding newInstrument in addInstrument.getGigStatuses().add(newGigStatus)");
				logger.info("Re-save addInstrument in the repo");
				instrumentRepo.save(addInstrument);
				logger.info("after instrumentRepo.save(addInstrument)");
			}
		}
		addInstrument = instrumentRepo.findOne(instrument.getInstrumentId());
	}
	
	
	// CREATE:  Create instrument records for a particular Gig by gigId
	public Iterable<GigStatus> createGigStatus(GigStatus gigStatus, Long gigId) throws Exception {
		try {
			List<GigStatus> savedGigStatuses = new ArrayList<GigStatus>();			
			Gig oldGig = repo.findOne(gigId);
			logger.info("In createGigStatus");
			
			GigStatus createGigStatus = new GigStatus();
			
			logger.info("gigStatus.getInstruments(): " + gigStatus.getInstruments());
			createGigStatus.setInstruments(instrumentService.createInstruments(gigStatus.getInstruments()));
			logger.info("after createInstruments");
			
			List<Instrument> gigRequiredInstruments = new ArrayList<Instrument>();		
			gigRequiredInstruments = createGigStatus.getInstruments();
			
			if (gigRequiredInstruments != null) {
				logger.info("gigRequiredInstruments is NOT null!");
				logger.info("After gigRequiredInstruments: " + gigRequiredInstruments.size());
				Double splitSalary = (oldGig.getSalary()/gigRequiredInstruments.size());
				logger.info("Salary:  " + splitSalary);
				
				for (Instrument inst : gigRequiredInstruments) {
					logger.info("In createGigStatus instrument inst name:" + inst.getName());
					GigStatus newGigStatus = new GigStatus();
					newGigStatus.setGigId(oldGig.getGigId());
					newGigStatus.setStatus(StatusType.OPEN);
					newGigStatus.setSalary(splitSalary);
					newGigStatus.setInstruments(instrumentService.createInstruments(gigRequiredInstruments));
					
					for (Instrument oneInst : newGigStatus.getInstruments()) {
						if (oneInst.getName().equals(inst.getName())) {
							logger.info("Adding Instrument: " + inst.getName() + " with id: " + inst.getInstrumentId());
							newGigStatus.setInstrumentId(oneInst.getInstrumentId());
						}
					}
					
					// Create a new GigStatus
					newGigStatus = statusRepo.save(newGigStatus);

					logger.info("Before addGigStatustoInstruments");
					addGigStatustoInstruments(newGigStatus);
					logger.info("After addGigStatustoInstruments");
					savedGigStatuses.add(newGigStatus);

					logger.info("Adding Instrument: " + inst.getInstrumentId() + " and GigId: " + oldGig.getGigId());
				} 

			}
			return savedGigStatuses;
			
		} catch (Exception e) {
				logger.error("Exception occurred while trying to create a Gig Status.");
				throw new Exception("Unable to add an instrument to a Gig.");
		}
					
	}
	
	// CONFIRM a musician in a particular Gig By the Gig Planner	
	public GigStatus updateGigStatusConfirm(GigStatus gigStatus, Long gigId, Long musicianId, Long plannerId) throws Exception {
		
		logger.info("In updateGigStatusConfirm!");
		try {
			//Retrieve all GigStatuses associated with the passed in GigId
			Gig requestedGig = repo.findOne(gigId);
			
			Iterable<GigStatus> matchedGigStatuses = statusRepo.findByGigId(gigId);
			logger.info("In updateGigStatusConfirm -- after findByGigId");
			
			int counter = 1;
			for (GigStatus anotherGigStatus : matchedGigStatuses) {
				logger.info("Counter: " + counter++ + "  GigStatus #: " + anotherGigStatus.getId());
			}
			//Retrieve THE Instrument RECORD of the requested Instrument!  
			Instrument requestedInstrument = new Instrument();
			requestedInstrument = findMatchingInstrument(gigStatus.getInstruments());
			logger.info("In updateGigStatusConfirm -- requestedInstrument is: " + requestedInstrument.getName());
			
			
			// Iterate through the gigStatuses, and CONFIRM the ONE requested by this musician
			counter = 1;
			for (GigStatus matchGigStatus : matchedGigStatuses) {
				logger.info("GigStatus #: " + matchGigStatus.getId());
				logger.info("Loop #: " + counter++ + " through GigStatus for Gig: " + gigId);
				// ONLY the registered planner can CONFIRM a musician for a Gig!
				// if plannerId matches Gig.plannerId(), &&  musicianId matches GigStatus.musicianId && gigStatus is REQUESTED
				
				logger.info("PlannerId of requestedGig: " + requestedGig.getPlannerId());
				logger.info("MusicianId of matchGigStatus: " + matchGigStatus.getMusicianId());
				logger.info("Status of matchGigStatus: " + matchGigStatus.getStatus());
				
				if ((requestedGig.getPlannerId().equals(plannerId)) && (matchGigStatus.getStatus().equals(StatusType.REQUESTED))) {				
					if ((matchGigStatus.getMusicianId() != null) && (matchGigStatus.getMusicianId().equals(musicianId))) {
						logger.info("Matching plannerId, musicianId and status!");
						
						// CONFIRM that the PLANNER wants to match this Instrument -- in case the musician requested more than one!
						if (matchGigStatus.getInstrumentId().equals(requestedInstrument.getInstrumentId())) {
							// Change status to CONFIRMED.
							logger.info("Matching instrument");
							matchGigStatus.setStatus(StatusType.CONFIRMED);
							statusRepo.save(matchGigStatus);
							logger.info("Musician: " + musicianId + " has been CONFIRMED for Gig: " + gigId + " by Planner: " + plannerId);
							return matchGigStatus;
						} else {
							logger.error("User instrument does not match!"); 
						}
					}
				}
			}
			logger.info("End of Loop -- No gigstatus found!");
		} catch (Exception e) {
			logger.error("Exception occurred while trying to hire a musician -- updating a Gig's status.");
			throw new Exception("Unable to update gig: " +  gigId  + " with userId: " + musicianId);
		}	
		logger.error("Exception occurred while trying to hire a musician -- updating a Gig's status.");
		throw new Exception("Unable to update gig: " +  gigId  + " with userId: " + musicianId);
	}
	
	// UPDATE:  Update a GigStatus -- REQUEST a GIG etc.
	public GigStatus updateGigStatus(GigStatus gigStatus, Long gigId, Long userId) throws Exception {	
		
		GigStatus relatedGigStatus = new GigStatus();

		// Initialize the boolean for USER INSTRUMENT MATCH an instrument for the gig
		boolean matchInstrument = false;
		boolean matchUser = false;
		logger.info("In updateGigStatus");
		try {		
			Iterable<GigStatus> matchedGigStatuses = new ArrayList<GigStatus>(); 
			
		//Retrieve all GigStatuses associated with the passed in GigId
			matchedGigStatuses = statusRepo.findByGigId(gigId);
			logger.info("In updateGigStatus -- after findByGigId");

		//Retrieve THE Instrument RECORD of the requested Instrument!  
			Instrument requestedInstrument = new Instrument();
			requestedInstrument = findMatchingInstrument(gigStatus.getInstruments());
			logger.info("In updateGigStatus -- requestedInstrument is: " + requestedInstrument.getName());
			
		//  Get the requested Instrument from the repository			
		// 		FIND IF REQUESTED INSTRUMENT matches an OPEN GIGSTATUS record for this Gig
			User theMatchUser  = new User();
			//GigStatus theMatchStatus = new GigStatus();
			for (GigStatus matchGigStatus : matchedGigStatuses) {
				if (matchGigStatus.getStatus().equals(StatusType.OPEN)) {
					logger.info("matchGigStatus.getId(): " + matchGigStatus.getId() + " matchGigStatus.getStatus(): " + matchGigStatus.getStatus());
					if ((matchGigStatus.getInstrumentId().equals(requestedInstrument.getInstrumentId()))
							&& (matchInstrument == false)){
						logger.info("matchGigStatus.getId(): " + matchGigStatus.getId() + " matchGigStatus.getStatus(): " + matchGigStatus.getStatus());
						matchInstrument = true;
						relatedGigStatus = matchGigStatus;
						logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " status: " + relatedGigStatus.getStatus());
						logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " gigId: " + relatedGigStatus.getGigId());
						logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " musicianId: " + relatedGigStatus.getMusicianId());
						logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " salary: " + relatedGigStatus.getSalary());
						logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " instrumentId: " + relatedGigStatus.getInstrumentId());
					}
				}
			}
			logger.info("After for loop -- Matched GigStatus:  " + relatedGigStatus.getId() + " status: " + relatedGigStatus.getStatus());

			
		// Find USER record in database, and get their instruments, and make sure that the correct instrument matches
			User relatedUser = userRepo.findOne(userId);
			Iterable<Instrument>  relatedUserInstruments = relatedUser.getInstruments();		
			for (Instrument instrument : relatedUserInstruments) {
				if (instrument.getInstrumentId().equals(relatedGigStatus.getInstrumentId())) {
					matchUser = true;
					theMatchUser  = relatedUser;
				}
			}
						
		//  IF the user is a MUSICIAN
			if (theMatchUser.getUserType().equals(UserType.MUSICIAN)){
				
			// IF the instrument matches  
				if (matchInstrument == true) {
					
				// IF Status is OPEN --> change status to REQUESTED 
					if (relatedGigStatus.getStatus().equals(StatusType.OPEN) && (matchUser == true)) {
						logger.info("status is OPEN for GigStatus Id: " + relatedGigStatus.getId());
						
						//If new status is REQUESTED, set it, and set Musician_ID to userId
						if (gigStatus.getStatus().equals(StatusType.REQUESTED)) {
							logger.info("looking to make a request");
							relatedGigStatus.setStatus(StatusType.REQUESTED);
							relatedGigStatus.setMusicianId(theMatchUser.getId());
							logger.info("before save(relatedGigStatus)");
							logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " status: " + relatedGigStatus.getStatus());
							logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " gigId: " + relatedGigStatus.getGigId());
							logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " musicianId: " + relatedGigStatus.getMusicianId());
							logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " salary: " + relatedGigStatus.getSalary());
							logger.info("Matched GigStatus:  " + relatedGigStatus.getId() + " instrumentId: " + relatedGigStatus.getInstrumentId());
							statusRepo.save(relatedGigStatus);
							logger.info("after save(relatedGigStatus)");

							return relatedGigStatus;
						} else {
							logger.error("Invalid Request for an OPEN GIG.");
						}
						
					} else {
						logger.error("Requested Gig Can not be assigned.");			
					}
				} else {
					logger.error("Instrument requested does not match.");
				}
			} else {
				logger.error("UserType is not valid.");
			
			}  // End of for every gigStatus in this gig (matches gigId)

		} catch  (Exception e) {
			logger.error("Exception occurred while trying to update a Gig's status.");
			throw new Exception("Unable to update gigStatus: " + relatedGigStatus.getId() + " with userId: " + userId);
		}	
		logger.error("Exception occurred while trying to update a Gig's status.");
		throw new Exception("Unable to update gigStatus: " + relatedGigStatus.getId() + " with userId: " + userId);
	}
	
	//  Retrieve a matching instrument record.
	public Instrument findMatchingInstrument(List<Instrument> requestedInstruments) throws Exception {
		//List<Instrument> allMatchingInstruments = new ArrayList<Instrument>();
		Iterable<Instrument> allInstruments = new ArrayList<Instrument>();
		allInstruments = instrumentRepo.findAll();			
		for (Instrument matchedInstrument : allInstruments) {
			for (Instrument requestedInstrument : requestedInstruments) {
				if ((matchedInstrument.getName().equals(requestedInstrument.getName())) ||
						(matchedInstrument.getInstrumentId().equals(requestedInstrument.getInstrumentId()))){
					return matchedInstrument;
				}			
			}
		}
		logger.error("No instrument found!");
		throw new Exception("No instrument found!");
	}
	
	public Gig updateGig(Gig newGig, Long gigId) throws Exception {
		try {
			Gig oldGig = repo.findOne(gigId);
			oldGig.setGigDate(newGig.getGigDate());
			oldGig.setGigStartTime(newGig.getGigStartTime());
			oldGig.setGigDuration(newGig.getGigDuration());
			oldGig.setPhone(newGig.getPhone());
			oldGig.setEvent(newGig.getEvent());
			oldGig.setGenre(newGig.getGenre());
			oldGig.setPlannerId(newGig.getPlannerId());
			oldGig.setSalary(newGig.getSalary());
			oldGig.setDescription(newGig.getDescription());
			logger.info("Updating gig: " + gigId);
			Gig savedGig = new Gig();
			savedGig = repo.save(oldGig);
						
			// Populate GigStatus Table
			// Maybe a @JsonIgnore in Gig??
			
//			Set<Instrument> gigRequiredInstruments = new HashSet<Instrument>();
//			gigRequiredInstruments = savedGig.getInstruments();
//			for (Instrument inst : gigRequiredInstruments) {
//				GigStatus gigStatus = new GigStatus();
//				gigStatus.setGigId(savedGig.getGigId());
//				gigStatus.setStatus(StatusType.OPEN);
//				gigStatus.setSalary(savedGig.getSalary());
//				logger.info("Adding Instrument: " + inst.getInstrumentId() + " to GigId: " + savedGig.getGigId());
//				gigStatus.setInstrumentId(inst.getInstrumentId());
//				statusRepo.save(gigStatus);
//			}			
			return savedGig;
		} catch (Exception e) {
			logger.error("Exception occurred while trying to update a gig.");
			throw new Exception("Unable to update gig id:" + gigId);
		}
	}	
		
	// DELETE:  Code to delete a Gig by gigId...  only works if it has no dependencies, 
	//			This is BY DESIGN -- because once dependencies are created, the information
	//			needs to be saved to Inform Musicians of the CANCELLED or CLOSED status
	//			of a Gig!
	public void deleteGig(Long gigId) throws Exception {	
		try {
			Iterable<GigStatus> gigStatusByGigId = getGigStatuses(gigId);
			for (GigStatus status : gigStatusByGigId){
				statusRepo.delete(status);
			}
			repo.delete(gigId);
			logger.info("Deleted gig: " + gigId);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to delete gig:" + gigId);
			throw new Exception("Unable to delete gig.");
		}
	}
}

