package org.meetinglog.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends javax.naming.AuthenticationException {

    private final String errorCode;

    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTHENTICATION_ERROR";
    }

    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}