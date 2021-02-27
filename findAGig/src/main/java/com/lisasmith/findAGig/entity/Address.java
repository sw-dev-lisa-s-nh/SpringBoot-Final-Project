package com.lisasmith.findAGig.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Address {

	private Long addressId;
	private String street;
	private String city;
	private String state;
	private String zip;
	
	@JsonIgnore
	private Set<User> users;
	
	@JsonIgnore
	private Set<Gig> gigs;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getAddressId() {
		return addressId;
	}
	
	public void setAddressId(Long id) {
		this.addressId = id;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@OneToMany(mappedBy="address")
	public Set<User> getUsers() {
		return users;
	}
	
	public void setUsers(Set<User> users) {
		this.users = users;
	}


	@OneToMany(mappedBy = "address")
	public Set<Gig> getGigs() {
		return gigs;
	}
	
	public void setGigs(Set<Gig> gigs) {
		this.gigs = gigs;
	}

}
