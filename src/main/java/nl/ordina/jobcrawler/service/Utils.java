package nl.ordina.jobcrawler.service;

// Class with utilities we might need more often

public class Utils {
    public static String upperCaseFirstChar(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
