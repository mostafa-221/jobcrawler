# Wireframe of the vacancylist


![screenshot-wireframe-vacancylist](https://user-images.githubusercontent.com/55943332/88810727-08cb0f80-d1b6-11ea-96d8-1e8f660e6699.png)


The vacancies screen shows the vacancies retrieved by the backend. (NB. search button and search field is missing from this wireframe screen)

The skills are retrieved from the skill table and shown in a list on this screen. One or more skills can be chosen from this list. The result is a list of vacancies that are related to ALL of the chosen skills.

The search field may contain a word or a sentence that is part of the vacancy text on the web page.

The vacancy title in the list of vacancies will show a popup that shows the raw (ascii) text of the vacancy (cleared from html tags of course). The link at the end of any vacancy will show the original vacancy in a separate browser tab.

The location field filters by location.

Distance field will filter the vacancy according to vacancies that contain a location (ie.e. a city name) at a distance smaller than the given number of kilometers to the given location. This distance filter will of course only work in case the location is filled in by the user
