package com.lisasmith.findAGig.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.lisasmith.findAGig.util.StatusType;

@Entity
public class GigStatus {

	private Long id;
	private Long gigId;
	private Long musicianId;
	private Long instrumentId;
	private double salary;
	private StatusType status;
	private List<Instrument> instruments;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getGigId() {
		return gigId;
	}

	public void setGigId(Long gigId) {
		this.gigId = gigId;
	}

	public Long getMusicianId() {
		return musicianId;
	}

	public void setMusicianId(Long musicianId) {
		this.musicianId = musicianId;
	}

	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}	
	
	@ManyToMany (mappedBy="gigStatuses")
	public List<Instrument> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<Instrument> instruments) {
		this.instruments = instruments;
	}

}
