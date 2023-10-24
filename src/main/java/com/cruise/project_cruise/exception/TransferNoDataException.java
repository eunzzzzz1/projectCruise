package com.cruise.project_cruise.exception;

public class TransferNoDataException extends Exception {
    public TransferNoDataException () {}
    public TransferNoDataException (String errorMsg) {
        super(errorMsg);
    }
}
