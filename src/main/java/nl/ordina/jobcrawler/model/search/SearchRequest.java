package nl.ordina.jobcrawler.model.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// on the server jackson gives an error that this class does not have a default constructor, which cannot exist with a final variable
//@Value  // makes getters and makes attributes private final
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SearchRequest {

    private String location;
    private String distance;
    private String keywords;

}
