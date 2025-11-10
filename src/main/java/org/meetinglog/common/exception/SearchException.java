package org.meetinglog.common.exception;

public class SearchException extends BusinessException {
    
    public SearchException(String message) {
        super(message, "SEARCH_ERROR");
    }
    
    public SearchException(String message, Throwable cause) {
        super(message, "SEARCH_ERROR");
        this.initCause(cause);
    }
}