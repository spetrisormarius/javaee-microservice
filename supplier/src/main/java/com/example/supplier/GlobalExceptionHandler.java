package com.example.supplier;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Throwable.class)
    public Map<String, String> defaultErrorHandler(HttpServletRequest request, Throwable e) {
        return getErrorResponse(e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgument(HttpServletRequest request, IllegalArgumentException e) {
        return getErrorResponse(e);
    }

    private Map<String, String> getErrorResponse(Throwable e) {
        System.out.println(e.getMessage());
        Map<String, String> errorPayload = new HashMap<>();
        errorPayload.put("exception", e.getMessage());
        return errorPayload;
    }
}
