package nl.ordina.jobcrawler.service.exception;

public class DuplicateRecordFoundException extends RuntimeException {
    public DuplicateRecordFoundException(String message) {
        super(message);
    }
}
