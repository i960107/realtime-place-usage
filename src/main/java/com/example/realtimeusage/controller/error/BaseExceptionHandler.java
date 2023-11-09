package com.example.realtimeusage.controller.error;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.exception.GeneralException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(GeneralException.class)
    public String generalError(GeneralException e, Model model) {
        ErrorCode errorCode = e.getErrorCode();

        model.addAttribute("statusCode", errorCode.getHttpStatus().value());
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("message", errorCode.getMessage(e));
        return "/error";
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public String constraintViolationError(Exception ex, Model model) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        model.addAttribute("statusCode", errorCode.getHttpStatus().value());
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("message", errorCode.getMessage(ex));
        return "/error";
    }

    @ExceptionHandler
    public String error(Exception e, HttpServletResponse response, Model model) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        ErrorCode errorCode = ErrorCode.valueOf(httpStatus);

        model.addAttribute("statusCode", httpStatus.value());
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("message", errorCode.getMessage(e));
        return "/error";
    }

}
