package com.example.realtimeusage.controller.error;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.dto.APIErrorResponse;
import com.example.realtimeusage.exception.GeneralException;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = {RestController.class})
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> generalError(GeneralException ex, WebRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        return handleExceptionInternal(ex, errorCode, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> error(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ErrorCode.INTERNAL_ERROR, request);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> validationError(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ErrorCode.VALIDATION_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        // TODO: 2023/11/09 stack overflow에러 발생하는데.. 
        return handleExceptionInternal(ex, ErrorCode.valueOf(status), headers, status, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorCode errorCode, WebRequest webRequest) {
        return handleExceptionInternal(e, errorCode, HttpHeaders.EMPTY, errorCode.getHttpStatus(), webRequest);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorCode errorCode, HttpHeaders headers,
                                                           HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(
                e,
                APIErrorResponse.of(errorCode, e),
                headers,
                status,
                request);
    }
}
