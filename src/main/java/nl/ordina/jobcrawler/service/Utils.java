package nl.ordina.jobcrawler.service;

// Class with utilities we might need more often

public class Utils {
    public static String upperCaseFirstChar(String string) {
        // Only convert string when string is not null and contains more than 1 char.
        // Used || instead of | to prevent nullpointerexception being thrown by .length() if string is null
        if(string == null || string.length() < 2)
            return string;

        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
