package com.library.exception;

public class InactiveAccountException extends Exception {
    public InactiveAccountException(String message) {
        super(message);
    }
}