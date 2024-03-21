package com.zerobase.community.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.zerobase.community.IntegrationTestSupport;
import com.zerobase.community.domain.member.Grade;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.member.exception.MemberException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryTest extends IntegrationTestSupport {

  @Autowired
  MemberRepository memberRepository;

  @AfterEach
  void tearDown() {
    memberRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("로그인 아이디로 회원 조회")
  void findByLoginId() {
    // given
    Member member = createMember();

    // when
    Member target = memberRepository.findByLoginId(member.getLoginId())
        .orElseThrow(() -> new MemberException("찾는 맴버가 없습니다."));

    // then
    assertThat(target).isNotNull()
        .extracting("loginId", "password", "nickName", "email", "grade")
        .containsExactlyInAnyOrder(
            target.getLoginId(),
            target.getPassword(),
            target.getNickName(),
            target.getEmail(),
            target.getGrade()
        );
  }

  @Test
  @DisplayName("아이디 존재여부 체크")
  void existsByLoginId() {
    // given
    Member member = createMember();

    // when
    boolean existsByLoginId = memberRepository.existsByLoginId(member.getLoginId());
    boolean notExistsByLoginId = memberRepository.existsByLoginId("notExists");

    // then
    assertThat(existsByLoginId).isTrue();
    assertThat(notExistsByLoginId).isFalse();
  }

  @Test
  @DisplayName("닉네임 존재여부 체크")
  void existsByNickName() {
    // given
    Member member = createMember();

    // when
    boolean ExistsByNickName = memberRepository.existsByNickName(member.getNickName());
    boolean notExistsByNickName = memberRepository.existsByNickName("notExists");

    // then
    assertThat(ExistsByNickName).isTrue();
    assertThat(notExistsByNickName).isFalse();
  }

  // 맴버 생성 메서드
  private Member createMember() {
    Member member = Member.builder()
        .loginId("member-loginId")
        .password("member-password")
        .nickName("member-nickName")
        .email("member@email.com")
        .grade(Grade.USER)
        .build();
    return memberRepository.save(member);
  }

}