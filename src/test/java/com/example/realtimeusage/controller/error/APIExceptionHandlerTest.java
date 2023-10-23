package com.example.realtimeusage.controller.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.dto.APIErrorResponse;
import com.example.realtimeusage.exception.GeneralException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

class APIExceptionHandlerTest {
    private APIExceptionHandler sut;
    private WebRequest webRequest;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        sut = new APIExceptionHandler();
        request = new MockHttpServletRequest();
        webRequest = new DispatcherServletWebRequest(request);
    }

    @DisplayName("검증 오류 -응답 데이터")
    @Test
    void thrownConstraintViolationExceptionShouldReturnResponseEntityOfApiErrorResponse() {
        //given
        ConstraintViolationException e = new ConstraintViolationException(Set.of());

        //when
        ResponseEntity<Object> response = sut.validationError(e, webRequest);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("body",
                        APIErrorResponse.of(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getMessage(e)));
    }

    @DisplayName("프로젝트 일반 오류 -응답 데이터")
    @Test
    void thrownGeneralExceptionShouldReturnResponseEntityOfApiErrorResponse() {
        //given
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        GeneralException e = new GeneralException(errorCode);

        //when
        ResponseEntity<Object> response = sut.generalError(e, webRequest);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.INTERNAL_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("body",
                        APIErrorResponse.of(errorCode, errorCode.getMessage(e)));
    }

    @DisplayName("기타(전체)오류 -응답 데이터")
    @Test
    void thrownExceptionShouldReturnResponseEntityOfApiErrorResponse() {
        //given
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        Exception e = new Exception();

        //when
        ResponseEntity<Object> response = sut.error(e, webRequest);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.INTERNAL_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("body",
                        APIErrorResponse.of(errorCode, errorCode.getMessage(e)));
    }
}