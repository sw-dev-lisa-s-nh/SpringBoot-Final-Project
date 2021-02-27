package com.lisasmith.findAGig.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Address;
import com.lisasmith.findAGig.repository.AddressRepository;


@Service
public class AddressService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);
	public static Long currentAddressId;

	@Autowired
	private AddressRepository repo;
	
	public Address createAddress(Address newAddress) throws Exception {
		try {
			Address address = new Address();
			// create an address
			logger.info("In createAddress");
			if (!doesAddressExist(newAddress)) {
				logger.info("Address does not exist");
				address.setAddressId(newAddress.getAddressId());
				logger.info("AddressId set");
				address.setCity(newAddress.getCity());
				logger.info("AddressCity set");
				address.setState(newAddress.getState());
				logger.info("AddressState set");
				address.setStreet(newAddress.getStreet());
				logger.info("AddressStreet set");
				address.setZip(newAddress.getZip());
				logger.info("AddressZip set");
				return repo.save(address);
			} else {
				logger.info("Address exists.  Id: " + currentAddressId);
				return repo.findOne(currentAddressId);
			}
		} catch (Exception e) {
			logger.error("Exception occurred while trying to create an address.");
			throw new Exception("Unable to create a new address.");
		}
	}
	
	public boolean doesAddressExist(Address newAddress) throws Exception {
		 boolean addressExists = false;
			
		 // Retrieve all address from the database
		 //	1.  check to see if this address is in the database.  
		 // 2.  if found, 
		 //			a.  Set addressExists to true
		 //			b.  Set the static variable to the address_id
		logger.info("In doesAddressExist()");

		 Iterable<Address> allAddresses = repo.findAll();
		 for (Address address : allAddresses ) {
			 if (address.getCity().equals(newAddress.getCity())) {
				 if (address.getStreet().equals(newAddress.getStreet())) {
					 if (address.getState().equals(newAddress.getState())) {
						 if (address.getZip().equals(newAddress.getZip())) {
							 logger.info("Address matches");
							 addressExists = true;
							 currentAddressId = address.getAddressId();
							 return addressExists;
						 }
					 }
				 }
			 }
		 }
		 return addressExists;		
	}
	
}
