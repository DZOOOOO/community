package com.zerobase.community.web.exception;

import com.zerobase.community.web.board.exception.BoardException;
import com.zerobase.community.web.comment.exception.CommentException;
import com.zerobase.community.web.exception.fielderror.CustomFieldError;
import com.zerobase.community.web.exception.response.ExceptionResponse;
import com.zerobase.community.web.interceptor.InterceptorException;
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

  // Board Exception
  @ExceptionHandler(BoardException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse boardErrorResponse(BoardException e) {
    log.error("[Board Exception] ", e);
    return new ExceptionResponse(e.getMessage());
  }

  // Comment Exception
  @ExceptionHandler(CommentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse commentErrorResponse(CommentException e) {
    log.error("[Comment Exception] ", e);
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

  @ExceptionHandler(InterceptorException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse InterceptorExceptionResponse(InterceptorException e) {
    log.error("[Interceptor Exception] ", e);
    return new ExceptionResponse(e.getMessage());
  }

}
