package com.kwsni.caught_up.tvdb.batch;

public class InvalidRecordException extends RuntimeException {
    public InvalidRecordException(String message) {
        super(message);
    }
}