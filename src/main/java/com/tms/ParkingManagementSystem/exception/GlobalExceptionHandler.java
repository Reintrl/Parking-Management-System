package com.tms.ParkingManagementSystem.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 NOT FOUND

    @ExceptionHandler({
            UserNotFoundException.class,
            VehicleNotFoundException.class,
            UserVehicleNotFoundException.class,
            TariffNotFoundException.class,
            ParkingLotNotFoundException.class,
            SpotNotFoundException.class,
            ReservationNotFoundException.class,
            ParkingSessionNotFoundException.class,
            SecurityNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        log.warn("NOT_FOUND: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        ));
    }

    //409 CONFLICT

    @ExceptionHandler({
            TariffNameAlreadyExistsException.class,
            EmailAlreadyExistsException.class,
            AddressAlreadyExistsException.class,
            PlateNumberAlreadyExistsException.class,
            SpotNumberAlreadyExistsException.class,
            TariffInUseException.class,
            ParkingSessionConflictException.class,
            ReservationInUseException.class,
            SpotInUseException.class,
            VehicleInUseException.class,
            ParkingLotInUseException.class,
            UserInUseException.class,
            ReservationConflictException.class,
            UsernameAlreadyExistsException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        log.warn("CONFLICT: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "CONFLICT",
                "message", ex.getMessage()
        ));
    }

    // 400 BAD REQUEST

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }

        log.warn("VALIDATION_ERROR: {}", fields);

        return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "fields", fields
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("BAD_REQUEST: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "BAD_REQUEST",
                "message", ex.getMessage()
        ));
    }

    //500 INTERNAL SERVER ERROR

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("UNEXPECTED_ERROR", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "message", "Unexpected error occurred"
        ));
    }

    //403 FORBIDDEN
    @ExceptionHandler({
            SessionAccessDeniedException.class,
            UserNotActiveException.class,
            ReservationAccessDeniedException.class,
            VehicleAccessDeniedException.class
    })
    public ResponseEntity<?> handleForbidden(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "SESSION_ACCESS_DENIED",
                        "message", ex.getMessage()
                ));
    }
}
