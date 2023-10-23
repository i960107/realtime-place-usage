package com.example.realtimeusage.controller.error;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.exception.GeneralException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(GeneralException.class)
    public String error(GeneralException e, Model model, HttpServletResponse response) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = errorCode.isClientSideError() ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;

        model.addAttribute("statusCode", status.value());
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("message", errorCode.getMessage(e));
        return "error";
    }

    @ExceptionHandler
    public String error(Exception e, Model model) {
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        model.addAttribute("statusCode", status.value());
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("message", errorCode.getMessage(e));
        return "error";
    }

}
