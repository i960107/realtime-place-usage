package com.example.realtimeusage.controller.error;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.dto.APIErrorResponse;
import com.example.realtimeusage.exception.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice(annotations = RestController.class)
public class APIExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<APIErrorResponse> error(GeneralException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = errorCode.isClientSideError() ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(status)
                .body(APIErrorResponse.of(errorCode, errorCode.getMessage(status.getReasonPhrase())));
    }

    @ExceptionHandler
    public ResponseEntity<APIErrorResponse> error(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(status)
                .body(APIErrorResponse.of(errorCode, errorCode.getMessage(status.getReasonPhrase())));
    }
}
