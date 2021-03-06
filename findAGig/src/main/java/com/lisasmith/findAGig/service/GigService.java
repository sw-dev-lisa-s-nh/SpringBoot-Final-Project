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

	private static final Logger logger = LogManager.getLogger(GigService.class);
	
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
	
	
	// CREATE:  Create a Gig  (First Implementation!)
	//			This currently only creates the main Gig record, you must call another routine 
	//			to add the instruments 
		public Gig createGig(Gig newGig) throws Exception {
			try {
				Gig gig = new Gig();
				Gig savedGig = new Gig();
				gig.setGigDate(newGig.getGigDate());
				gig.setGigStartTime(newGig.getGigStartTime());
				gig.setGigDuration(newGig.getGigDuration());
				gig.setAddress(addressService.createAddress(newGig.getAddress()));
				gig.setPhone(newGig.getPhone());
				gig.setEvent(newGig.getEvent());
				gig.setGenre(newGig.getGenre());
				gig.setDescription(newGig.getDescription());
				gig.setSalary(newGig.getSalary());
				gig.setEventStatus(newGig.getEventStatus());
				gig.setPlannerId(newGig.getPlannerId());
				gig.setGigStatuses(newGig.getGigStatuses());
				savedGig = repo.save(gig);
				logger.info("Created a Gig");				
				return savedGig;

			} catch (Exception e) {
				logger.error("Exception occurred while trying to create a Gig.",e);
				throw new Exception (e.getMessage());
			}
		}
		
	// READ:  Return all Gigs -- NO MATTER 
	//			what the Gig eventStatus is set to:  PLANNED, OPEN, CANCELLED, CLOSED
	public Iterable<Gig> getGigs() throws Exception {
		try {
			return repo.findAll();
		} catch (Exception e) {
			logger.error("Retrieve gigs failed!",e);
			throw new Exception(e.getMessage());
		}
		
	}
	
	// READ:  Return all Gigs & Instruments Information -- NO MATTER
	//			what the Gig eventStatus is set to:  PLANNED, OPEN, CANCELLED, CLOSED
	public Iterable<Gig> getGigsAndGigStatuses() {
		logger.info("Finding all Gigs & Instruments for each Gig!");
		List<Gig> allGigs = (List<Gig>) repo.findAll();
		for (Gig oneGig : allGigs) {
			 oneGig.setGigStatuses((List<GigStatus>) getGigStatuses(oneGig.getGigId()));
		}
		return allGigs;		
	}
	
	// READ:  Return one Gigs & Instruments Information -- NO MATTER
	//			what the Gig eventStatus is set to:  PLANNED, OPEN, CANCELLED, CLOSED	
	public Gig getGigAndGigStatusesById(Long Id) {
		logger.info("Finding one Gig: " + Id + "& Instruments for that Gig!");
		Gig requestedGig = repo.findOne(Id);
		requestedGig.setGigStatuses((List<GigStatus>) getGigStatuses(requestedGig.getGigId()));
		return requestedGig;		
	}
	
	// READ:  Return all Gigs & Instruments that are OPEN positions
	public Iterable<Gig> getGigsAndGigStatusesByOpen() {
		logger.info("Finding all Gigs & Instruments for each Gig if OPEN!");
		List<Gig> matchedGigs = new ArrayList<Gig>();
		List<Gig> allGigs = (List<Gig>) repo.findAll();
		for (Gig oneGig : allGigs) {
			boolean found = false;
			// Check status at the Gig level first!  Only return Gigs that are OPEN
			if ((oneGig.getEventStatus() != null) && (oneGig.getEventStatus().equals(StatusType.OPEN))) {
				 List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
				 List<GigStatus> openList = new ArrayList<GigStatus>();
				 for (GigStatus gigStatus : tempList) {
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
		logger.info("Finding all Gigs & Instruments for each Gig if in state: " + stateName);
		List<Gig> allGigs = (List<Gig>) repo.findAll();
		List<Gig> matchingGigs = new ArrayList<Gig>();
		boolean useThisGig = true;
		for (Gig oneGig : allGigs) {
			if (isByOpen) {
				if ((oneGig.getEventStatus() != null) && (!(oneGig.getEventStatus().equals(StatusType.OPEN))))  {
					useThisGig = false;
				}
			}
			if (useThisGig) {
				// If the Gig state equals the stateName passed in, check to see if there are any open positions.
				if (oneGig.getAddress().getState().equals(stateName)) {
					List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
					List<GigStatus> openList = new ArrayList<GigStatus>();
					for (GigStatus gigStatus : tempList) {
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
			useThisGig = true;
		}
		return matchingGigs;		
	}
	
	// READ:  Return all Gigs & Instruments that are in a particular instrument  (OPEN or ALL)
		public Iterable<Gig> getGigsAndGigStatusesByInstrumentName(String instName, boolean isByOpen) {
			logger.info("Finding all Gigs & Instruments for each Gig if they need a: " + instName);
			Instrument searchInstrument = instrumentRepo.findByName(instName);
			List<Gig> allGigs = (List<Gig>) repo.findAll();
			List<Gig> matchingGigs = new ArrayList<Gig>();
			boolean useThisGig = true;
			for (Gig oneGig : allGigs) {
				if (isByOpen) {
					if ((oneGig.getEventStatus() != null) && (!(oneGig.getEventStatus().equals(StatusType.OPEN))))  {
						useThisGig = false;
					}
				}
				if (useThisGig) {
					boolean match = false;
					// get the GigStatus records for this Gig, and check to see if that position is for instName, 
					// and if it is OPEN.
					
					List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
					List<GigStatus> openList = new ArrayList<GigStatus>();
					for (GigStatus gigStatus : tempList) {
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
				useThisGig = true;
			}
			return matchingGigs;		
		}
	
		// READ:  Return all Gigs & Instruments that are a particular Genre Type (OPEN or ALL)
		public Iterable<Gig> getGigsAndGigStatusesByGenreType(GenreType genreType, boolean isByOpen) {
			logger.info("Finding all Gigs & Instruments for each Gig if by genre: " + genreType);
			List<Gig> allGigs = (List<Gig>) repo.findAll();
			List<Gig> matchingGigs = new ArrayList<Gig>();
			boolean useThisGig = true;
			for (Gig oneGig : allGigs) {
				if (isByOpen) {
					if ((oneGig.getEventStatus() != null) && (!(oneGig.getEventStatus().equals(StatusType.OPEN))))  {
						useThisGig = false;
					}
				}
				if (useThisGig) {
					// If the Gig state equals the stateName passed in, check to see if there are any open positions.
					if (oneGig.getGenre().equals(genreType)) {
						List<GigStatus> tempList = (List<GigStatus>) getGigStatuses(oneGig.getGigId());
						List<GigStatus> openList = new ArrayList<GigStatus>();
						for (GigStatus gigStatus : tempList) {
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
				useThisGig = true;
			}
			return matchingGigs;		
		}
		
	
	// READ:  Find all instruments associated with a particular gigId
	public Iterable<GigStatus> getGigStatuses(Long gigId) {
		logger.info("Finding all Instruments for Gig: " + gigId);
		return statusRepo.findByGigId(gigId);			 
	}

	
	// READ:  Find all musicians associated with a particular GigId
	public Iterable<User> getGigStatusesWithMusicianInfo(Long gigId) {
		logger.info("Finding all Musicians for gig: " + gigId);
		Iterable<GigStatus> matchingGigStatuses = statusRepo.findByGigId(gigId);
		List<User> matchingMusicians = new ArrayList<User>();
		for (GigStatus gigStatus : matchingGigStatuses) {
			if (gigStatus.getMusicianId() != null) {
				matchingMusicians.add(userRepo.findOne(gigStatus.getMusicianId()));
			}
		}
		return matchingMusicians;
	}

	// READ:  Find all Gigs assigned to a UserId  (ANY STATUS IS FINE)
	public Iterable<GigStatus> getGigStatusesByUserId(Long userId) {
		logger.info("Finding all Gigs for userId: " + userId);
		return statusRepo.findByMusicianId(userId);
	}
	 	
	
	// CREATE:  Create a Gig  (Upgraded Implementation!)
	//			This creates the main Gig record, and all of the GigStatus records
	//			to add the instruments 	
	public Gig createGigAndGigStatuses(Gig newGig) throws Exception {
		try {
			Gig gig = new Gig();
			Gig savedGig = new Gig();
			gig.setGigDate(newGig.getGigDate());
			gig.setGigStartTime(newGig.getGigStartTime());
			gig.setGigDuration(newGig.getGigDuration());
			gig.setAddress(addressService.createAddress(newGig.getAddress()));
			gig.setPhone(newGig.getPhone());
			gig.setEvent(newGig.getEvent());
			gig.setGenre(newGig.getGenre());
			gig.setDescription(newGig.getDescription());
			gig.setSalary(newGig.getSalary());
			gig.setPlannerId(newGig.getPlannerId());
			gig.setEventStatus(newGig.getEventStatus());
			savedGig = repo.save(gig);
			gig.setGigStatuses(newGig.getGigStatuses());
			List<GigStatus> inputGigStatuses  = gig.getGigStatuses();
			List<GigStatus> newGigStatuses = new ArrayList<GigStatus>();
			if (inputGigStatuses != null) {
					newGigStatuses = (List<GigStatus>)createGigStatusWithinGig(inputGigStatuses, gig.getGigId());
					logger.info("All instruments connected to newly created Gig: " + savedGig.getGigId());
				}
			savedGig.setGigStatuses(newGigStatuses);
			return repo.save(savedGig);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to create a Gig or associated Instruments.",e);
			throw new Exception (e.getMessage());
		}
	}
	
	// CREATE:  Create instrument records for a particular Gig by gigId
		public Iterable<GigStatus> createGigStatusWithinGig(List<GigStatus> gigStatusList, Long gigId) throws Exception {
			try {
				List<GigStatus> savedGigStatuses = new ArrayList<GigStatus>();			
				Gig oldGig = repo.findOne(gigId);
				
				GigStatus createGigStatus = new GigStatus();
				int counter=1;
				for (GigStatus gigStatus : gigStatusList ) {
					logger.info("Counter: " + counter++);
					createGigStatus.setInstruments(instrumentService.createInstruments(gigStatus.getInstruments()));
					
					List<Instrument> gigRequiredInstruments = new ArrayList<Instrument>();		
					gigRequiredInstruments = createGigStatus.getInstruments();
					
					if (gigRequiredInstruments != null) {
						Double splitSalary = (oldGig.getSalary()/gigRequiredInstruments.size());

						for (Instrument inst : gigRequiredInstruments) {
							GigStatus newGigStatus = new GigStatus();
							newGigStatus.setGigId(oldGig.getGigId());
							newGigStatus.setStatus(StatusType.OPEN);
							newGigStatus.setSalary(splitSalary);
							newGigStatus.setInstruments(instrumentService.createInstruments(gigRequiredInstruments));
							
							for (Instrument oneInst : newGigStatus.getInstruments()) {
								if (oneInst.getName().equals(inst.getName())) {
									newGigStatus.setInstrumentId(oneInst.getInstrumentId());
								}
							}				
							// Create a new GigStatus
							newGigStatus = statusRepo.save(newGigStatus);
							addGigStatustoInstruments(newGigStatus);
							savedGigStatuses.add(newGigStatus);
						} 
					}
				}			
				return savedGigStatuses;			
			} catch (Exception e) {
					logger.error("Exception occurred while trying to add an instrument to a Gig.", e);
					throw new Exception(e.getMessage());
			}
						
		}
	
	
	
	
	

	//  CREATE:  Create relationship between GigStatus table and instruments table
	private void addGigStatustoInstruments(GigStatus gigStatus) {		
		Instrument instrument = instrumentRepo.findOne(gigStatus.getInstrumentId());
		Instrument addInstrument = instrument;

				
		List<GigStatus> gigStatuses = new ArrayList<GigStatus>();
		gigStatuses = addInstrument.getGigStatuses();

		logger.info("Instrument name: " + addInstrument.getName() + " id: " + addInstrument.getInstrumentId());
		if (gigStatuses != null) {
			addInstrument.getGigStatuses().add(gigStatus);
			instrumentRepo.save(addInstrument);
		} else {
			//create a list, and add the instrument
			GigStatus newGigStatus = new GigStatus();
			
			List<GigStatus> newGigStatuses = new ArrayList<GigStatus>();
			newGigStatus = gigStatus;
			
			addInstrument.setGigStatuses(newGigStatuses);	
			if ((addInstrument.getGigStatuses() != null) && (addInstrument.getGigStatuses().add(newGigStatus))) {
				instrumentRepo.save(addInstrument);
			}
		}
		addInstrument = instrumentRepo.findOne(instrument.getInstrumentId());
	}
	
	
	// CREATE:  Create instrument records for a particular Gig by gigId
	public Iterable<GigStatus> createGigStatus(GigStatus gigStatus, Long gigId) throws Exception {
		try {
			List<GigStatus> savedGigStatuses = new ArrayList<GigStatus>();			
			Gig oldGig = repo.findOne(gigId);
			
			GigStatus createGigStatus = new GigStatus();
			
			createGigStatus.setInstruments(instrumentService.createInstruments(gigStatus.getInstruments()));
			
			List<Instrument> gigRequiredInstruments = new ArrayList<Instrument>();		
			gigRequiredInstruments = createGigStatus.getInstruments();
			
			if (gigRequiredInstruments != null) {
				Double splitSalary = (oldGig.getSalary()/gigRequiredInstruments.size());

				for (Instrument inst : gigRequiredInstruments) {
					GigStatus newGigStatus = new GigStatus();
					newGigStatus.setGigId(oldGig.getGigId());
					newGigStatus.setStatus(StatusType.OPEN);
					newGigStatus.setSalary(splitSalary);
					newGigStatus.setInstruments(instrumentService.createInstruments(gigRequiredInstruments));
					
					for (Instrument oneInst : newGigStatus.getInstruments()) {
						if (oneInst.getName().equals(inst.getName())) {
							newGigStatus.setInstrumentId(oneInst.getInstrumentId());
						}
					}				
					// Create a new GigStatus
					newGigStatus = statusRepo.save(newGigStatus);
					addGigStatustoInstruments(newGigStatus);
					savedGigStatuses.add(newGigStatus);
				} 
			}
			return savedGigStatuses;			
		} catch (Exception e) {
				logger.error("Exception occurred while trying to add an instrument to a Gig.",e);
				throw new Exception(e.getMessage());
		}
					
	}
	
	// CONFIRM a musician in a particular Gig By the Gig Planner	
	public GigStatus updateGigStatusConfirm(GigStatus gigStatus, Long gigId, Long musicianId, Long plannerId) throws Exception {
		
		try {
			//Retrieve all GigStatuses associated with the passed in GigId
			Gig requestedGig = repo.findOne(gigId);
			
			Iterable<GigStatus> matchedGigStatuses = statusRepo.findByGigId(gigId);
			
//			int counter = 1;
//			for (GigStatus anotherGigStatus : matchedGigStatuses) {
//				logger.info("Counter: " + counter++ + "  GigStatus #: " + anotherGigStatus.getId());
//			}
			
			//Retrieve THE Instrument RECORD of the requested Instrument!  
			Instrument requestedInstrument = new Instrument();
			requestedInstrument = findMatchingInstrument(gigStatus.getInstruments());
			
			
			// Iterate through the gigStatuses, and CONFIRM the ONE requested by this musician
			for (GigStatus matchGigStatus : matchedGigStatuses) {

				// ONLY the registered planner can CONFIRM a musician for a Gig!
				// if plannerId matches Gig.plannerId(), &&  musicianId matches GigStatus.musicianId && gigStatus is REQUESTED
				
//				logger.info("PlannerId of requestedGig: " + requestedGig.getPlannerId());
//				logger.info("MusicianId of matchGigStatus: " + matchGigStatus.getMusicianId());
//				logger.info("Status of matchGigStatus: " + matchGigStatus.getStatus());
				
				if ((requestedGig.getPlannerId().equals(plannerId)) && (matchGigStatus.getStatus().equals(StatusType.REQUESTED))) {				
					if ((matchGigStatus.getMusicianId() != null) && (matchGigStatus.getMusicianId().equals(musicianId))) {
						
						// CONFIRM that the PLANNER wants to match this Instrument -- in case the musician requested more than one!
						if (matchGigStatus.getInstrumentId().equals(requestedInstrument.getInstrumentId())) {
							// Change status to CONFIRMED.
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
		try {		
			Iterable<GigStatus> matchedGigStatuses = new ArrayList<GigStatus>(); 
			
		//Retrieve all GigStatuses associated with the passed in GigId
			matchedGigStatuses = statusRepo.findByGigId(gigId);

		//Retrieve THE Instrument RECORD of the requested Instrument!  
			Instrument requestedInstrument = new Instrument();
			requestedInstrument = findMatchingInstrument(gigStatus.getInstruments());
			
		//  Get the requested Instrument from the repository			
		// 		FIND IF REQUESTED INSTRUMENT matches an OPEN GIGSTATUS record for this Gig
			User theMatchUser  = new User();
			for (GigStatus matchGigStatus : matchedGigStatuses) {
				if (matchGigStatus.getStatus().equals(StatusType.OPEN)) {
					if ((matchGigStatus.getInstrumentId().equals(requestedInstrument.getInstrumentId()))
							&& (matchInstrument == false)){
						matchInstrument = true;
						relatedGigStatus = matchGigStatus;
					}
				}
			}
			
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
							relatedGigStatus.setStatus(StatusType.REQUESTED);
							relatedGigStatus.setMusicianId(theMatchUser.getId());
							statusRepo.save(relatedGigStatus);
							return relatedGigStatus;
						} else {
							logger.error("Invalid Request for an OPEN GIG.");
						}
						
					} else {
						logger.error("Requested Gig can not be assigned.");			
					}
				} else {
					logger.error("Instrument requested does not match.");
				}
			} else {
				logger.error("UserType is not valid.");
			
			}  // End of for every gigStatus in this gig (matches gigId)

		} catch  (Exception e) {
			logger.error("Unable to update gigStatus: " + relatedGigStatus.getId() + " with userId: " + userId, e);
			throw new Exception(e.getMessage());
		}	
		logger.error("Unable to update gigStatus: " + relatedGigStatus.getId() + " with userId: " + userId);
		throw new Exception("Unable to update gigStatus: " + relatedGigStatus.getId() + " with userId: " + userId);
	}
	
	//  Retrieve a matching instrument record.
	public Instrument findMatchingInstrument(List<Instrument> requestedInstruments) throws Exception {
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
		throw new Exception("No instrument found!");
	}
	
	public Gig updateGigStatusOnly(Long gigId, StatusType newStatus) throws Exception {
		try {
			Gig oldGig = repo.findOne(gigId);
			List<GigStatus> updatedGigStatuses = new ArrayList<GigStatus>();
			logger.info("Updating gig: " + gigId);
			Gig savedGig = new Gig();
			oldGig.setEventStatus(newStatus);	
			savedGig = repo.save(oldGig);
			
			// Find all GigStatus records for this Gig, and Set them all to the new status			
			List<GigStatus> relatedGigStatuses = (List<GigStatus>) statusRepo.findByGigId(gigId);			
			for (GigStatus gigStatus : relatedGigStatuses) {
				gigStatus.setStatus(newStatus);
				statusRepo.save(gigStatus);
				updatedGigStatuses.add(gigStatus);
			}	
			savedGig.setGigStatuses(updatedGigStatuses);
			return savedGig;
		} catch (Exception e) {
				logger.error("Exception occurred while updating the EventStatus of a Gig.");
				throw new Exception("Unable to update gig id:" + gigId);
		} 
	}
	
	
	public Gig updateGig(Gig newGig, Long gigId) throws Exception {
		try {
			Gig oldGig = repo.findOne(gigId);
			List<GigStatus> updatedGigStatuses = new ArrayList<GigStatus>();
			logger.info("Updating gig: " + gigId);
			Gig savedGig = new Gig();
			oldGig.setGigDate(newGig.getGigDate());
			oldGig.setGigStartTime(newGig.getGigStartTime());
			oldGig.setGigDuration(newGig.getGigDuration());
			oldGig.setPhone(newGig.getPhone());
			oldGig.setEvent(newGig.getEvent());
			oldGig.setGenre(newGig.getGenre());
			oldGig.setPlannerId(newGig.getPlannerId());
			oldGig.setSalary(newGig.getSalary());
			oldGig.setDescription(newGig.getDescription());
			oldGig.setEventStatus(newGig.getEventStatus());
			savedGig = repo.save(oldGig);
			
			// Find all GigStatus records for this Gig, and Set them to the new status			
			List<GigStatus> relatedGigStatuses = (List<GigStatus>) statusRepo.findByGigId(gigId);			
			for (GigStatus gigStatus : relatedGigStatuses) {
				gigStatus.setStatus(savedGig.getEventStatus());
				statusRepo.save(gigStatus);
				// Should I add the gigStatus records to the savedGig here???
				updatedGigStatuses.add(gigStatus);
			}
			savedGig.setGigStatuses(updatedGigStatuses);
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
		Gig requestedGig = repo.findOne(gigId);
		User planner = userRepo.findOne(requestedGig.getPlannerId());
		if (!(planner.getUserType().equals(UserType.ADMIN))) {
			logger.error("Only an ADMIN can delete a Gig!");
			throw new Exception ("Only an ADMIN can delete a Gig!");
		}			
		try {
			Iterable<GigStatus> gigStatusByGigId = getGigStatuses(gigId);			
			for (GigStatus status : gigStatusByGigId){
				statusRepo.delete(status);
			}
			repo.delete(gigId);
			logger.info("Deleted gig: " + gigId);	
		} catch (Exception e) {
			logger.error("Exception occurred while trying to delete gig:" + gigId,e);
			throw new Exception(e.getMessage());
		}
	}
}

