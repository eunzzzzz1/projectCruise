package com.cruise.project_cruise.exception;

public class NoDataException extends Exception {
    public NoDataException() {}
    public NoDataException(String errorMsg) {
        super(errorMsg);
    }
}
