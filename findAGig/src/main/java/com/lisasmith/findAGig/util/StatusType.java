package com.lisasmith.findAGig.util;

public enum StatusType {

	PLANNED, 		// Planned: NOT yet active!
	OPEN,			// Active:  Available to be REQUESTED
	REQUESTED,		// Active:  REQUESTED, Available to be CONFIRMED
	CONFIRMED,		// Active:  CONFIRMED, Available to be CLOSED or CANCELLED
	CLOSED,			// Complete:  CLOSED, Not available.
	CANCELLED;		// Cancelled:  CANCELLED -- event will not happen
	
}
