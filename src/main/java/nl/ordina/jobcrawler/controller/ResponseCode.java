package nl.ordina.jobcrawler.controller;

import lombok.Getter;

@Getter
public class ResponseCode {
    private String ErrorCode;

    public ResponseCode(String errorCode) {
        ErrorCode = errorCode;
    }
}
