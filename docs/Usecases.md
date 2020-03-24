# Backend improvements
Mostafa =- 24 march
This section explains some functionality that I think would be good to have in the backend.

## Type URL
The vacancy URL is currently read and saved as a string. 
Having the url be of type java.net.URL can give us extra functionality such as checking if the url is not corrupt. This can however be checked without changing the type of the url variable. However I think it is a good learning point to see how to work with and save other types.

## Making attributes optional in reading the job
In updating a job, it would be great to not give all attributes. So it would update the job based only on the attributes that were sent.

For example u can supply an ID and a url, then it would update only the URL of that ID and leave the rest as is.  

## Get jobs by other fields
Getting the jobs by all its fields would be preferable. We can use this to filter all jobs from a certain company or from a certain data or distance. 

## Date of retrieval
This task is to have the date at which the job was read from the page. Time would also be handy. We can then use this to sort and filter.

## making skills a many to many relationship
Right now skills are a list of string that has the annotation 

    @ElementCollection
    
This annotation makes a new table in the database with an entry for each skill with the vacancy ID and the name of the skill. 
This means if more jobs have the same skill, that skill will be saved multiple times in the database. Also it makes many queries when updating the skills as per this video https://www.youtube.com/watch?v=BbDX0nv_Td0

A way to solve this is to make the skills an entity with a many-to-many relationship, as a job can have multiple skills and a skill can be required for multiple jobs. 
Feel free however to research and experiment with other solutions.

## Sorting
When getting all the jobs it would be nice to be able to show sorted data, by date, company, alphabetical, # of skills, etc. 

# Jobcrawler use cases

Adriaan =- 23 maart
We staan nog aan het begin van onze samenwerking in dit jobcrawler project. 
Op dit moment zijn we nog heel erg in de verkenningsfase.
Door veel onduidelijkheden in de mogelijkheden van de webservices is het opstellen van use cases nog prematuur.

Omdat nog niet duidelijk is wat we van de jobcrawler gaan willen is de vraag wat we uiteindelijk van de jobcrawler willen verwachten. Om de gedachten wat te ordenen heb ik dit document opgesteld als een voorlopig levend ontwerpdocument.
Ik nodig hierbij eenieder van het jobcrawler project uit om eigen bevindingen rondom de jobcrawler informatie eraan toe te voegen.

## Inventarisatie op te halen informatie van recruiters

Adriaan =- 23 maart
Het blijkt het geval te zijn dat de websites van de verschillende recruiters een zeer verschillende opbouw hebben.
Aan de hand van de uitkomsten van de verkenningsfase zou ik graag een inventarisatie willen hebben van de mogelijkheden en informatie-items die we zouden kunnen onttrekken aan de verschillende websites.
Samen kunnen bespreken wat we er uiteindelijk voor use cases uit zouden willen destilleren. Op het moment van schrijven is het maandag 23 maart, laten we als voorlopig doel stellen dat we dit voor eind maart weten en dan onze eerste use cases opbouwen.



## Gemeenschappelijke Aanvraag-informatie bestaande pakket

Adriaan =- 23 maart
De informatie die bij de verschillende recruiters vandaan gehaald is door de verschillende scapers in het bestaande pakket ondergebracht in de entiteit
Aanvraag:

    private URL aanvraagURL;
    private String title;
    private String broker;
    private String aanvraagNummer;
    private String hours;
    private String location;
    private String postingDate;
    private Recruiter recruiter;
    private String about;
    private List<String> skillSet;
    
    
## Verschillen tussen de websites

Adriaan =- 23 maart
Verschillen tussen de websites van recruiters uit het bestaande pakket:

- De website van Yacht is voorzien van een vrij vast template, zodat daar ook bepaalde vaste kenmerken kunnen worden opgehaald zoals (voorzover ik mij herinner) duur van de opdracht en dergelijke.
- Myler heeft een hoeveelheid losse tekst die weliswaar is opgemaakt maar bij iedere opdracht net weer anders,
je kunt daar dus niet zoeken op vaste kopjes

Kennelijk is de Aanvraag entiteit voorzien van de gemeenschappelijke gegevens over meerdere recruiters.

De "oude" webcrawler is kennelijk op magische wijze al in staat  van verschillende recruiters toch de al zeer informatieve kenmerken uit de Aanvraag al te verzamelen ondanks de verschillen tussen de opbouw van de webpagina's.

## Keuzen, ideeën en mogelijke uitgangspunten

Adriaan, 23 maart - idee:
Mogelijkheid zou zijn wat minder strak vast te houden aan uitsluitend gemeenschappelijke attributen die we via alle recruiters altijd kunnen vullen.
Dit heeft als gevolg een wat complexere opzet van de applicatie maar wel met de mogelijkheid om bij bepaalde recruiterssites een hoeveelheid gegevens op te halen die wat meer zegt dan de gegevens in de huidige Aanvraag entiteit. 

Adriaan, 23 maart - vraag:
Vraag: is het nu zo dat de attributen in de entiteit Aanvraag in de bestaande webapplicatie altijd worden gevuld of alleen bij bepaalde recruiters? Ik twijfel er aan of dit wel het geval is?

