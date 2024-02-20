package com.dsoft.CitizenRegistrationSystem.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        return handleExceptionInternal(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException ex) {
        return handleExceptionInternal(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class,
                               HandlerMethodValidationException.class,
                               ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleArgumentException(RuntimeException ex) {
        return handleExceptionInternal(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return handleExceptionInternal(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder sbMessage = new StringBuilder();
        ex.getFieldErrors().forEach(e -> {
            sbMessage.append("The field ");
            sbMessage.append(e.getField());
            sbMessage.append(" is invalid. Reason: ");
            sbMessage.append(e.getDefaultMessage());
            sbMessage.append("; ");
        });
        String message = sbMessage.substring(0, sbMessage.length() - 2);
        return new ResponseEntity<>(new ErrorResponse(message), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<ErrorResponse> handleExceptionInternal(RuntimeException ex, HttpStatus status) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), new HttpHeaders(), status);
    }
}
