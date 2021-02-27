package com.lisasmith.findAGig.repository;

import org.springframework.data.repository.CrudRepository;

import com.lisasmith.findAGig.entity.GigStatus;

public interface GigStatusRepository extends CrudRepository<GigStatus, Long> {
	
	public Iterable<GigStatus> findByGigId(Long gigId);

	public Iterable<GigStatus> findByMusicianId(Long musicianId);

	public Iterable<GigStatus> findByInstrumentId(Long instrumentId);

}
