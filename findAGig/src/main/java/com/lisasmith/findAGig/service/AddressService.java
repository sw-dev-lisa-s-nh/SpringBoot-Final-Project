package com.lisasmith.findAGig.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Address;
import com.lisasmith.findAGig.repository.AddressRepository;


@Service
public class AddressService {
	
	private static final Logger logger = LogManager.getLogger(AddressService.class);
	public static Long currentAddressId;

	@Autowired
	private AddressRepository repo;
	
	public Address createAddress(Address newAddress) throws Exception {
		try {
			Address address = new Address();
			// create an address
			if (!doesAddressExist(newAddress)) {
				address.setAddressId(newAddress.getAddressId());
				address.setCity(newAddress.getCity());
				address.setState(newAddress.getState());
				address.setStreet(newAddress.getStreet());
				address.setZip(newAddress.getZip());
				logger.info("Created address.");
				return repo.save(address);
			} else {
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

		 Iterable<Address> allAddresses = repo.findAll();
		 for (Address address : allAddresses ) {
			 if (address.getCity().equals(newAddress.getCity())) {
				 if (address.getStreet().equals(newAddress.getStreet())) {
					 if (address.getState().equals(newAddress.getState())) {
						 if (address.getZip().equals(newAddress.getZip())) {
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
