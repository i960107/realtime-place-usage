package com.example.realtimeusage.controller.error;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.dto.APIErrorResponse;
import com.example.realtimeusage.exception.GeneralException;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = {RestController.class, Service.class})
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> generalError(GeneralException ex, WebRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode.isClientSideError() ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;

        return getObjectResponseEntity(ex, request, errorCode, status, HttpHeaders.EMPTY);
    }

    @ExceptionHandler
    public ResponseEntity<Object> error(Exception ex, WebRequest request) {
        return getObjectResponseEntity(
                ex,
                request,
                ErrorCode.INTERNAL_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpHeaders.EMPTY);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        ErrorCode errorCode =
                status.is4xxClientError() ?
                        ErrorCode.SPRING_BAD_REQUEST :
                        ErrorCode.SPRING_INTERNAL_ERROR;
        return getObjectResponseEntity(ex, request, errorCode, status, headers);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> validationError(Exception ex, WebRequest request) {
        return getObjectResponseEntity(
                ex,
                request,
                ErrorCode.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST,
                HttpHeaders.EMPTY);
    }

    private ResponseEntity<Object> getObjectResponseEntity(Exception e,
                                                           WebRequest request,
                                                           ErrorCode errorCode,
                                                           HttpStatus status,
                                                           HttpHeaders headers) {
        return super.handleExceptionInternal(e,
                APIErrorResponse.of(errorCode.getCode(), errorCode.getMessage(e)),
                headers,
                status,
                request);
    }

}
