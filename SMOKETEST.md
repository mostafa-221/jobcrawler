
# Jobcrawler smoketest
Smoketest for both front end and backend for the JobCrawler application


### Test Backend run 1. Run from IntelliJ, separate Postgres docker file

Test whether the application runs and is visible from the browser. Postgres is present in a separate docker container that must be run.

### Creates the container, runs it and creates a database 'jobcrawler' in it:
#### Start the shell in the source folder of the backend project
docker run --name jobcrawler-postgres -e POSTGRES_PASSWORD=admin -d -p 5432:5432 postgres
docker exec -it jobcrawler-postgres bash
psql -U postgres
create database jobcrawler;

### Test Backend run 2 Create and run docker container including Postgres  

#### Test1: Check get vacancies endpoint from browser

#### Test2: Check get skills endpoint from browser

#### Test3: Check add skill with Postman  




### Deploy and run on server




