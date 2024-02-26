package com.zerobase.community.web.exception.fielderror;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CustomFieldError {

  private String field;
  private String message;

  // 필드에러 정보 출력
  public static List<CustomFieldError> getFieldErrors(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream()
        .filter(e -> e.getCode() != null && e.getDefaultMessage() != null)
        .map(e -> CustomFieldError.builder()
            .field(e.getField())
            .message(e.getDefaultMessage())
            .build())
        .toList();
  }

  @Override
  public String toString() {
    return this.message;
  }
}
