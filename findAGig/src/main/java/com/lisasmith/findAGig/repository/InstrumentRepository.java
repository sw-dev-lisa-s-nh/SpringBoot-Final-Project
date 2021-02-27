package com.lisasmith.findAGig.repository;

import org.springframework.data.repository.CrudRepository;

import com.lisasmith.findAGig.entity.Instrument;

public interface InstrumentRepository extends CrudRepository<Instrument,Long>{

	public Instrument findByName(String name);
}
