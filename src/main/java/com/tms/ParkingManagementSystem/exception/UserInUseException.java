package com.tms.ParkingManagementSystem.exception;

public class UserInUseException extends RuntimeException {

    public UserInUseException(Long userId, String reason) {
        super("User with id = " + userId + " is in use and cannot be deleted"
                + (reason == null || reason.isBlank() ? "" : ": " + reason));
    }
}