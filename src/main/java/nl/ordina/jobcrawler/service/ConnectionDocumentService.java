package nl.ordina.jobcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
ConnectionDocumentService makes a connection to the specified URL. It returns the html code of a page which can by used for further purposes.
 */

@Component
public class ConnectionDocumentService {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36 ArabotScraper";

    public Document getConnection(final String url) throws IOException {
        return Jsoup.connect(url).userAgent(userAgent).get();
    }
}
