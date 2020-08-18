
# Jobcrawler smoketest
Smoketest for both front end and backend for the JobCrawler application


### Test Backend run 1. Run from IntelliJ, separate Postgres docker file

Test whether the application runs and is visible from the browser. Postgres is present in a separate docker container that must be run.

### Creates the container, runs it and creates a database 'jobcrawler' in it:
#### 1. Start the shell in the source folder of the backend project
docker run --name jobcrawler-postgres -e POSTGRES_PASSWORD=admin -d -p 5432:5432 postgres

docker exec -it jobcrawler-postgres bash

psql -U postgres

create database jobcrawler;

Exit from this database and the container

Start IntelliJ in the backend project, edit application.properties and change spring.datasource-url to 'jdbc:postgresql://localhost:5432/jobcrawler'

Run the application from IntelliJ

What do I see: the application boots without problems and doesn't start scraping

#### 2. Start scraping

Edit the scraperService.java and uncomment line @ PostConstruct before the scrape() method. Run the application again.

What do I see: application starts and starts scraping

What do I see: when I again enter the docker container and connect to the jobcrawler database I can type
select title from vacancies

I should now see a number of vacancies

### Test Backend run 2 Create and run docker container including Postgres  

#### Test1: Check get vacancies endpoint from browser

#### Test2: Check get skills endpoint from browser

#### Test3: Check add skill with Postman  




### Deploy and run on server




