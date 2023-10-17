package com.example.realtimeusage.controller.error;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.dto.APIErrorResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseErrorController implements ErrorController {
    @GetMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public String error(Model model, HttpServletResponse response) {
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        ErrorCode errorCode = status.is4xxClientError() ? ErrorCode.BAD_REQUEST : ErrorCode.INTERNAL_ERROR;

        model.addAttribute("statusCode", status.value());
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("message", errorCode.getMessage(status.getReasonPhrase()));
        return "error";
    }

    @GetMapping("/error")
    public ResponseEntity<APIErrorResponse> error(HttpServletResponse response) {
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        ErrorCode errorCode = status.is4xxClientError() ? ErrorCode.BAD_REQUEST : ErrorCode.INTERNAL_ERROR;

        return ResponseEntity
                .status(status)
                .body(APIErrorResponse.of(errorCode, errorCode.getMessage(status.getReasonPhrase())));
    }
}
