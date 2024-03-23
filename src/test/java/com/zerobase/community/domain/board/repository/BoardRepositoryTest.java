package com.zerobase.community.domain.board.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.zerobase.community.IntegrationTestSupport;
import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.member.Grade;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class BoardRepositoryTest extends IntegrationTestSupport {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  BoardRepository boardRepository;

  @AfterEach
  void tearDown() {
    boardRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("작성자 아이디로 게시글 찾기")
  void findAllByMember() {
    // given
    Member member = createMember();
    Board board1 = Board.builder()
        .title("test-title1")
        .content("test-content1")
        .member(member)
        .build();
    Board board2 = Board.builder()
        .title("test-title2")
        .content("test-content2")
        .member(member)
        .build();
    boardRepository.save(board1);
    boardRepository.save(board2);

    // when
    List<Board> boardList = boardRepository.findAllByMember(member);

    // then
    assertThat(boardList).hasSize(2)
        .extracting("title", "content")
        .containsExactlyInAnyOrder(
            tuple(board1.getTitle(), board1.getContent()),
            tuple(board2.getTitle(), board2.getContent())
        );

  }

  @Test
  @DisplayName("게시글 제목으로 게시글 찾기")
  void findByTitleContainingIgnoreCase() {
    // given
    Member member = createMember();
    Board board1 = Board.builder()
        .title("test-title1")
        .content("test-content1")
        .member(member)
        .build();
    Board board2 = Board.builder()
        .title("test-title2")
        .content("test-content2")
        .member(member)
        .build();
    boardRepository.save(board1);
    boardRepository.save(board2);

    // when
    Page<Board> findTarget = boardRepository.findByTitleContainingIgnoreCase("test",
        Pageable.ofSize(10));

    // then
    assertThat(findTarget).hasSize(2)
        .extracting("title")
        .containsExactlyInAnyOrder(
            findTarget.getContent().get(0).getTitle(),
            findTarget.getContent().get(1).getTitle()
        );
    assertThat(findTarget.getPageable().getPageNumber()).isEqualTo(0);
    assertThat(findTarget.getPageable().getPageSize()).isEqualTo(10);

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