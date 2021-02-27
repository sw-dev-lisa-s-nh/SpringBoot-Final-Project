package com.lisasmith.findAGig.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lisasmith.findAGig.entity.Gig;
import com.lisasmith.findAGig.entity.GigStatus;
import com.lisasmith.findAGig.service.GigService;
import com.lisasmith.findAGig.util.GenreType;

@RestController
@RequestMapping("/gigs")
public class GigController {
	
	@Autowired
	private GigService service;
	
	
//	// READ:  Retrieve all gigs
//	@RequestMapping(method=RequestMethod.GET)
//	public ResponseEntity<Object> getGigs() {
//		return new ResponseEntity<Object>(service.getGigs(), HttpStatus.OK);
//	}
	
	// READ:  Retrieve all gigs & gig statuses
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Object> getGigsAndGigStatuses() {
		return new ResponseEntity<Object>(service.getGigsAndGigStatuses(), HttpStatus.OK);
	}

	// CREATE: Create a Gig
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> createGig(@RequestBody Gig gig) throws Exception {
		return new ResponseEntity<Object>(service.createGig(gig), HttpStatus.CREATED);
	}
	
	// UPDATE: Update a Gig by id 
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Object> updateGig(@RequestBody Gig gig, @PathVariable Long id) throws Exception {
		try {
			return new ResponseEntity<Object>(service.updateGig(gig, id), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	// CREATE:  Add (ADD) instruments into GigStatus for an existing Gig
	@RequestMapping(value="/{id}",method=RequestMethod.POST)
	public ResponseEntity<Object> createGigStatus(@RequestBody GigStatus gigStatus, @PathVariable Long id) throws Exception {
			return new ResponseEntity<Object>(service.createGigStatus(gigStatus,id), HttpStatus.CREATED);
	}
	
	// READ:  Read INSTRUMENTS from GigStatus for an existing Gig by GigId
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigStatuses(@PathVariable Long id) throws Exception {
			return new ResponseEntity<Object>(service.getGigStatuses(id), HttpStatus.OK);
	}

	// READ:  Read all USERS from GigStatus that match a particular existing Gig by GigId
	//        Print out all User Information for the matching GigStatuses.
	@RequestMapping(value="/{id}/users",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigStatusesWithMusicianInfo(@PathVariable Long id) throws Exception {
			return new ResponseEntity<Object>(service.getGigStatusesWithMusicianInfo(id), HttpStatus.OK);
	}	
	
	// UPDATE: Update a Gig/GigStatus by id with a new Status ==> using new UserId
	@RequestMapping(value="/{id}/users/{userId}/request", method=RequestMethod.PUT)
	public ResponseEntity<Object> updateGigStatus(@RequestBody GigStatus gigStatus, @PathVariable Long id, @PathVariable Long userId) throws Exception {
		try {
			return new ResponseEntity<Object>(service.updateGigStatus(gigStatus, id, userId), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	// UPDATE: Update a Gig/GigStatus by id with a new Status by ID
		@RequestMapping(value="/{id}/users/{musicianId}/confirm/{plannerId}", method=RequestMethod.PUT)
		public ResponseEntity<Object> updateGigStatusConfirm(@RequestBody GigStatus gigStatus, @PathVariable Long id, @PathVariable Long musicianId, @PathVariable Long plannerId) throws Exception {
			try {
				return new ResponseEntity<Object>(service.updateGigStatusConfirm(gigStatus, id, musicianId, plannerId), HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
			}
		}
	
	// READ:  Retrieve gigs with OPEN positions
	@RequestMapping(value="/open",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigsandGigStatusByOPEN() throws Exception {
		try {
			return new ResponseEntity<Object>(service.getGigsAndGigStatusesByOpen(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	// READ:  Retrieve gigs with OPEN positions matching /state/{name} 
	@RequestMapping(value="/open/state/{name}",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigsandGigStatusByStateOpen(
			@PathVariable String name, 
			@RequestParam(value = "isOpen", required=false, defaultValue = "true") boolean isOpen) throws Exception {
		
		try {
			return new ResponseEntity<Object>(service.getGigsAndGigStatusesByState(name, isOpen), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	// READ:  Retrieve gigs with OPEN positions matching /instrument/{name} 
	@RequestMapping(value="/open/instrument/{name}",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigsandGigStatusByInstrumentOpen(
			@PathVariable String name, 
			@RequestParam(value = "isOpen", required=false, defaultValue = "true") boolean isOpen) throws Exception {
		
		try {
			return new ResponseEntity<Object>(service.getGigsAndGigStatusesByInstrumentName(name,isOpen), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}	
	
	// READ:  Retrieve gigs with OPEN positions matching /genre/{type} 
	@RequestMapping(value="/open/genre/{type}",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigsandGigStatusByGenreOPEN(
			@PathVariable GenreType type, 
			@RequestParam(value = "isOpen", required=false, defaultValue = "true") boolean isOpen) throws Exception {		
		try {
			return new ResponseEntity<Object>(service.getGigsAndGigStatusesByGenreType(type,isOpen), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}		

	
	// READ:  Retrieve gigs with matching /state/{name} 
		@RequestMapping(value="/state/{name}",method=RequestMethod.GET)
		public ResponseEntity<Object> getGigsandGigStatusByState(
				@PathVariable String name, 
				@RequestParam(value = "isOpen", required=false, defaultValue = "false") boolean isOpen) throws Exception {
	
			try {
				return new ResponseEntity<Object>(service.getGigsAndGigStatusesByState(name, isOpen), HttpStatus.OK);					
			} catch (Exception e) {
				return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
			}
		}
		
	// READ:  Retrieve gigs with matching /instrument/{name} 
		@RequestMapping(value="/instrument/{name}",method=RequestMethod.GET)
		public ResponseEntity<Object> getGigsandGigStatusByInstrument(
				@PathVariable String name, 
				@RequestParam(value = "isOpen", required=false, defaultValue = "false") boolean isOpen) throws Exception {		
			try {
				return new ResponseEntity<Object>(service.getGigsAndGigStatusesByInstrumentName(name,isOpen), HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
			}
		}	
		
	// READ:  Retrieve gigs with matching /genre/{type} 
		@RequestMapping(value="/genre/{type}",method=RequestMethod.GET)
		public ResponseEntity<Object> getGigsandGigStatusByGenre(
				@PathVariable GenreType type, 
				@RequestParam(value = "isOpen", required=false, defaultValue = "false") boolean isOpen) throws Exception {		
			try {
				return new ResponseEntity<Object>(service.getGigsAndGigStatusesByGenreType(type,isOpen), HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
			}
		}		

		
	// READ:  Retrieve gigs by user (userId) assigned to it. 
	@RequestMapping(value="/users/{userId}",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigStatusesByUserId(@PathVariable Long userId) throws Exception {
			return new ResponseEntity<Object>(service.getGigStatusesByUserId(userId), HttpStatus.OK);
	}
	
	// DELETE:  Delete an existing Gig by GigId
	//			BY DESIGN:  THIS ONLY WORKS if there are no interdependencies
	//						We DO NOT want gigs to be deleted, even if they are cancelled. 
	//						Instead, we want to set the STATUS to CANCELLED or CLOSED (if the event has happened!)
	//
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteGig(@PathVariable Long id) throws Exception {
		try {
			service.deleteGig(id);
			return new ResponseEntity<Object>("Successfully deleted gig with id: " + id, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}

