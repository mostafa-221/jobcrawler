package scrapers;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.scrapers.HTMLStructureException;
import nl.ordina.jobcrawler.scrapers.JobBirdScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
//import sun.security.x509.InhibitAnyPolicyExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class JobBirdScraperTest  {


    @InjectMocks
    private JobBirdScraperTestHelp jobBirdScraperTestHelp;


    @Mock
    private Document documentMock;

    @Mock
    Elements elemsMock;

    @Mock
    Elements elementsMock, childrenMock;

    @Mock
    Element e2Mock;

    @Mock
    Element e3Mock;

    @Mock
    Element e4Mock;




    @Test
    public void getSEARCH_URL_test() {
        jobBirdScraperTestHelp    = new JobBirdScraperTestHelp();
        // check if the search url is still the same
        String sUrl =  jobBirdScraperTestHelp.getSEARCH_URL();
        Assert.assertEquals(sUrl, "https://www.jobbird.com/nl/vacature?s=java");
    }


    //happy flow met als resultaat 5 pagina's
    @Test
    public void getTotalnrOfPagesTestFile_HappyFlow() throws IOException {
        String filename = "testfiles/jobbird01_should_count_5_pages.htm";  // should count 5 pages
        ClassLoader classLoader = new JobBirdScraperTest().getClass().getClassLoader();

        File inputFile = new File(classLoader.getResource(filename).getFile());

        // System.out.println("File found:" + inputFile.exists());
        // String content = new String(Files.readAllBytes(inputFile.toPath()));
        // System.out.println(content);

        Document doc = Jsoup.parse(inputFile, "UTF-8", "");

       int count =  jobBirdScraperTestHelp.getTotalNumberOfPagesHelp(doc);
       assertEquals(5, count);
    }


    /* page structure altered, page number section not found
     * should return zero
     */
    @Test
    public void getTotalnrOfPagesTestFile_invalidPageStructure() throws IOException {
        String filename = "testfiles/jobbird02_invpage.htm";  // should count 5 pages
        ClassLoader classLoader = new JobBirdScraperTest().getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource(filename).getFile());
        Document doc = Jsoup.parse(inputFile, "UTF-8", "");

        int count =  jobBirdScraperTestHelp.getTotalNumberOfPagesHelp(doc);
        assertEquals(0, count);
    }



    // build mock document by using elements with html doc structure for 2 pages, check the happy flow
    @Test
    public void getTotalnrOfPagesTest_HappyFlow() {
        jobBirdScraperTestHelp    = new JobBirdScraperTestHelp();

        try {
            Element el1 = new Element("el1");
            Element el2 =  new Element("el2");
            Element el3 =  new Element("el3");

            Elements children = new Elements();
            children.add(el1);
            children.add(el2);
            Element parent1 = new Element("parent1");
            parent1.appendChild(el1);
            parent1.appendChild(el2);
            Element parent2 = new Element("parent2");
            parent2.appendChild(parent1);
            parent2.appendChild(el3);

            //when (connectionDocumentServiceMock.getConnection(anyString())).thenReturn(documentMock);
            when (documentMock.select("span.page-link")).thenReturn(children);

//            when (e2Mock.parent()).thenReturn(parent1);
            int count =  jobBirdScraperTestHelp.getTotalNumberOfPagesHelp(documentMock);
            assertEquals(2, count);
        } catch (Exception E) {

        }
    }


    /* set vacancy title using vacancy htm file
    */
    @Test
    public void setVacancyTitle_HappyFlow() throws IOException, HTMLStructureException {
        String filename = "testfiles/jobbird03_vacancy.htm";  // should count 5 pages
        ClassLoader classLoader = new JobBirdScraperTest().getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource(filename).getFile());
        Document doc = Jsoup.parse(inputFile, "UTF-8", "");

        Vacancy vacancy = new Vacancy();
        jobBirdScraperTestHelp.setVacancyTitleHelp(doc, vacancy);
        assertEquals("Applications Engineering - Software Engineering Internship (Fall 2020)", vacancy.getTitle());
    }


    /*
     *  If title cannot be found in the html a HTMLStructureException should be thrown
     */
    @Test
    public void setVacancyTitle_invalidPageStructure() throws Exception {
        String filename = "testfiles/jobbird03_vacancy_notitle.htm";  // should count 5 pages
        ClassLoader classLoader = new JobBirdScraperTest().getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource(filename).getFile());
        Document doc = Jsoup.parse(inputFile, "UTF-8", "");

        Vacancy vacancy = new Vacancy();
        assertThatThrownBy(() ->
                jobBirdScraperTestHelp.setVacancyTitleHelp(doc, vacancy)).isInstanceOf(
                    HTMLStructureException.class);
    }

}
