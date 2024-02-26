package com.zerobase.community.web.member.controller;

import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.service.MemberService;
import com.zerobase.community.web.exception.fielderror.CustomFieldError;
import com.zerobase.community.web.member.dto.request.MemberEditRequestDto;
import com.zerobase.community.web.member.dto.request.MemberJoinRequestDto;
import com.zerobase.community.web.member.dto.request.MemberLoginRequestDto;
import com.zerobase.community.web.member.dto.request.MemberPasswordRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  // 회원가입 API
  @PostMapping("/join")
  public ResponseEntity<?> joinMember(@Validated @RequestBody MemberJoinRequestDto request,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      List<CustomFieldError> fieldErrors = CustomFieldError.getFieldErrors(bindingResult);
      log.error("회원가입 API 필드 오류 메시지 = {}", fieldErrors);
      return new ResponseEntity<>("다시 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    Member member = memberService.join(request);

    return new ResponseEntity<>(member, HttpStatus.OK);
  }

  // 로그인 API
  @PostMapping("/login")
  public ResponseEntity<?> loginMember(@Validated @RequestBody MemberLoginRequestDto dto,
      BindingResult bindingResult,
      HttpServletRequest request) {

    if (bindingResult.hasErrors()) {
      List<CustomFieldError> fieldErrors = CustomFieldError.getFieldErrors(bindingResult);
      log.error("로그인 API 오류 = {}", fieldErrors);
      return new ResponseEntity<>("다시 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    // 1. 아이디 비밀번호 확인
    Member loginMember = memberService.login(dto);

    // 2. 세션에 로그인 유저 정보담기
    HttpSession session = request.getSession();
    session.setAttribute("loginMember", loginMember);

    return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
  }

  // 로그아웃 API
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    if (session != null) {
      session.invalidate();
    } else {
      return new ResponseEntity<>("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>("로그아웃 성공", HttpStatus.OK);
  }

  // 회원정보 수정 API
  @PutMapping("/edit")
  public ResponseEntity<?> editMember(@Validated @RequestBody MemberEditRequestDto dto,
      BindingResult bindingResult,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    if (loginMember == null) {
      log.error("사용자 정보를 알 수 없습니다.");
      return new ResponseEntity<>("다시 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    if (bindingResult.hasErrors()) {
      List<CustomFieldError> fieldErrors = CustomFieldError.getFieldErrors(bindingResult);
      log.error("회원정보 수정 API 오류 = {}", fieldErrors);
      return new ResponseEntity<>("다시 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    memberService.editMember(dto, loginMember);

    return new ResponseEntity<>("회원정보 수정", HttpStatus.OK);
  }

  // 비밀번호 수정 API
  @PutMapping("/edit/password")
  public ResponseEntity<?> editPassword(@Validated @RequestBody MemberPasswordRequestDto dto,
      BindingResult bindingResult,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    if (loginMember == null) {
      log.error("사용자 정보를 알 수 없습니다.");
      return new ResponseEntity<>("다시 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    if (bindingResult.hasErrors()) {
      List<CustomFieldError> fieldErrors = CustomFieldError.getFieldErrors(bindingResult);
      log.error("회원정보 수정 API 오류 = {}", fieldErrors);
      return new ResponseEntity<>("다시 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    memberService.editPassword(dto, loginMember);

    return new ResponseEntity<>("비밀번호 수정", HttpStatus.OK);
  }

  // 회원탈퇴 API
  @DeleteMapping("/delete/{memberId}")
  public ResponseEntity<?> deleteMember(
      @Validated @PathVariable("memberId") @Positive Long memberId,
      BindingResult bindingResult,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    if (loginMember == null) {
      log.error("사용자 정보를 알 수 없습니다.");
      return new ResponseEntity<>("다시 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    if (bindingResult.hasErrors()) {
      List<CustomFieldError> fieldErrors = CustomFieldError.getFieldErrors(bindingResult);
      log.error("회원탈퇴 API 오류 = {}", fieldErrors);
    }

    memberService.deleteMember(memberId);

    return new ResponseEntity<>("회원탈퇴", HttpStatus.OK);
  }
}
