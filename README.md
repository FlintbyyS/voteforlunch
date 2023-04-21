# Vote for lunch - votes counter

## About

Here you can find voting app for the restaurant with the best menu for today


## Build and Deploy the Project

> mvn clean install

This is a Spring Boot project, so you can deploy it by simply using the main class: `VoteforlunchApplication.java`

Once deployed, you can access the app at: http://localhost:8080

Mind that this application is backend only. 

Link to Swagger: http://localhost:8080/swagger-ui.html


## Technical requirement

Design and implement a REST API using Hibernate/Spring/SpringMVC (Spring-Boot preferred!) without frontend.

The task is: Build a voting system for deciding where to have lunch.

* 2 types of users: admin and regular users
* Admin can input a restaurant, and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote for a restaurant they want to have lunch at today
* Only one vote counted per user
* If user votes again the same day:
* If it is before 11:00 we assume that he changed his mind.
* If it is after 11:00 then it is too late, vote can't be changed
* Each restaurant provides a new menu each day.


## How it works

- The user can view menu lists for a specific date.
- The user can vote for a particular restaurant.
- The user's vote will be counted for the current date.
- It doesn't matter if the restaurant has a menu for that day or not.
- The user can vote at any time.
- The user can change or remove (withdraw) his vote from 00:00 until the time specified in the program settings (11:00).
- The user can request the distribution of votes between restaurants for any date.
- The administrator has full access to all end-points.


## Used technologies

**Spring Boot / Spring Data JPA / H2 database / Spring Security / Jackson / Caffeine cache  / Spring Validation / Lombok / Spring Tests / Swagger**

- For simplicity, I am using an in-memory H2 database that is populated with some test data using InitializeData component
- ORM framework is Spring Data JPA
- Entities are mapped to DTOs using simple mapper classes without using Mapstruct or other complex libraries
- The application uses Caffeine for caching. According to the application logic, MenuService and VoteService#getVotesDistributionOnDate uses the cache. Cached: menu GET operations, votes distribution


## Database structure

![img.png](img.png)


## Security

Users for tests:
- Administrator login _admin@ya.ru_ password _admin_
- User login _user@ya.ru_ password _user_

Self-registration of users is not implemented

For clarity, the credentials that are generated when the program starts are not encrypted. The program implements the following logic for working with users:
- When creating a new user, his password is encrypted at the stage of converting UserDto to User
- When converting User to UserDto for return to the frontend, the password is hidden
- When updating the data of an already existing user in the database, the password does not change
- To change the password, a separate functionality must be implemented but that is not implemented in this version of the program
- To change roles, a separate functionality must be implemented but that is not implemented in this version of the program
