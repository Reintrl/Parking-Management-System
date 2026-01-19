package com.tms.ParkingManagementSystem.exception;

public class UserNotActiveException extends RuntimeException {

    public UserNotActiveException(Long userId) {
        super("User with id = " + userId + " is not ACTIVE");
    }

    public UserNotActiveException(String username) {
        super("User with username = " + username + " is not ACTIVE");
    }
}