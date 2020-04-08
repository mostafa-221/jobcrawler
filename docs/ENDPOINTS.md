# Endpoints
Explanation of endpoints provided by this API

## POST: "/searchrequest"
Invokes a crawl on all known sites for the given data.

### Needed request body
All important search data must be provided in the body of the request:

```
{
"location": "city", 
"distance": "in km", 
"keywords": "seperated by commas"
}
```

### Provided response body
The API response will return the found results in the following format:

```
{
results: [
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
    },
]
```