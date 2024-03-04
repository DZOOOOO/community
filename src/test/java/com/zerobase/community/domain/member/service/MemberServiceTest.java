package com.zerobase.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zerobase.community.domain.member.Grade;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import com.zerobase.community.web.member.dto.request.MemberEditRequestDto;
import com.zerobase.community.web.member.dto.request.MemberJoinRequestDto;
import com.zerobase.community.web.member.dto.request.MemberLoginRequestDto;
import com.zerobase.community.web.member.dto.request.MemberPasswordRequestDto;
import com.zerobase.community.web.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MemberServiceTest {

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  // dto 생성 클래스
  static class Dto {

    public static MemberJoinRequestDto createDto(
        String loginId,
        String password,
        String nickName,
        String email) {
      return MemberJoinRequestDto.builder()
          .loginId(loginId)
          .password(password)
          .nickName(nickName)
          .email(email)
          .build();
    }

    public static MemberLoginRequestDto loginRequestDto(
        String loginId,
        String password) {
      return MemberLoginRequestDto.builder()
          .loginId(loginId)
          .password(password)
          .build();
    }

    public static MemberEditRequestDto editRequestDto(
        String nickName,
        String email) {
      return MemberEditRequestDto.builder()
          .nickName(nickName)
          .email(email)
          .build();
    }

    public static MemberPasswordRequestDto passwordRequestDto(
        String password
    ) {
      return MemberPasswordRequestDto.builder()
          .password(password)
          .build();
    }
  }

  @BeforeEach
  void init() {
    memberRepository.save(Member.builder()
        .loginId("test")
        .password("test")
        .nickName("test")
        .email("test")
        .build());
  }

  @AfterEach
  void destroy() {
    memberRepository.deleteAll();
  }

  @Test
  @DisplayName("회원가입 메서드 테스트 - 정상 성공")
  void join_success() {
    Member joinMember = memberService.join(Dto.createDto(
        "user",
        "test",
        "user",
        "test"));

    assertThat(joinMember).isNotNull();
    assertThat(joinMember.getLoginId()).isEqualTo("user");
    assertThat(joinMember.getPassword()).isEqualTo("test");
    assertThat(joinMember.getNickName()).isEqualTo("user");
    assertThat(joinMember.getEmail()).isEqualTo("test");
    assertThat(joinMember.getGrade()).isEqualTo(Grade.USER);
  }

  @Test
  @DisplayName("로그인 아이디가 중복되는 경우 - 실패")
  void join_fail_loginIdDup() {
    assertThatThrownBy(() -> memberService.join(Dto.createDto(
        "test",
        "test",
        "nickName",
        "test")))
        .isInstanceOf(MemberException.class);
  }

  @Test
  @DisplayName("닉네임이 중복되는 경우 - 실패")
  void join_fail_nickNameDup() {
    assertThatThrownBy(() -> memberService.join(Dto.createDto(
        "loginId",
        "test",
        "test",
        "test")))
        .isInstanceOf(MemberException.class);
  }

  @Test
  @DisplayName("로그인 - 정상 성공")
  void login_success() {
    Member member = memberService.login(Dto.loginRequestDto("test", "test"));
    assertThat(member.getLoginId()).isEqualTo("test");
    assertThat(member.getPassword()).isEqualTo("test");
  }

  @Test
  @DisplayName("로그인 - 아이디 실패")
  void login_id_fail() {
    assertThatThrownBy(() -> memberService.login(Dto.loginRequestDto(
        "user",
        "test")))
        .isInstanceOf(MemberException.class);
  }

  @Test
  @DisplayName("로그인 - 아이디, 비밀번호 일치 실패")
  void login_password_fail() {
    assertThatThrownBy(() -> memberService.login(Dto.loginRequestDto(
        "test",
        "password")))
        .isInstanceOf(MemberException.class);
  }

  @Test
  @DisplayName("로그인 - 아이디, 비밀번호 둘다 실패")
  void login_id_password_fail() {
    assertThatThrownBy(() -> memberService.login(Dto.loginRequestDto(
        "user",
        "password")))
        .isInstanceOf(MemberException.class);
  }

  @Test
  @DisplayName("회원정보 수정 - 정상 성공")
  void edit_success() {
    // given
    MemberJoinRequestDto joinDto = MemberJoinRequestDto.builder()
        .loginId("pepsi")
        .password("test")
        .nickName("cola")
        .email("pepsi@test.com")
        .build();
    Member member = memberService.join(joinDto);

    MemberEditRequestDto dto = Dto.editRequestDto("coca-cola", "coca@email.com");
    Member findMember = memberService.findById(member.getId());

    // when
    memberService.editMember(dto, findMember);

    // then
    Member editMember = memberService.findById(member.getId());
    assertThat(editMember.getNickName()).isEqualTo("coca-cola");
    assertThat(editMember.getEmail()).isEqualTo("coca@email.com");
  }

  @Test
  @DisplayName("비밀번호 수정 - 정상 성공")
  void edit_password_success() {
    // given
    MemberPasswordRequestDto dto = Dto.passwordRequestDto("edit-password");
    Member findMember = memberRepository.findByLoginId("test").get();

    // when
    memberService.editPassword(dto, findMember);

    // then
    Member editMember = memberRepository.findByLoginId("test").get();
    assertThat(editMember.getPassword()).isEqualTo("edit-password");
  }

  @Test
  @DisplayName("회원탈퇴 - 정상 성공")
  void delete_member_success() {
    // given
    MemberJoinRequestDto dto = MemberJoinRequestDto.builder()
        .loginId("pepsi")
        .password("test")
        .nickName("cola")
        .email("pepsi@test.com")
        .build();
    Member member = memberService.join(dto);

    // when
    memberService.deleteMember(member.getId());

    // then
    assertThatThrownBy(() -> memberService.findById(member.getId()))
        .isInstanceOf(MemberException.class);
  }

}