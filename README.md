# SpringBoot-Final-Project

NOTE:  Current Promineo Tech Back-End students -- This final project was written before the jeep-sales project was created.  This follows a different design for a REST API.  Notice the following:

      (1) Lack of interface files for all layers
      
      (2) No Swagger Interface
      
      (3) This project was tested by using Postman (A downloadable app). -- ask me if you are interested in learning this!
      
      (4) No lombok dependancy.
      
      (5) No JDBC calls -- using JPA instead (an ORM)  
                   See this link for a great explanation of JDBC vs. JPA https://www.baeldung.com/jpa-vs-jdbc
      
      
Final Project for our Promineo Tech BESD Coding Bootcamp -- WebAPI 

BESD Coding Bootcamp Spring Boot Week 6 -- Final Project 

**Author**:  sw-dev-lisa-s-nh

**Updated**:  3/6/2021

**Course**:   Promineo Tech BESD Coding Bootcamp 2020-2021

This is the final week of our final project.   

      (1) Designed the Database
      
      (2) Created a Maven Project
      
      (3) Implemented the Repositories
      
      (4) Implemented the Entities
      
      (5) Implemented the Controllers
      
      (6) Implemented the Services
      
      (7) CRUD Operations Are Implemented for Instruments, Gigs & Users

      (8) GET of information on users and gigs by instrument, state, or genre.
      
      (9) Ability to update Gigs STATUS, Request, Confirm, Cancel, or Close a Gig.
      
      
Here is the updated plan for my project:

Initial Idea: My idea is to have a website that helps musicians connect with possible events (gigs) 
& additionally have event (gig) planners connect with available musicians.  

Entities:  
1.  user (roles: musician & planner) (MANYTOONE with address)
2.  address (aspect of location for users and gigs) (ONETOMANY with Gig) (ONETOMANY with User) 
3.  instrument
4.  gig (MANYTOONE with address)
5.  gig_status (connects musicians with gigs)
6.  join table on musician & instrument (MANYTOMANY)
7.  join table on gig_status & instruments requested (ONETOMANY)
8.  (FUTURE PLAN):  credentials
9.  (FUTURE PLAN):  Add another role to user:  systemAdmin

NOTES:  A gig might request multiple musicians to play the same instruments (gig_status)   
(Requiring multiple version of the same instrument).
