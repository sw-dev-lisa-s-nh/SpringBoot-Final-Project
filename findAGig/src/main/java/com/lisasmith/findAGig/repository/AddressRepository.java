package com.lisasmith.findAGig.repository;

import org.springframework.data.repository.CrudRepository;

import com.lisasmith.findAGig.entity.Address;

public interface AddressRepository extends CrudRepository<Address,Long> {

	public Iterable<Address> findByState(String state);
	
	public Iterable<Address> findByCity(String city);
}
