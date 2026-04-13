package com.m347.pollit;

import com.m347.pollit.exceptions.CommonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleCommonException(CommonException e) {
        Map<String, String> errorMap = Map.of(
                "message", e.getMessage()
        );
        return new ResponseEntity<>(errorMap, Objects.requireNonNullElse(e.status, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        FieldError error = ex.getBindingResult().getFieldErrors().get(0);
        errors.put("message", error.getDefaultMessage());
        return ResponseEntity.badRequest().body(errors);
    }

}
