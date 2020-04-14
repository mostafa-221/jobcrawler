package nl.ordina.jobcrawler;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SearchRequest {

    String location;
    String distance;
    String keywords;

}
