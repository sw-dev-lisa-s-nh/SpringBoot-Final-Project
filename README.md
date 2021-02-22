# SpringBoot-Final-Project
Final Project for our Promineo Tech BESD Coding Bootcamp -- WebAPI 

# SpringBoot-week4
BESD Coding Bootcamp Spring Boot Week 5 & 6 -- Final Project (Week 3-4)

Author:  sw-dev-lisa-s-nh

Updated:  2/20/2021

Course:   Promineo Tech BESD Coding Bootcamp 2020-2021

This is the second week of our final project.   Well into the implementation at this point.
I have completed the following tasks:

      (1) Designed the Database
      
      (2) Created a Maven Project
      
      (3) Implemented the Repositories
      
      (4) Implemented the Entities
      
      (5) Implemented the Initial Controllers
      
      (6) Implemented the Initial Services
      
      (7) CRUD Operations Are Implemented for Instruments, Gigs & Users

My tasks for this week are:

      (1) Test all implemented functionality

      (2) Design the remaining functionality

      (3) Start to implement the interaction between users and gigs


Here is the updated plan for my project:

Initial Idea: My idea is to have a website that helps musicians connect with possible events (gigs) 
& additionally have event (gig) planners connect with available musicians.  

Entities:  
1.  user (roles: musician & planner)
2.  address (aspect of location for users and gigs)
3.  instrument
4.  gig
5.  gig_status (connects musicians with gigs)
6.  join table on musician & instrument
7.  join table on gig_status & instruments requested
8.  (FUTURE PLAN):  credentials
9.  (FUTURE PLAN):  Add another role to user:  systemAdmin

           
JoinTables & Relationships Between Tables:
1.  One to Many: musician to instrument (Each musician might play multiple instruments)
2.  One to Many:  gig_status to instrument (Each gig_status request might have multiple requested instruments)
3.  Many to Many :  gig to instrument -- Each instrument can be requested multiple times for a gig (record in gig_status). 


NOTES:  A gig might request multiple musicians to play the same instruments (gig_status)   A gig might request multiple musicians to play the same instruments (gig_status) multiple version of the same instrument.
