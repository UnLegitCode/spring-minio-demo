package ru.unlegit.miniodemo.controller;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.unlegit.miniodemo.dto.ErrorInfo;

import java.io.FileNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<ErrorInfo> handleMinioException(MinioException exception) {
        var errorInfo = new ErrorInfo(exception.getMessage());

        if (exception instanceof ErrorResponseException errorResponseException) {
            if (errorResponseException.errorResponse().code().equals("NoSuchKey")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorInfo);
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorInfo);
    }
}