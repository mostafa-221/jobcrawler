# Jobcrawler use cases

Adriaan =- 23 maart We staan nog aan het begin van onze samenwerking in dit jobcrawler project.
Op dit moment zijn we nog heel erg in de verkenningsfase.
Door veel onduidelijkheden in de mogelijkheden van de webservices is het opstellen van use cases nog prematuur.

Omdat nog niet duidelijk is wat we van de jobcrawler gaan willen is de vraag wat we uiteindelijk van de jobcrawler willen verwachten. Om de gedachten wat te ordenen heb ik dit document opgesteld als een voorlopig levend ontwerpdocument.
Ik nodig hierbij eenieder van het jobcrawler project uit om eigen bevindingen rondom de jobcrawler informatie eraan toe te voegen.

## Inventarisatie op te halen informatie van recruiters

Adriaan =- 23 maart Het blijkt het geval te zijn dat de websites van de verschillende recruiters een zeer verschillende opbouw hebben.

Aan de hand van de uitkomsten van de verkenningsfase zou ik graag een inventarisatie willen hebben van de mogelijkheden en informatie-items die we zouden kunnen onttrekken aan de verschillende websites.
Samen kunnen bespreken wat we er uiteindelijk voor use cases uit zouden willen destilleren. Op het moment van schrijven is het maandag 23 maart, laten we als voorlopig doel stellen dat we dit voor eind maart weten en dan onze eerste use cases opbouwen.



## Gemeenschappelijke Aanvraag-informatie bestaande pakket
Adriaan =- 23 maart De informatie die bij de verschillende recruiters vandaan gehaald is door de verschillende scapers in het bestaande pakket ondergebracht in de entiteit
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
Adriaan =- 23 maart Verschillen tussen de websites van recruiters uit het bestaande pakket:

- De website van Yacht is voorzien van een vrij vast template, zodat daar ook bepaalde vaste kenmerken kunnen worden opgehaald zoals (voorzover ik mij herinner) duur van de opdracht en dergelijke.
- Myler heeft een hoeveelheid losse tekst die weliswaar is opgemaakt maar bij iedere opdracht net weer anders,
je kunt daar dus niet zoeken op vaste kopjes

Kennelijk is de Aanvraag entiteit voorzien van de gemeenschappelijke gegevens over meerdere recruiters.

De "oude" webcrawler is kennelijk op magische wijze al in staat  van verschillende recruiters toch de al zeer informatieve kenmerken uit de Aanvraag al te verzamelen ondanks de verschillen tussen de opbouw van de webpagina's.

## Keuzen, ideeën en mogelijke uitgangspunten
Adriaan, 23 maart - idee: Mogelijkheid zou zijn wat minder strak vast te houden aan uitsluitend gemeenschappelijke attributen die we via alle recruiters altijd kunnen vullen. Dit heeft als gevolg een wat complexere opzet van de applicatie maar wel met de mogelijkheid om bij bepaalde recruiterssites een hoeveelheid gegevens op te halen die wat meer zegt dan de gegevens in de huidige Aanvraag entiteit.

Adriaan, 23 maart - vraag: Vraag: is het nu zo dat de attributen in de entiteit Aanvraag in de bestaande webapplicatie altijd worden gevuld of alleen bij bepaalde recruiters? Ik twijfel er aan of dit wel het geval is?


-- Start toevoeging Timo, 24 maart --
## Haalbaarheid scrapen vacatures van websites welke zijn gebruikt in het Arabot project

In het Arabot project zijn scrapers voor de volgende vacaturesites aanwezig:
-	http://www.huxleyit.com
-	https://www.it-staffing.nl (nu https://www.destaffinggroep.nl/)
-	https://www.it-contracts.nl
-	https://www.myler.nl
-	https://www.yacht.nl

Een algemeen probleem op bovenstaande vacatures sites is dat een overzicht van vacatures goed binnen te halen is. Dit zijn dan ook entries vanuit een database welke beknopt, op vaste wijze, worden weergegeven.
Het probleem ontstaat bij de individuele vacatures. Binnen de website van een broker zit er geen cohesie in de opmaak van de vacatures.

### HuxleyIT
De inhoud van een vacature op HuxletIT bevindt zich in de `<div class="job-details_content">`.
Wat betreft uiterlijk zien de volgende twee vacatures er verschillend uit, ietwat andere indeling en opmaak.

- https://www.huxley.com/en-gb/job/java-developer---low-latency/HA-33123591_1584618807
```html
<div class="job-details__content">
<p>Are you a true technology enthusiast who knows a thing or two about distributed systems development, either living or willing to relocate to a location within 15 minute commute to Amsterdam?</p>
<p>A leading international FinTech company in the Amsterdam area is looking for a Java Developer to join their international development team. You will be working on diverse projects to build new business critical systems, making them scalable to accommodate future business needs. The team are working on a complex enterprise class applications and are seeking a new colleague with a sound understanding of low level Java programming and API Development experience.</p>
<p>You are not a 'classical' developer: you embrace the chance to work with new technologies and enjoy being on a journey of discovery. You understand that your ideas may not always work out and are happy to 'go back to the drawing board' to find a solution that does. You enjoy working in a collaborative environment where everybody has the chance to make a contribution.</p>
<p><strong>Profile of a Java Developer </strong></p>
<ul>
<li>Minimum 5 years of experience working with core Java</li>
<li>Experience in developing highly scalable, distributed systems</li>
<li>You love keeping up to date with technology and applying your new found knowledge in a complex business and technical environment</li>
<li>Good communication skills in English</li>
<li>BSc / MSc Computer Science or a related subject</li>
</ul>
<p>In addition to the pleasure of working with your team of skilled and motivated colleagues on an exciting, high-profile technical transformation you will also be well compensated financially. Alongside a competitive salary you will receive a comprehensive secondary benefits package including an interesting bonus scheme.</p><img src="https://counter.adcourier.com/SkxDUy4yMTk4Ni4xMTEzMEBzdGhyZWVubC5hcGxpdHJhay5jb20.gif">
</div>
```
- https://www.huxley.com/en-gb/job/java-developer---epayments-company/HA-33345564_1582019008
```html
<div class="job-details__content">
<p>Are you an experienced Java Developer (with EU passport) who is currently looking for a new challenge? Are you interested in banking/payments/finance and have strong back-end experience? Then this opportunity might be just for you! </p>
<p>One of the most innovative ePayment Companies located in North Holland is looking for a software engineer/developer who would like to join permanently their Technology department. You and your squad (that will consist of developers, test automation engineers, scrum masters and product owner) will be responsible for the full development lifecycle and you will also be working in the agile environment.</p>
<p>What can you expect? You will be building and deploying the applications using pipelines, so you are expected to have knowledge of Jenkins or any other CI/CD tool. Furthermore, the company is currently in the middle of a DevOps transformation so previous DevOps experience is considered a plus. </p>
<p><span style="text-decoration: underline;">Requirements</span></p>
<p>-3+ years hands-on experience in Java 8</p>
<p>-Versioning systems: git/SVN</p>
<p>-Maven experience</p>
<p>-Sping Framework experience</p>
<p>-SQL knowledge</p>
<p>-Any JIRA &amp; DevOps knowledge is a big plus</p>
<p><span style="text-decoration: underline;">What can you expect?</span></p>
<p>-Excellent salary combined with attractive incentives</p>
<p>-Fully paid pension scheme</p>
<p>-Bonus plan and 29,5 paid holidays</p>
<p>-International Environment</p>
<p>-Agile/Scrum way of working</p>
<p>-Option for personal &amp; professional development</p>
<p>Are you interested or know someone who might be? Please apply to the link or contact directly: Anna Wencel, Recruitment Consultant at Huxley</p><img src="https://counter.adcourier.com/QUFFTC43OTI2Mi4xMTEzMEBzdGhyZWVubC5hcGxpdHJhay5jb20.gif">
</div>
```

De skills welke wij in onze skillset zouden willen stoppen wordt bij de ene vacature ‘profile of a java developer’ genoemd, en bij de andere vacature staan deze onder ‘requirements’. Dat is echter niet het enige verschil. Kijkende naar de broncode is ook te zien dat de eerste vacature een on-gesoorteerde list `<ul><li>` gebruikt, terwijl de tweede vacature paragraph tags `<p>` gebruikt.

Verschillende soorten opmaak maakt het lastiger om de gegeven data aan de juiste variabelen toe te wijzen. Echter denk ik niet dat dit onmogelijk is voor HuxleyIT.

In de bestaande Arabot code zitten elementen welke wij kunnen overnemen voor HuxleyIT, maar dit vergt nog wel enige aanpassing. Hiernaast wordt deze code niet gebruikt om de skillset te vul-len.

### IT-staffing / De staffing group
Wanneer ik op de url van it-staffing klik, word ik doorgeleid naar de website van de staffing group.
Naast dat deze website bij mij ietwat traag is, is deze wel goed te gebruiken voor onze jobcrawler.
Vacatures bevatten vrij veel tekst, maar allen de volgende indeling, te onderscheiden wegens header `<h2>` tags:
-	Titel
-	Organisatie
-	Achtergrond opdracht
-	Functie eisen

URLs naar twee verschillende vacatures:
- https://www.destaffinggroep.nl/opdracht/full-stack-applicatieontwikkelaar-anr-24724/
```html
<article class="m--cms-content">
<h2>Titel </h2>
<p class="introduction">
Full-stack applicatieontwikkelaar
</p>
<h2>Organisatie </h2>
<p class="introduction">
<p>Het Ministerie van Economische zaken staat voor een duurzaam ondernemend Nederland. We zetten ons in voor een uitstekend ondernemersklimaat en een sterke internationale concurrentiepositie. Door de juiste randvoorwaarden te creëren en door ondernemers de ruimte te geven om te vernieuwen en te groeien. Door aandacht te hebben voor onze natuur en leefomgeving. Door samenwerking te stimuleren tussen onderzoekers en ondernemers. Zo bouwen we onze topposities in landbouw, industrie, diensten en energie verder uit en investeren we in een krachtig en duurzaam Nederland.</p><p><br></p><p>DICTU levert ICT en digitale diensten voor EZ en een aantal andere ministeries. Ze zet haar kennis en ervaring in om de beleids- en organisatie doelen van haar opdracht gevers optimaal te ondersteunen. Daarbij speelt ze in op de nieuwste technologische ontwikkelingen, zoals de Cloud. Ook borgt ze een hoogwaardige beveiliging van de gegevens die ze technisch beheert en bewaart. </p><p>Meer informatie vindt u op https://www.dictu.nl</p>
</p>
<h2>Achtergrond opdracht </h2>
<p class="introduction">
<p>De full-stack applicatieontwikkelaar (AO) hoogbeveiligd (die ook applicatiebeheer (AE) activiteiten uitvoert), hierna te benoemen als ontwikkelaar, is onderdeel van het scrumteam applicatiebeheer &amp; -ontwikkeling &quot;Scrumteam Delta&quot; dat ingericht is voor de applicaties voor Register Niet-Ingezetenen (RNI).</p><p><br></p><p>Er wordt gewerkt volgens Scrum.</p><p>De belangrijkste taken zijn:</p><ul><li>Het ontwerpen, bouwen en testen van de applicatie;</li><li>Het afstemmen van werkzaamheden met functioneel- en technisch beheer;</li><li>Het ondersteunen van technisch beheer bij het in productie nemen van releases;</li><li>Het adviseren en ondersteunen van de klant (RVIG) bij het doorvoeren van wijzigingen;</li><li>Het adviseren van technisch beheer bij de inrichting en uitvoeren van technisch applicatiebeheer</li></ul><p><br></p><p><b>Achtergrond opdracht</b></p><p>De werkzaamheden worden conform de Scrum-methode in nauw overleg tussen RVIG (functioneel beheer, product owner, functioneel testen) en DICTU (applicatiebeheer en testen voor oplevering) uitgevoerd</p><p><br></p><p>Bij de werkzaamheden wordt Technisch applicatiebeheer (TAB) actief betrokken en wordt actief aandacht besteed aan het in beheer nemen van projecten. Dit is de eerste fasen om de dienstverlening door te ontwikkelen naar de DEVOPS manier van werken</p><p><br></p><p>Om de opdracht uit te kunnen voeren is gedegen kennis en vaardigheden nodig van:</p><ul><li>Java</li><li>Build definitions- Source control</li><li>Code reviews</li><li>PostgreSQL</li><li>Testing</li><li>Unit tests</li><li>Automatische regressie tests (ART)- Systeem tests</li></ul><p><br></p><p>Tevens is kennis en ervaring met de volgende tools relevant:</p><ul><li>Jira</li><li>TopDesk</li><li>Robot Framework• Angular</li><li>Jenkins</li><li>GitLab</li></ul>
</p>
<h2>Functie eisen </h2>
<p class="introduction">
<p><b>Werk- en denkniveau</b></p><ul><li>Minimaal 2 jaar met ervaring binnen het gevraagde vakgebied, en die reeds binnen complexe omgevingen actief zijn (geweest).</li><li>Minimaal 2 jaar ontwikkelervaring met Java</li><li>Minimaal 2 jaar ervaring binnen de gevraagde technologiestack (Java/PostgreSQL).</li><li>HBO/WO werk- en denkniveau</li></ul><p><b>Kennis</b></p><ul><li>Ervaring binnen het gevraagde vakgebied, en binnen complexe omgevingen.</li><li>Kennis van, en ervaring met, werken binnen de overheid en met registers is een pre.</li><li>Kennis en ervaring met SCRUM en ITIL</li><li>Bekendheid met de benoemde stelselapplicaties</li><li>Ervaring met testwerkzaamheden</li></ul>
</p>
</article>
```
- https://www.destaffinggroep.nl/opdracht/senior-java-ontwikkelaar-anr-24759/
```html
<article class="m--cms-content">
<h2>Titel </h2>
<p class="introduction">
Senior Java ontwikkelaar
</p>
<h2>Organisatie </h2>
<p class="introduction">
<p>Belastingdienst te Apeldoorn</p>
</p>
<h2>Achtergrond opdracht </h2>
<p class="introduction">
<p>Wij zoeken op korte termijn een senior Java ontwikkelaar voor het huidige OB/ICT systeem. Als Java applicatieontwikkelaar bouw je mee aan het IT landschap onder architectuur. Je houdt je bezig met het vertalen van functionele vragen naar standaard functionaliteit binnen een geïntegreerd systeemlandschap, zodat de gebruikers op een effectieve en efficiënte wijze hun werkzaamheden kunnen uitvoeren en de geautomatiseerde verwerking vlekkeloos verloopt.</p><p><br></p><p>Je voert zelfstandig wijzigingen door aan bestaande programmatuur, creëert nieuwe programma’s en voert technische verbeteringen door. Ervaring met onderstaande is een pré J2EE (met name JPA en JMS)</p><ul><li>WebSphere Application Server (WAS)</li><li>SQL/DB2- MQ Series- Spring</li><li>Hibernate</li><li>Struts</li><li>Wicket</li><li>Java 8</li><li>Junit</li><li>JAXWS</li><li>JAXB</li><li>OO/Design patterns</li><li>Maven</li><li>Jenkins</li><li>SonarQube</li><li>Webservices</li><li>SOA</li><li>Micro Services</li><li>Angular</li><li>Cucumber</li><li>Selenium</li><li>Fitnesse</li><li>SOAP</li><li>REST</li></ul><p><br></p><p><b>Achtergrond opdracht</b></p><p>De IBS Omzetbelasting is verantwoordelijk voor adviesregie, architectuur, ontwerp, bouw, test, integratie, (gebruikers)documentatie en het implementatierijp opleveren van de aangepaste bedrijfsprocessen en informatievoorziening.IBS Omzetbelasting is verantwoordelijk voor de integratie van de gehele oplossing die gerealiseerd kan worden door de eigen teams van IBS OB, STS, GBS en raakvlakken binnen IBS. IBS Omzetbelasting stemt de architectuur af, stuurt de realisatie en de planning via de releasetrain en verstrekt de opdrachten naar de raakvlakken</p>
</p>
<h2>Functie eisen </h2>
<p class="introduction">
<p><b>Opleiding, Certificaten</b></p><ul><li>Senior ervaring in java ontwikkeling</li></ul><p><b>Werk- en denkniveau</b></p><ul><li>Minimaal 4 jaar Java ontwikkelaar</li></ul><p><b>Competenties</b></p><ul><li>Vakkundig en professioneel als ontwikkelaar staat voor zijn/haar vak en heeft drive om een kwalitatief hoogwaardig product op te leveren.</li><li>Veranderingsgericht en creatief: iemand die gemakkelijk in iets nieuws durft te stappen of een nieuw pad durft te verkennen.</li><li>Assertieve persoonlijkheid met een kritische blik en een oplossingsgerichte, samenwerkende mentaliteit.</li></ul>
</p>
</article>
```

### IT-contracts
IT-contracts bevat veel vacatures, ten opzichte van de andere websites, met het zoekwoord java.
Zaken als locatie en uur per week zijn eenvoudig uit de vacature te halen doordat deze in vaste volgorde in een kolom naast de vacature staan.
Het verwerken van data uit de vacature zal bij IT-contracts een stuk lastiger worden. De inhoud van de vacature is een lap tekst, nagenoeg zonder html opmaak. De broncode van verschillende vacatures laat zien dat de tekst in de meeste gevallen voorzien wordt van breaks `<br />`. Een enkele vacature heeft paragraph tags en een andere vacature maakt gebruik van een ongesor-teerde list.

URLs naar twee vacatures:
- https://www.it-contracts.nl/vacature_Java_Ontwikkelaar_Parkeren_Sr.__254194/search/java/from/0
```html
<br />
<!--bluetrail-->
Opdrachtspecificaties
<br />Referentienummer: BTIT31398
<br />Omgeving: Rotterdam
<br />Startdatum: 2 mei 2020
<br />Duur: 12 maanden
<br />Optie op verlenging: Ja 1x 12 maanden
<br />Aantal uur per week: 24
<br />Intakegesprek: Week 16
<br />Sluitingsdatum: Woensdag 25 maart om 09.00 uur. Reacties na deze tijd zullen in principe niet worden meegenomen in het selectieproces.
<br />
<br />BlueTrail is op zoek naar een Senior Java Ontwikkelaar Parkeren voor 24 uur per week.
<br />
<br />Opdracht/functie omschrijving:
<br />
<br />Het Expertisecentrum SysteemOntwikkeling (ESO) is leverancier van het concern Rotterdam voor maatwerkoplossingen om de dienstverlening o.a. naar burgers en het bedrijfsleven te verbeteren. Het is een kennisorganisatie en partner voor haar opdrachtgevers. ESO ontwikkelt software volgens de agile principes met de nieuwste methoden en technieken binnen de kaders van de concernarchitectuur. De organisatie telt 25 java developers. Programmeren is je passie, en je vindt het mooi om voor een wereldstad oplossingen te verzinnen om de burger beter van dienst te zijn en efficiencywinst voor de stad te realiseren.
<br />
<br />Taken/werkzaamheden:
<br />
<br />Programmeren is je passie, en je vindt het mooi om voor een wereldstad oplossingen te verzinnen om de burger beter van dienst te zijn en efficiencywinst voor de stad te realiseren.
<br />
<br />Je bent onderdeel van een zelfsturend Scrum team dat iedere 2 weken software van hoge kwaliteit oplevert. Jij voelt je net als alle teamleden verantwoordelijk voor alle aspecten, vanaf de vraag tot en met de oplevering in productie. Je bent kritisch, je helpt de klant om zijn wensen helder te krijgen, je schrijft clean code die van hoge kwaliteit is, met bijbehorende unit- en integratietesten, je ondersteunt zo nodig bij deployments naar productie.
<br />
<br />Als vakvrouw/vakman krijg je veel ruimte om jezelf verder te ontwikkelen, omdat "voortdurende verbetering" voor ons een belangrijke waarde is. We leren van elkaar door het organiseren en bijwonen van code camps en door het uitvoeren van codereviews en samen pairen.
<br />
<br />Functie eisen
<br />KNOCK-OUT:
<br />
<br />Je beschikt minimaal over een afgeronde relevante HBO opleiding (zoals Hogere Informatica Opleiding en/of Technische Informatica) of een afgeronde WO Beta studie.
<br />Je beschikt over minimaal 8 jaar ervaring als Java Ontwikkelaar (senior) opgedaan in de afgelopen 10 jaar.
<br />Je beschikt over minimaal 5 jaar ervaring met het Spring Framework opgedaan in de afgelopen 8 jaar.
<br />Je beschikt over minimaal 5 jaar ervaring met Oracle SQL opgedaan in de afgelopen 8 jaar.
<br />Je beschikt over minimaal 5 jaar ervaring met JPA/Hibernate opgedaan in de afgelopen 8 jaar.
<br />Je beschikt over minimaal 5 jaar ervaring met Webservices (SOAP en REST) opgedaan in de afgelopen 8 jaar.
<br />Je beschikt over minimaal 5 jaar ervaring met Wicket opgedaan in de afgelopen 8 jaar.
<br />Reacties van kandidaten die geen kennis van/ervaring met de gestelde eisen hebben, worden door de opdrachtgever terzijde gelegd.
<br />
<br />Gunningscriteria:
<br />
<br />Je beschikt over kennis van de gemeentelijke basisregistratie personen (BRP, StUF koppelvlakken).                                
<br />Je beschikt over kennis van milieukenmerken van voertuigen en de RDW koppelvlakken.
<br />Je beschikt over kennis van Android, iOS en progressive webapps en hoe die te integreren met enterprise backend applicaties.                     
<br />Je beschikt over minimaal 2 jaar ervaring met Maven.                 
<br />Je beschikt over minimaal 2 jaar ervaring met XSLT.                 
<br />Je beschikt over minimaal 2 jaar ervaring met JBOSS EAP6 of hoger.         
<br />Je beschikt over kennis en ervaring met Jira, Confluence en Bamboo.         
<br />Je beschikt over kennis en ervaring met GIT / Bitbucket.
<br />Je beschikt over kennis en ervaring met gemeentelijke processen.   
<br />Je beschikt over kennis van het domein Parkeren en parkeerproducten van gemeenten.
<br />
<br />Competenties:
<br />
<br />Resultaatgerichtheid
<br />Kwaliteitsgericht (clean code)
<br />Integriteit
<br />Planmatig werken
<br />Samenwerken
<br />Sociaal
<br />Gericht op delen van kennis
<br />Beheersing van de Nederlandse taal
<br /><br />
```
- https://www.it-contracts.nl/vacature_Senior_Java_Ontwikkelaar_254231/search/java/from/0
```html
<h1>
<!--<span style="color:#4f4f4f">Gezocht voor actuele ICT-opdracht:</span> <br />-->
<a href="/solliciteren/vacature_Senior_Java_Ontwikkelaar_254231/search/java/from/0" style="color: #0099cc;">
Senior Java Ontwikkelaar, Apeldoorn</a></h1>
<span style="font-weight:bolder">Omschrijving:</span><br />
<p dir="LTR" align="LEFT">Voor de Belastingdienst in Apeldoorn zijn we op zoek naar een<br /> <br /> Senior Java Ontwikkelaar<br /> <br /> Startdatum: 1-4-2020<br /> Duur: 6 maanden met optie op verlenging<br /> Inzet: 40 uren per week<br /> <br /> OPDRACHT<br /> Wij zoeken op korte termijn een senior Java ontwikkelaar voor het huidige OB/ICT systeem. Als Java applicatieontwikkelaar bouw je mee<br /> aan het IT landschap onder architectuur. Je houdt je bezig met het vertalen van functionele vragen naar standaard functionaliteit binnen<br /> een geïntegreerd systeemlandschap, zodat de gebruikers op een effectieve en efficiënte wijze hun werkzaamheden kunnen uitvoeren en de<br /> geautomatiseerde verwerking vlekkeloos verloopt.<br /> <br /> Je voert zelfstandig wijzigingen door aan bestaande programmatuur, creëert nieuwe programma’s en voert technische verbeteringen door.<br /> Ervaring met onderstaande is een pré: J2EE (met name JPA en JMS)<br /> - WebSphere Application Server (WAS)<br /> - SQL/DB2<br /> - MQ Series<br /> - Spring<br /> - Hibernate<br /> - Struts<br /> - Wicket<br /> - Java 8<br /> - Junit<br /> - JAXWS<br /> - JAXB<br /> - OO/Design patterns<br /> - Maven<br /> - Jenkins<br /> - SonarQube<br /> - Webservices<br /> - SOA<br /> - Micro Services<br /> - Angular<br /> - Cucumber<br /> - Selenium<br /> - Fitnesse<br /> - SOAP<br /> - REST<br /> Belastingdienst specifieke tooling<br /> De IBS Omzetbelasting is verantwoordelijk voor adviesregie, architectuur, ontwerp, bouw, test, integratie, (gebruikers)documentatie en het implementatierijp opleveren van de aangepaste bedrijfsprocessen en informatievoorziening.<br /> IBS Omzetbelasting is verantwoordelijk voor de integratie van de gehele oplossing die gerealiseerd kan worden door de eigen teams van IBS OB, STS, GBS en raakvlakken binnen IBS. IBS Omzetbelasting stemt de architectuur af, stuurt de realisatie en de planning via de releasetrain en verstrekt de opdrachten naar de raakvlakken.<br /> <br /> EISEN<br /> Senior ervaring in java ontwikkeling (&gt;4 jr.)<br /> <br /> WENSEN<br /> Vakkundig en professioneel als ontwikkelaar staat voor zijn/haar vak en heeft drive om een kwalitatief hoogwaardig product op te leveren.<br /> Veranderingsgericht en creatief: iemand die gemakkelijk in iets nieuws durft te stappen of een nieuw pad durft te verkennen. Assertieve persoonlijkheid met een kritische blik en een oplossingsgerichte, samenwerkende mentaliteit.<br /> <br /> Aanbiedingen van CV en onder vermelding van tarief dienen uiterlijk 24 maart om 12:00 uur in ons bezit te zijn.<br /> <br /> </p>
<br /><br />
```

### Myler
Myler heeft enkele vacatures welke op het oog de volgende indeling aanhoudt:
-	Opdrachtbeschrijving
-	Achtergrond opdracht
-	Organisatorische context en cultuur
-	Eisen
  -	Opleiding, certificaten, kennisniveau
  -	Professionele kennisgebieden
  -	Kwaliteitenprofielen
  -	Werk- en denkniveau
-	Wensen
  - Competenties
  - Aanvullende kennis
  -	Overige functiewensen

De hoofdpunten hierboven benoemd zijn bij iedere vacature op Myler aanwezig. Iets als ‘overige functiewensen’ is niet bij alle vacatures aanwezig.

URLs:
- https://www.myler.nl/opdrachten-single/performancetester-66967/
```html
<div class="text">
<p><u>Opdrachtbeschrijving</u><br />Wij zoeken een performancetester. De primaire taak van deze performance tester richt zich op alles wat met de performance van de door IV-interactie opgeleverde producten. voor het zakelijk- en het douaneportaal (inclusief de bijbehorende formulieren) te maken heeft. Dit is begeleiden van de systeemontwikkeling en performancetesten doen gedurende het gehele ontwikkelproces (van architectuurconcept tot en met de testfase). We willen een zo vroeg mogelijke detectie van de performance bottlenecks. Je moet er mede voor zorgen, dat de applicatie voldoet aan de gestelde performance eisen. Je geeft architecten, ontwerpers, en bouwers vroegtijdig inzicht in de consequenties voor de performance van gemaakte c.q. te maken keuzes. Om je werk zo effectief mogelijk te kunnen doen, werk je nauw samen met het Performance Competence Center. De doelen van je werk zijn het beoordelen van de kwaliteit van de opgeleverde producten, het opzetten (bepalen scope, meetpunten, diepgang, opstellen testcases, loadmodellen, testaanpak) en uitvoeren van testen t.a.v. performance en robuustheid en het adviseren aangaande performance. En je rapporteert over de uitgevoerde testen.<br /><u><br /></u></p>
<p><u>Achtergrond opdracht<br /></u>Binnen DCS is er een Performance Competence Center. Dit is een afdeling, die als doel heeft om de performance van de systemen op een hoog peil te brengen en te houden, opdat ook de systemen qua performance een hoog niveau hebben. Voor het uitvoeren van de performancetesten voor het zakelijk- en het douaneportaal van IV-interactie ben jij de testcapaciteit die IV interactie levert en je werkt daarbij nauw samen met het PCC. Het kan voor een goede uitvoering van je werk nodig zijn, dat je per week enkele dagen werkt op locatie bij het PCC, maar je blijft altijd je werk uitvoeren binnen de context van de releasetrain van IV-interactie.<br /><u><br /></u></p>
<p><u>Organisatorische context en cultuur<br /></u>De IV- organisatie van de Belastingdienst is verantwoordelijk voor en verzorgt de ICT- voorzieningen. Het merendeel van de applicaties wordt op dit moment door de IV- organisatie zelf ontwikkeld, onderhouden en beheerd in het eigen data center.</p>
<p>Naast de zorg voor continuïteit op de massale heffing- en inningsprocessen die plaatsvinden binnen een degelijke, stabiele omgeving, wordt er tevens volop gewerkt aan modernisering van het IV- landschap. Dit gebeurt deels intern door gebruik te maken van de expertise die intern aanwezig is, maar ook door het aantrekken van (kant-en-klaar) oplossingen en expertise uit de markt.<br /><u><br /></u></p>
<p><u>Eisen<br /></u><span><u>Opleiding, Certificaten, Kennisniveau</u><br />-&nbsp;HBO werk- en denkniveau<br />-&nbsp;Certificaat T-Map foundation/engineer, T-Map manager en/of ISTQB<br /></span><u><br /></u></p>
<p><span><u>Professionele Kennisgebieden</u><br />-&nbsp;Service Design<br /></span><u><br /></u></p>
<p><span><u>Kwaliteitenprofielen</u><br />- Applicatieontwikkeling<br />- Testmanagement<br /></span><u><br /></u></p>
<p><span><u>Werk- en denkniveau</u><br />-&nbsp;Testervaring binnen de ICT-branche bij voorkeur als testspecialist, performance tester en/of testautomatiseerder / 5 jaar ervaring<br />- Ervaring met het schrijven van plannen m.b.t. performance en het opstellen van rapportages<br />- Kennis van computersystemen en -applicaties, databases en netwerken<br /></span></p>
<p><br /><u>Wensen</u><br /><u>Competenties<br /></u>- Sterk ontwikkeld analytisch vermogen;<br />- Oog voor kwaliteit;<br />- Communicatief Sterk;<br />- Zelfstandig;<br />- Doorzettingsvermogen;<br />- Overtuigingskracht;<br />- Flexibel;<br />- Stressbestendig;<br /><u><br /></u></p>
<p><u>Aanvullende kennis<br /></u>- Kennis van de basisprincipes van performance(testen) en testautomatisering;<br />- Kennis van websphere en/of Java en monitoring tools;<br />- GIT, Jira en Confluence;<br />- Jenkins, Jmeter en, Gatling<br /></p>                            
</div>
```
- https://www.myler.nl/opdrachten-single/bpm-ontwikkelaar-66319/
```html
<div class="text">
<p><u>Opdrachtbeschrijving</u><br />Binnen IV Douane zijn we op zoek naar een ervaren BPM-ontwikkelaar voor de keten Toezicht Goederen &amp; Risicomanagement (TG&amp;R).De keten TG&amp;R is verantwoordelijk voor het ontwikkelen, beheren en beschikbaar stellen van IV-voorzieningen voor de procesgebieden "Binnenbrengen, Uitgaan &amp; Provianderen", "Fysiek Toezicht" en "Risico &amp; Intelligence". De ontwikkeling van nieuwe applicaties en het verbeteren of uitbreiden van bestaande applicaties vindt kort-cyclisch plaats m.b.v. Agile-ontwikkelmethoden. Binnen de keten zijn meerdere multidisciplinaire teams werkzaam. Teams bestaan uit een mix (multidisciplinair) van ontwikkelaars, aangevuld met een scrummaster en productowner. <br /><br />Voor het team Pluto (procesgebied "Risico &amp; Intelligence") zoeken we een ervaren BPM-ontwikkelaar.<br /><br />De werkzaamheden zullen voornamelijk bestaan uit business proces modellering alsmede de realisatie van deze processen, maar ook zaken als review- en test-werkzaamheden zullen binnen het team opgepakt moeten worden als de situatie daarom vraagt (bereidheid tot T-shape model).Het werk zelf wordt door het team iom productowner en businessowner bepaald, vastgesteld per PI-periode en in sprints van 2 weken elk uitgevoerd.<br /><br />Specifieke vakkennis:<br />- ruime kennis en ervaring met BPM-standaard<br />- kennis en ervaring met BPM-advanced is een pré<br />- kennis en ervaring met Java is een pré<br /><br />Naast de vanzelfsprekende brede vakkennis die van de ontwikkelaar gevraagd wordt, wordt ook gevraagd:<br />- kennis van SCRUM en ervaring met SCRUM-projecten<br />- kennis en ervaring met SAFe is een pré<br />- persoonlijke vaardigheden: goede communicatieve vaardigheden, gericht op samenwerking (“teamplayer”), flexibele instelling, drive en doorzettingsvermogen<br /><br />We zoeken iemand die echt gemotiveerd is om in een Agile/Scrum omgeving te werken en daaraan ook actief wil bijdragen (binnen en buiten het eigen team). Algemene taken van een teamlid binnen een MultiDisciplinair-team zijn:<br />- Je draagt bij aan het verfijnen van backlog items<br />- Je draagt bij aan het plannen van iteraties<br />- Je draagt bij aan het meten en verbeteren van de uitgevoerde werkzaamheden<br />- Je neemt actief deel aan de benodigde scrum sessies en events<br />- Je werkt samen met andere multidisciplinaire teams om te zorgen dat de keten altijd blijft werken<br />- Je ondersteunt en vervangt daar waar nodig collega's binnen het multidisciplinaire team.<br /><br /><u>Achtergrond opdracht</u><br />Binnen IV-Douane keten TG&amp;R (Toezicht Goederen &amp; Risicomanagement) zijn we voor het procesgebied “Risico &amp; Intelligence" op zoek naar uitbreiding van onze BPM-ontwikkel-expertise.<br /><br /><u>Organisatorische context en cultuur</u><br />De IV- organisatie van de Belastingdienst is verantwoordelijk voor en verzorgt de ICT-&nbsp; voorzieningen. Het merendeel van de applicaties wordt op dit moment door de IV- organisatie zelf ontwikkeld, onderhouden en beheerd in het eigen data center.<br /><br />Naast de zorg voor continuïteit op de massale heffing- en inningsprocessen die plaatsvinden binnen&nbsp; een degelijke, stabiele omgeving, wordt er tevens volop gewerkt aan modernisering van het IV-&nbsp; landschap. Dit gebeurt deels intern door gebruik te maken van de expertise die intern aanwezig is,&nbsp; maar ook door het aantrekken van (kant-en-klaar) oplossingen en expertise uit de markt.<br /><br /><u>Eisen</u><br /><u>Opleiding, Certificaten, Kennisniveau</u><br />- IBM BPMs<br />- Java Script<br /><br /><u>Professionele Kennisgebieden</u><br />- Programmeren<br /><br /><u>Kwaliteitenprofielen</u><br />-&nbsp;Applicatieontwikkeling<br /><br /><u>Werk- en denkniveau</u><br />-&nbsp;HBO werk- en Denkniveau / 2 jaar ervaring<br />- Realiseren van integraties / 3 jaar ervaring<br />- Modeleren van BPD's in IBM-BPMS / 2 jaar ervaring&nbsp;<br /><br /><u>Wensen</u><br /><u>Competenties</u><br />- Goede communicatieve vaardigheden, analytisch, gericht op samenwerking (“teamplayer”) en kennisdeling/overdracht, flexibele instelling, drive en doorzettingsvermogen.<br />- Gericht op kennisdeling/-overdracht<br />-&nbsp;NL sprekend<br />- 100% on-site<br /><br /><u>Aanvullende kennis</u><br />- Kennis van ODM is pré<br />- Kennis van Java, Jenkins, Maven, Splunk, Webservices, JPA, Gherkin is pré<br />- Kennis van testframeworks zoals JUnit, Cucumber, Fitnesse, Selenium en of Robot Framework is pré<br /><br /><u>Overige functiewensen<br /></u>- Kennis van Git, JIRA, Bitbucket, en Confluence<br /></p>                            
</div>
```

De vacature inhoud bevindt zich in `<div class="tekst">` de verschillende kopjes zouden te onder-scheiden moeten zijn wegens de underline tags `<u>`. Zolang deze indeling gehanteerd wordt, zouden wij Myler voor onze jobcrawler moeten kunnen gebruiken.

### Yacht
Yacht maakt gebruik van een vaste indeling door middel van headers (<h2) tags. Deze indeling is als volgt:
-	Over de functie
-	Functie-eisen
-	Arbeidsvoorwaarden
-	Bedrijfsinformatie
-	Contactinformatie

URLs naar vacatures:
- https://www.yacht.nl/vacatures/9079689/pega-software-engi-neer?position=3&filter=ja&listingType=regular&zoekterm=java&soortdienstverband=Detachering
```html
<div class="description">
<h2>Over de functie</h2>
<p>As a DevOps Software Engineer you, together with the team, are responsible for the Product Feature / Business Rules Engine within KPN. KPN’s Business Rules Engine (Pega) determines the package availability for customers (capability checker/L3CC), is the application in which all marketing campaigns are configured, is KPN’s speed- and leadtime ‘calculator’ and the Pega Bulkorder tool is the go-to rationalization tool for&nbsp;</p>
<p>all portfolio-related migrations and rationalizations of KPN’s fixed consumer market.</p>
<p><br></p>
<p>As a Software Engineer you are responsible for:</p>
<p>• Development of the Business Rules Engine of KPN (Pega)</p>
<p>• Development and introduction of new portfolio for KPN</p>
<p>• Act as a DevOps Engineer taking ownership over KPN’s Business Rules Engine</p>
<p>• Configuring KPN’s marketing campaigns like the next Black Friday, FOX Sports, FTTH-related campaigns, and for new product introductions.</p>
<p>• Setting up a (almost) zero downtime CICD pipeline</p>
<p>• Create new functional designs or redesign business processes for new changes</p>
<p>• Help key users by answering questions and help them in unexpected circumstances</p>
<p><br></p>
<h2>Functie-eisen</h2>
<p>• Good experience in Pega and Python</p>
<p>• Proficient in managing the entire Software Development Lifecycle (SDLC) involving requirement gathering, requirement analysis, design, development, technical documentation, unit testing, delivery and maintenance.</p>
<p>• Pega v7x or above technical certification</p>
<p>• Candidates with PEGA CSSA or CSA certification</p>
<p>• Understand the Agile and DevOps mindset and Scrum way-of-work</p>
<p><br></p>
<p>• Understand the KPN processes and technologies of KPN Consumer Market</p>
<p>• Experience in tools/languages: Python, Docker, Jenkins, Java is a pré</p>
<p>• Good communication skills in English/Dutch</p>
<p>• Must have working permit for The Netherlands or EU passport</p>
<h2>Arbeidsvoorwaarden</h2>
<p>36h per week</p>
<p>Place: Groningen</p>
<p>KPN locatie: stationsplein 7.</p>
<p><br></p>
<p>Interested?</p>
<p>The deadline for this vacancy is 24 March 2020</p>
<p>You'll receive 26 March if you are selected for an interview the 27 or not.</p>
<p>This will be a video meeting </p>
<p><br></p>
<p>Starting date: 1 April 2020 from home!</p>
<p><br></p>
<p>#Corona safe working environment</p>
<p><br></p>
<h2>Bedrijfsinformatie</h2>
<p>The Pega backlog is loaded with a lot of exciting items, which will be key in realizing KPN’s new strategy. </p>
<p>Examples include: introduction of new portfolio items, containerization (DOCKER), setting up a true CICD pipeline with almost zero downtime, portfolio rationalizations, configuring marketing campaigns, the integration of mobile (VAMO) and playing a crucial role in KPN’s new FTTH-strategy.</p>
<p><br></p>
<p>You are going to work with 10 other collegues. During the Corona virus it will be in a digital setting.</p>
<h2>Contactinformatie</h2>
<p>Voor meer informatie neem je contact op met Tim Lai via telefoonnummer 06-30349860<br><br><b>Werken voor Yacht</b><br>Yacht is dé organisatie van en voor professionals. Wij verbinden professionals en organisaties die het verschil willen maken. Ons doel is optimaal resultaat: jou als professional uitdagend werk bieden waarmee jij de organisaties van onze opdrachtgevers blijvend verbetert. Behoor jij tot de beste professionals in jouw vak? Wil je samen met vakgenoten het verschil maken bij toonaangevende organisaties? We geloven dat diverse teams van belang zijn voor ons als lerende organisatie die voorop wil blijven lopen in de wereld van werk. Want juist verschillen tussen mensen zorgen voor groei. Van collega's, klanten, kandidaten en daarmee van Yacht. Heb jij een uniek talent? We ontmoeten je graag.</p>
</div>
```
- https://www.yacht.nl/vacatures/9079768/oracle-software-develo-per?position=2&filter=ja&listingType=regular&zoekterm=java&soortdienstverband=Detachering
```html
<div class="description">
<h2>Over de functie</h2>
<p><b>Our work environment</b></p>
<p>Group Services is a part of ING TECH and manages the applications, systems and processes needed for ING Group entities like Finance, Risk and HR. Group Services takes care of application development and configuration, and performs various operational IT activities. Group Services is also responsible for the development and delivery of change initiatives in these ING Group entities, whereby we adhere to the Spotify model and have implemented the Agile Way of Working. GFRS (Group Finance Reporting Services) chapter is positioned within Statutory &amp; Regulatory Reporting tribe and is primarily responsible for delivering IT services and application changes regarding financial and regulatory reporting to Group Finance and various financial authorities across the globe. The chapter has an agile mindset, a drive for continuous improvement, and eagerness to learn and adapt. Especially looking at the numerous requirements coming our way, this chapter plays a crucial role and is at the heart of ING Finance.</p>
<p><br></p>
<p>﻿</p>
<h2>Functie-eisen</h2>
<p><b>Who we're looking for</b></p>
<p>We're maintaining and rebuilding an OracleDB based platform for ingesting data, financial processing and reporting. We're counting on your help with analysing the application and functional requirements, and finding ways to migrate business logic from PL/SQL to Drools rule engine and microservices. As such, we're looking for a colleague who's deeply familiar with products and financial reports in retail/wholesale banking.</p>
<p><br></p>
<p><b>Education/Working experience</b></p>
<ul>
 <li>&nbsp;&nbsp;&nbsp;&nbsp;Experience in the banking sector</li>
 <li>&nbsp;&nbsp;&nbsp; Professional experience in software development</li>
 <li>&nbsp;&nbsp;&nbsp; Bachelor in IT, Finance, Mathematics or Physics</li>
</ul>
<p><br></p>
<p><b>Required competencies&nbsp;</b></p>
<ul>
 <li>&nbsp;&nbsp;&nbsp; Deep understanding of banking products</li>
 <li>&nbsp;&nbsp;&nbsp; Experience with Drools rule engine</li>
 <li>&nbsp;&nbsp;&nbsp; Knowledge of Java or other JVM-based language on a competent level</li>
 <li>&nbsp;&nbsp;&nbsp; Good SQL skills</li>
 <li>&nbsp;&nbsp;&nbsp; Team player, self-driven with customer focus</li>
</ul>
<h2>Arbeidsvoorwaarden</h2>
<p>Contract through Yacht. </p>
<h2>Bedrijfsinformatie</h2>
<p>At ING we work on innovations for 8.5 million customers every day. We are a bank with great ambitions, where ambitious people work. We ask a lot from our employees. But they also get a lot in return! What makes you happy when it comes to work and working conditions?</p>
<p><br></p>
<p>ING’s mission is "to enable people to stay one step ahead, both private and business." Our customers, but certainly also our employees. So our employment conditions are distinctive, and sometimes even surprising.</p>
<p><br></p>
<h2>Contactinformatie</h2>
<p>Voor meer informatie neem je contact op met Hans Bakker via telefoonnummer 06-20527051<br><br><b>Werken voor Yacht</b><br>Yacht is dé organisatie van en voor professionals. Wij verbinden professionals en organisaties die het verschil willen maken. Ons doel is optimaal resultaat: jou als professional uitdagend werk bieden waarmee jij de organisaties van onze opdrachtgevers blijvend verbetert. Behoor jij tot de beste professionals in jouw vak? Wil je samen met vakgenoten het verschil maken bij toonaangevende organisaties? We geloven dat diverse teams van belang zijn voor ons als lerende organisatie die voorop wil blijven lopen in de wereld van werk. Want juist verschillen tussen mensen zorgen voor groei. Van collega's, klanten, kandidaten en daarmee van Yacht. Heb jij een uniek talent? We ontmoeten je graag.</p>
</div>
```

Wel valt direct op dat de opmaak onder deze kopjes anders is. Bij de ene vacature is een op-somming met list tags gebruikt, terwijl een andere vacature gebruik maakt van paragraph tags.
Ik ben van mening dat dit geen belemmering hoeft te vormen voor het gebruik van Yacht bij onze jobcrawler. Het is met jsoup namelijk mogelijk om het aantal (bijvoorbeeld list) tags te tellen in een selectie. Hierbij kan eenvoudig onderscheid worden gemaakt tussen het gebruik van list of pa-ragraph tags bij de gevraagde functie-eisen en kunnen beiden geïmporteerd worden.

### Conclusie
Van de gebruikte websites in het Arabot project ben ik van mening dat wij de volgende sites kun-nen gebruiken voor onze jobcrawler:
-	HuxleyIT
-	IT-staffing / de staffing group
-	Myler
-	Yacht

Ondanks dat de website van it-contracts ten opzichte van de anderen meer java vacatures heeft, is het nagenoeg onmogelijk om deze vacatures op een juiste manier te scrapen. We kunnen altijd een poging doen om it-contracts te gebruiken en zelf keywords in te geven waarop binnen de pagina moet worden gezocht. Het is dan echter wel zo dat een vacature welke niet een ingege-ven keyword bevat niet gebruikt zal worden.

Verder heb ik nog geen onderzoek gedaan naar andere vacature sites. We kunnen wel verder kijken dan de gebruikte websites in het Arabot project.

-- Eind toevoeging Timo, 24 maart --
