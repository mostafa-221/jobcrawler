package nl.ordina.jobcrawler;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value  // makes getters and makes attributes private final
@AllArgsConstructor
public class SearchRequest {

    String location;
    String distance;
    String keywords;

}
