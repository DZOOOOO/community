package com.zerobase.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zerobase.community.IntegrationTestSupport;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import com.zerobase.community.web.member.dto.request.MemberEditRequestDto;
import com.zerobase.community.web.member.dto.request.MemberJoinRequestDto;
import com.zerobase.community.web.member.dto.request.MemberLoginRequestDto;
import com.zerobase.community.web.member.dto.request.MemberPasswordRequestDto;
import com.zerobase.community.web.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class MemberServiceTest extends IntegrationTestSupport {

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  @AfterEach
  void tearDown() {
    memberRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("회원가입 메서드 테스트 - 정상 성공")
  void join_success() {
    // given
    MemberJoinRequestDto request = MemberJoinRequestDto.builder()
        .loginId("test")
        .password("test")
        .nickName("test")
        .email("test@test.com")
        .build();

    // when
    Member member = memberService.join(request);

    // then
    assertThat(member).extracting("loginId", "password", "nickName", "email")
        .containsExactlyInAnyOrder("test", "test", "test", "test@test.com");
  }

  @Test
  @DisplayName("로그인 아이디가 중복되는 경우 - 실패")
  void join_fail_loginIdDup() {
    // given
    createMember();
    MemberJoinRequestDto request = MemberJoinRequestDto.builder()
        .loginId("test")
        .password("test1234")
        .nickName("test1234")
        .email("test1234@test.com")
        .build();

    // when
    // then
    assertThatThrownBy(() -> memberService.join(request))
        .isInstanceOf(MemberException.class)
        .hasMessage("이미 존재하는 아이디입니다.");
  }

  @Test
  @DisplayName("닉네임이 중복되는 경우 - 실패")
  void join_fail_nickNameDup() {
    // given
    createMember();
    MemberJoinRequestDto request = MemberJoinRequestDto.builder()
        .loginId("test1234")
        .password("test1234")
        .nickName("test")
        .email("test1234@test.com")
        .build();

    // when
    // then
    assertThatThrownBy(() -> memberService.join(request))
        .isInstanceOf(MemberException.class)
        .hasMessage("이미 존재하는 닉네임 입니다.");
  }

  @Test
  @DisplayName("로그인 - 정상 성공")
  void login_success() {
    // given
    createMember();
    MemberLoginRequestDto request = MemberLoginRequestDto.builder()
        .loginId("test")
        .password("test")
        .build();

    // when
    Member loginMember = memberService.login(request);

    // then
    assertThat(loginMember).extracting("loginId", "password")
        .containsExactlyInAnyOrder("test", "test");
  }

  @Test
  @DisplayName("로그인 - 아이디 실패")
  void login_id_fail() {
    // given
    createMember();
    MemberLoginRequestDto request = MemberLoginRequestDto.builder()
        .loginId("test123")
        .password("test")
        .build();

    // when
    // then
    assertThatThrownBy(() -> memberService.login(request))
        .isInstanceOf(MemberException.class)
        .hasMessage("아이디, 비밀번호를 다시 확인해주세요.");
  }

  @Test
  @DisplayName("로그인 - 아이디, 비밀번호 일치 실패")
  void login_password_fail() {
// given
    createMember();
    MemberLoginRequestDto request = MemberLoginRequestDto.builder()
        .loginId("test")
        .password("test123")
        .build();

    // when
    // then
    assertThatThrownBy(() -> memberService.login(request))
        .isInstanceOf(MemberException.class)
        .hasMessage("아이디, 비밀번호를 다시 확인해주세요.");
  }

  @Test
  @DisplayName("로그인 - 아이디, 비밀번호 둘다 실패")
  void login_id_password_fail() {
    // given
    createMember();
    MemberLoginRequestDto request = MemberLoginRequestDto.builder()
        .loginId("ewfewfwe")
        .password("gregreger")
        .build();

    // when
    // then
    assertThatThrownBy(() -> memberService.login(request))
        .isInstanceOf(MemberException.class)
        .hasMessage("아이디, 비밀번호를 다시 확인해주세요.");
  }

  @Test
  @DisplayName("회원정보 수정 - 정상 성공")
  void edit_success() {
    // given
    Member loginMember = createMember();
    MemberEditRequestDto request = MemberEditRequestDto.builder()
        .email("edit@test.com")
        .nickName("editNickName")
        .build();

    // when
    memberService.editMember(request, loginMember);
    Member target = memberService.findById(loginMember.getId());

    // then
    assertThat(target).extracting("email", "nickName")
        .containsExactlyInAnyOrder("edit@test.com", "editNickName");
  }

  @Test
  @DisplayName("비밀번호 수정 - 정상 성공")
  void edit_password_success() {
    // given
    Member loginMember = createMember();
    MemberPasswordRequestDto request = MemberPasswordRequestDto.builder()
        .password("edit-test")
        .build();

    // when
    memberService.editPassword(request, loginMember);
    Member targetMember = memberService.findById(loginMember.getId());

    // then
    assertThat(targetMember).extracting("password")
        .isEqualTo("edit-test");
  }

  @Test
  @DisplayName("회원탈퇴 - 정상 성공")
  void delete_member_success() {
    // given
    Member member = createMember();

    // when
    memberService.deleteMember(member.getId());

    // then
    assertThatThrownBy(() -> memberService.findById(member.getId()))
        .isInstanceOf(MemberException.class);
  }

  private Member createMember() {
    return memberRepository.save(Member.builder()
        .loginId("test")
        .password("test")
        .nickName("test")
        .email("test")
        .build());
  }


}