package com.banana.AccountsService.exception;

public class UserNotfoundException extends GlobalException {
    protected static final long serialVersionUID = 2L;

    public UserNotfoundException() {
        super("User not found");
    }

    public UserNotfoundException(Long userId) {
        super("User with id: " + userId + " not found");
    }
}
