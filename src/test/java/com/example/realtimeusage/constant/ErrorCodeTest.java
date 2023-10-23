package com.example.realtimeusage.constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ErrorCodeTest {
    @DisplayName("예외를 받으면 예외 메시지가 포함된 메시지를 출력한다.")
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getErrorCodeValues")// test하려는 함수와 같은 이름으로 static함수 선언
    void gettingErrorCodeMessageWithGivenExceptionShouldReturnMessage(ErrorCode errorCode) {
        //given
        String message = "This is test exception";
        Exception e = new Exception(message);

        //when
        String result = errorCode.getMessage(e);

        //then
        assertThat(result)
                .isEqualTo(errorCode.getMessage() + " - " + message);
    }

    private static Stream<Arguments> getErrorCodeValues() {
        return Arrays.stream(ErrorCode.values()).map(Arguments::arguments);
    }

    @DisplayName("메시지를 받으면 해당 에러 메시지를 출력한다.")
    @ParameterizedTest(name = "[{index}] \"{0}\" -> {1}")
    @MethodSource// test하려는 함수와 같은 이름으로 static함수 선언
    void gettingErrorCodeMessageWithGivenMessageShouldReturnMessage(String input, String expected) {
        //given
        //when
        String result = ErrorCode.INTERNAL_ERROR.getMessage(input);

        //then
        assertThat(result)
                .isEqualTo(expected);
    }

    private static Stream<Arguments> gettingErrorCodeMessageWithGivenMessageShouldReturnMessage(){
        return Stream.of(
                arguments(null, ErrorCode.INTERNAL_ERROR.getMessage()),
                arguments("", ErrorCode.INTERNAL_ERROR.getMessage()),
                arguments(" ", ErrorCode.INTERNAL_ERROR.getMessage()),
                arguments("This is test message", "This is test message")
                );
    }

    @DisplayName("toString()을 호출하면 에러 코드 정보를 출력한다.")
    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @MethodSource("getErrorCodeValues")// test하려는 함수와 같은 이름으로 static함수 선언
    void invokeToStringReturnErrorCodeDetail(ErrorCode errorCode) {
        //when
        String result = errorCode.toString();

        //then
        assertThat(result)
                .contains(errorCode.getCode().toString(), errorCode.getMessage(), errorCode.name());
    }
}
