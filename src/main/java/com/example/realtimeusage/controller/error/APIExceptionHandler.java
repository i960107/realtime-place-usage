package com.example.realtimeusage.controller.error;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.dto.APIErrorResponse;
import com.example.realtimeusage.exception.GeneralException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = RestController.class)
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> error(GeneralException e,  WebRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = errorCode.isClientSideError() ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;

        return super.handleExceptionInternal(e,
                APIErrorResponse.of(errorCode.getCode(),
                        errorCode.getMessage(e.getMessage())),
                HttpHeaders.EMPTY,
                status,
                request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> error(Exception e, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return super.handleExceptionInternal(e,
                APIErrorResponse.of(errorCode.getCode(),
                        errorCode.getMessage(e.getMessage())),
                HttpHeaders.EMPTY,
                status,
                request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        ErrorCode errorCode =
                status.is4xxClientError() ?
                        ErrorCode.SPRING_BAD_REQUEST :
                        ErrorCode.SPRING_INTERNAL_ERROR;
        return super.handleExceptionInternal(ex,
                APIErrorResponse.of(errorCode.getCode(), errorCode.getMessage(ex.getMessage())), headers, status,
                request);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> validationError(Exception ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return super.handleExceptionInternal(ex,
                APIErrorResponse.of(errorCode, errorCode.getMessage(ex)),
                HttpHeaders.EMPTY,
                status,
                request);
    }
}
