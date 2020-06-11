# Endpoints
Documentation of endpoints provided by this API

## /scraper
An endpoint that controls the behaviour of the scraping of IT websites. 
### PUT
To begin the scraping of websites

## /vacancies (same holds for /skills)
A CRUD endpoint for the vacancies that are scraped. 
### GET
**Returns** all the vacancies that were scraped and code 200 OK. 

Can have additional parameters for filtering and pagination such as 
```
GET /vacancies?location=city&sort_by=distance&page_size=20&page=2
```  
to sort the result by distance from the specified location and to give 20 results for page 2. 

### POST 
To manually add a job to the database. **Requires** a JSON body of the vacancy, for example:
```
{
    "vacancyURL": "example.com",
    "title": "job1",
    "broker": "broker",
    "vacancyNumber": "2387",
    "hours": "30",
    "location": "City",
    "salary": "2500",
    "postingDate": "14 April 2020",
    "about": "this is a description of the example job",
    "skills":[
        "skill1",
        "skill2"
    ]
}
```
**Returns**:
- 201 Created and a link to the new vacancy if success
- 400 Bad Request if the given body is invalid

### /{id}
#### GET
**Returns**:
 - 200 OK and JSON body of the vacancy if the id is found and the vacancy is successfully retrieved 
 - 404 Not Found if the id was not found  

#### PUT 
To update a vacancy. **Requires** a JSON body with the fields to be updated. **Returns**:
 - 200 OK if the id was found, and the vacancy is updated 
 - 404 Not Found if the id was not found
 - 409 Conflict if there is a conflict 

#### DELETE
To delete a vacancy. **Returns**:
- 200 OK if the delete was successful
- 404 Not Found if a product with the specified ID is not found


