package com.cruise.project_cruise.exception;

public class TransferLackOfBalanceException extends Exception {
    public TransferLackOfBalanceException () {}
    public TransferLackOfBalanceException (String errorMsg) {
        super(errorMsg);
    }
}
