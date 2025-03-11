package com.library.exception;

public class MaxBooksReachedException extends Exception {
    public MaxBooksReachedException(String message) {
        super(message);
    }
}