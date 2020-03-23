# Jobcrawler use cases

We staan nog aan het begin van onze samenwerking in dit jobcrawler project. 
Op dit moment zijn we nog heel erg in de verkenningsfase.
Door veel onduidelijkheden in de mogelijkheden van de webservices is het opstellen van use cases nog prematuur.

Omdat nog niet duidelijk is wat we van de jobcrawler gaan willen is de vraag wat we uiteindelijk van de jobcrawler willen verwachten. Om de gedachten wat te ordenen heb ik dit document opgesteld als een voorlopig levend ontwerpdocument.
Ik nodig hierbij eenieder van het jobcrawler project uit om eigen bevindingen rondom de jobcrawler informatie eraan toe te voegen.

## Inventarisatie op te halen informatie van recruiters

Het blijkt het geval te zijn dat de websites van de verschillende recruiters een zeer verschillende opbouw hebben.

Aan de hand van de uitkomsten van de verkenningsfase zou ik graag een inventarisatie willen hebben van de mogelijkheden en informatie-items die we zouden kunnen onttrekken aan de verschillende websites.
Samen kunnen bespreken wat we er uiteindelijk voor use cases uit zouden willen destilleren. Op het moment van schrijven is het maandag 23 maart, laten we als voorlopig doel stellen dat we dit voor eind maart weten en dan onze eerste use cases opbouwen.



## Gemeenschappelijke Aanvraag-informatie bestaande pakket
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
Verschillen tussen de websites van recruiters uit het bestaande pakket:

- De website van Yacht is voorzien van een vrij vast template, zodat daar ook bepaalde vaste kenmerken kunnen worden opgehaald zoals (voorzover ik mij herinner) duur van de opdracht en dergelijke.
- Myler heeft een hoeveelheid losse tekst die weliswaar is opgemaakt maar bij iedere opdracht net weer anders,
je kunt daar dus niet zoeken op vaste kopjes

Kennelijk is de Aanvraag entiteit voorzien van de gemeenschappelijke gegevens over meerdere recruiters.

De "oude" webcrawler is kennelijk op magische wijze al in staat  van verschillende recruiters toch de al zeer informatieve kenmerken uit de Aanvraag al te verzamelen ondanks de verschillen tussen de opbouw van de webpagina's.

