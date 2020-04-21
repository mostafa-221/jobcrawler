# Heroku server deployment 

This document will walk you through a number of steps to get started with the heroku server. Make sure you accept the invitation and make an account first.
- Downloading the command-line tool
- Making a link to the heroku server
- Deploying from a repo branch
- Deploying local changes
- Reading errors and logs
- Useful commands


## Download the heroku command line 
If you have [Homebrew](https://brew.sh/), you can install the command line tool by executing 

    brew install heroku/brew/heroku
    
Else you can dowload the package from the [website](https://devcenter.heroku.com/articles/getting-started-with-java#set-up).

## Link the command-line tool to your account
execute

    heroku login 
    
and follow the procedures.


## Make a link between the heroku server and the current project directory
In the directory of a project run 

    git remote add heroku <heroku-git-link>

so inside the backend project folder execute

    git remote add heroku https://git.heroku.com/api-jobcrawler.git

and inside the frontend project folder execute 

    git remote add heroku https://git.heroku.com/jobcrawler-site.git

## Deploy from a repo branch
I think it is easiest to go the heroku dashboard/managing pages of projects to deploy from the git repository. 

Frontend: https://dashboard.heroku.com/apps/jobcrawler-site \
backend: https://dashboard.heroku.com/apps/api-jobcrawler

click on the tap deploy, scroll down to manual deploy, choose a branch and deploy it. 


## Deploy local changes (won't be pushed to the repo)
first add files and commit the changes to your local git with 
    
    git commit -am “message”
then update the head of the heroku server to the local change you have (not sure if this is exactly what it does, but it works)

    git config remote.heroku.push +HEAD:refs/heads/master
then push the changes to the master branch of heroku

    git push heroku +HEAD:master

**Note**: if you are trying many things until it works, if you push directly to the repo, this will push all the commits you did. Ideally we would want to see a single commit. You can either squash your commits to combine all commits into one commit, or you can clone the project in a new directory run git status on the one that works, apply the changes to the new directory and commit and push from the new one. 

## Reading errors and logs of the server
In the directory that has a link to a remote heroku server run

    heroku logs –tail
to see the console of the app along with other login and http request information.

## Useful commands 
To start the container, if it is not running, first you have to specify that you want a single container to run with
    
    heroku ps:scale web=1
    
then you can start the container

    heroku start

For restarting a running container 
	    
    heroku restart
To open the page of the container
        
    heroku open

To execute single lines
    
    heroku run <command>
    
To open a bash of the container
	 
	 heroku run bash
	 
## Sources
[Getting started on Heroku with Java](https://devcenter.heroku.com/articles/getting-started-with-java) \
[How to link a folder with an existing Heroku app](https://stackoverflow.com/questions/5129598/how-to-link-a-folder-with-an-existing-heroku-app)

	
