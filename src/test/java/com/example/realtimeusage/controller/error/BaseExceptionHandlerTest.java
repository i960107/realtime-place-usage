package com.example.realtimeusage.controller.error;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.exception.GeneralException;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

@DisplayName("핸들러 - 기본 예외 처리")
class BaseExceptionHandlerTest {
    private BaseExceptionHandler sut;
    private Model model;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        sut = new BaseExceptionHandler();
        model = new ConcurrentModel();
        response = new MockHttpServletResponse();
    }

    @DisplayName("일반 예외")
    @Test
    void thrownGeneralExceptionShouldReturnErrorPage() {
        //given
        ErrorCode errorCode = ErrorCode.NOT_FOUND;
        GeneralException e = new GeneralException(errorCode);

        //when
        String viewName = sut.generalError(e, model);

        //then
        assertThat(viewName).isEqualTo("/error");
        assertThat(model)
                .hasFieldOrPropertyWithValue("statusCode", errorCode.getHttpStatus())
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("message", errorCode.getMessage(e));
    }

    @DisplayName("검증 예외 예외")
    @Test
    void thrownConstraintExceptionShouldReturnErrorPage() {
        //given
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        ConstraintViolationException e = new ConstraintViolationException(Set.of());

        //when
        String viewName = sut.constraintViolationError(e, model);

        //then
        assertThat(viewName).isEqualTo("/error");
        assertThat(model)
                .hasFieldOrPropertyWithValue("statusCode", errorCode.getHttpStatus())
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("message", errorCode.getMessage(e));
    }

    @DisplayName("기타 예외")
    @Test
    void thrownExceptionShouldReturnErrorPage() {
        //given
        response.setStatus(HttpStatus.FORBIDDEN.value());
        RuntimeException e = new RuntimeException("this is test");

        //when
        String viewName = sut.error(e, response, model);

        //then
        assertThat(viewName).isEqualTo("/error");
        assertThat(model)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.FORBIDDEN.value())
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("message", ErrorCode.BAD_REQUEST.getMessage(e));
    }
}
