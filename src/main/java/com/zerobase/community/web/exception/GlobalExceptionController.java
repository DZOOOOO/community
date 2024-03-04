package com.zerobase.community.web.exception;

import com.zerobase.community.web.exception.fielderror.CustomFieldError;
import com.zerobase.community.web.exception.response.ExceptionResponse;
import com.zerobase.community.web.member.exception.MemberException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

  // Member Exception
  @ExceptionHandler(MemberException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse memberErrorResponse(MemberException e) {
    log.error("[Member Exception] ", e);
    return new ExceptionResponse(e.getMessage());
  }

  // Field Exception
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse fieldErrorResponse(MethodArgumentNotValidException e) {
    List<CustomFieldError> fieldErrors = CustomFieldError.getFieldErrors(e.getBindingResult());
    log.error("[Field Exception] = {}", fieldErrors);
    return new ExceptionResponse(fieldErrors.toString());
  }

}
