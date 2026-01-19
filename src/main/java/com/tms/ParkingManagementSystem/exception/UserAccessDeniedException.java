package com.tms.ParkingManagementSystem.exception;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException(Long userId) {
        super("Access denied to user with id = " + userId);
    }
}