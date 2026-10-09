# Product Backlog
### Description
A prioritised list of user stories for the rwanda reviews app.
| # | User Story | Story Point | Priority (1 = lowest, 5 = highest) |
| :-: | :--------- | :---------: | :--------------------------------: |
| 1 | As a customer I want to be able to write reviews so that I can share my honest thoughts about the services of a certain business. | 2 | 5 |
| 2 | As a customer I want to be able to read reviews left by other users about a certain business so that I can decide whether I can get services from that business. | 2 | 5 |
| 3 | As a customer I want to filter businesses by location, service type or rating so that I can see businesses close to me that provide a certain service and what rating they have been given by other users. | 3 | 4 |
| 4 | As a user of the app I want to be able to read reviews in either Kinyarwanda, English or French so that I can understand what other users think of a business without the language barrier | 8 | 3 |
| 5 | As a budget-conscious person I want to see the prices that different businesses offer services at so that I can plan my budget before visiting the business | 8 | 3 |
| 6 | As a consumer I want to have the option to get services from the business without having to go to the business' premises physically so that I can conveniently get services from the business | 13 | 2 |
* * *

# Acceptance Criteria
### Description
A set of criteria that each user story must meet in order to be sure that it was implemented correctly
* * * 
### User Story #1
* **Given** a user is registered **When** select a business, **and** choose to write a review **Then** they should be given a textbox to enter their review and submit it to link it with that specific business.

### User Story #2
* **Given** a user is on the app's home page, **When** the page loads fully, **Then** they should be able to see a list of various businesses and a list of their top 3 reviews that can then be expanded to see more.

### User Story #3
* **Given** a user is on the app's home page, **When** they set filters for location and/or service type and/or rating, **Then** they should see a list of businesses and their 3 most recent reviews that correspond to the set filters.

### User Story #4
* **Given** a user is viewing a business' reviews, **When** they click on the translate button, **and** select the language to translate to, **Then** the review and the entire page should be translated to that specified language.

### User Story #5
* **Given** a user is on the home page, **When** they select a certain businees, **Then** they should be able to see a summary of the price ranges that the business has for various services.

### User Story #6
* **Given** a user is viewing the services of a business, **When** they click on the Order Online button, **Then** they should be redirected to a page that asks for their order information such as service needed, size of order, payment and delivery details.
* * *

# Definition of Done
### Description
A checklist for any and all the user stories so that the team can know when a user story is completed.
* * *
- [ ] **UI Design:** Design of each page matches the figma prototype and brand kit.
- [ ] **Performance:** Pages load in under 1.5 seconds.
- [ ] **Code Quality:** Code has been reviewed by at least one senior engineer.
- [ ] **Testing:** Code passes all automated tests.
- [ ] **Error Handling:** Code handles errors gracefully without crashing the app or producing cryptic messages for users.
