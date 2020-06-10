# Endpoints
Documentation of endpoints provided by this API

## /scraper
An endpoint that controls the behaviour of the scraping of IT websites. 
### PUT
To begin the scraping of websites

## /vacancies (same holds for /skills)
A CRUD endpoint for the vacancies that are scraped. 
### GET
**Returns** all the vacancies that were scraped. 
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

## /search
Endpoint for interaction from the frontend. 

### POST
**Requires** search criteria in the body of the request, for example:
```
{
    "location": "city", 
    "distance": "in km", 
    "keywords": [
        "keyword1",
        "keyword2"
    ]
}
```
If successful **returns** found results in the following format:

```
{   
    "request": {
       "location": "city", 
       "distance": "in km", 
       "keywords": [
           "keyword1",
           "keyword2"
       ]
    },
    "results": [
        {
            "jobName": "name1",
            "location": "city",
            "distance": "in km"
            "broker": "broker1",
            "foundKeys": "keys",
            "link": "url to vacancy"
        },
        {
            "jobName": "name2",
            "location": "city",
            "distance": "in km"
            "broker": "broker1",
            "foundKeys": "keys",
            "link": "url to vacancy
        },
        {
            "jobName": "name3",
            "location": "city",
            "distance": "in km"
            "broker": "broker2",
            "foundKeys": "keys",
            "link": "url to vacancy
        }
    ]
}
```
**Returns**:
- 200 OK If successful with the results
- 400 Bad Request if the given body is invalid


