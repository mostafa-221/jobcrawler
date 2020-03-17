package nl.ordina.jobcrawler;

public class SearchRequest {

    private final String location;
    private final String distance;
    private final String keywords;

    public SearchRequest(String location, String distance, String keywords) {
        this.location = location;
        this.distance = distance;
        this.keywords = keywords;
    }

    public String getLocation() {return this.location;}
    public String getDistance() {return this.distance;}
    public String getKeywords() {return this.keywords;}

}
