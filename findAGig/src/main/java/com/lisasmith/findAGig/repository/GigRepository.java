package com.lisasmith.findAGig.repository;

import org.springframework.data.repository.CrudRepository;

import com.lisasmith.findAGig.entity.Gig;
import com.lisasmith.findAGig.util.EventType;
import com.lisasmith.findAGig.util.GenreType;

public interface GigRepository extends CrudRepository<Gig, Long> {

	public Iterable<Gig> findByEvent(EventType event);
	
	public Iterable<Gig> findByGenre(GenreType genre);
}
