package com.zerobase.community.web.exception;

import com.zerobase.community.web.exception.response.ExceptionResponse;
import com.zerobase.community.web.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

  // Member Exception
  @ExceptionHandler(MemberException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse errorResponse(MemberException e) {
    log.error("[Member Exception] ", e);
    return new ExceptionResponse(e.getMessage());
  }

}
