package com.lisasmith.findAGig.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Instrument {

	private Long instrumentId;
	private String name;
	
	@JsonIgnore
	private List<User> musicians;
	
	@JsonIgnore
	private List<GigStatus> gigStatuses;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getInstrumentId() {
		return instrumentId;
	}
	
	public void setInstrumentId(Long id) {
		this.instrumentId = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "musician_instrument",
			joinColumns = @JoinColumn(name = "instrumentId", referencedColumnName = "instrumentId"),
			inverseJoinColumns = @JoinColumn(name="userId", referencedColumnName = "id"))
	public List<User> getMusicians() {
		return musicians;
	}
	
	public void setMusicians(List<User> musicians) {
		this.musicians = musicians;
	}
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "gigStatus_instrument",
			joinColumns = @JoinColumn(name = "instrumentId", referencedColumnName = "instrumentId"),
			inverseJoinColumns = @JoinColumn(name = "gigStatusId", referencedColumnName = "id"))
	public List<GigStatus> getGigStatuses() {
		return gigStatuses;
	}
	
	public void setGigStatuses(List<GigStatus> gigStatuses) {
		this.gigStatuses = gigStatuses;
	}
}
