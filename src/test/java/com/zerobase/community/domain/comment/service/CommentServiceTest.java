package com.zerobase.community.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.zerobase.community.IntegrationTestSupport;
import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.comment.repositroy.CommentRepository;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import com.zerobase.community.web.comment.dto.request.CommentEditRequestDto;
import com.zerobase.community.web.comment.dto.request.CommentWriteRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends IntegrationTestSupport {

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  CommentService commentService;

  @Autowired
  BoardRepository boardRepository;

  @Autowired
  MemberRepository memberRepository;

  @AfterEach
  void tearDown() {
    commentRepository.deleteAllInBatch();
    boardRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("댓글 작성")
  void writeComment() {
    // given
    Member member = createMember(0);
    Board board = createBoard(0, member);
    CommentWriteRequestDto request = CommentWriteRequestDto.builder()
        .content("test-comment")
        .boardId(board.getId())
        .build();

    // when
    Comment comment = commentService.writeComment(request, member);

    // then
    assertThat(comment).extracting("content", "member")
        .containsExactlyInAnyOrder("test-comment", member);
  }

  @Test
  @DisplayName("댓글 수정")
  void editComment() {
    // given
    Member member = createMember(0);
    Board board = createBoard(0, member);
    Comment comment = createComment(0, member, board);
    CommentEditRequestDto request = CommentEditRequestDto.builder()
        .commentId(comment.getId())
        .boardId(board.getId())
        .content("test-edit")
        .build();

    // when
    commentService.editComment(request, member);
    Comment target = commentRepository.findById(comment.getId()).orElse(null);

    // then
    assertThat(target.getContent()).isEqualTo("test-edit");
  }

  @Test
  @DisplayName("댓글 삭제")
  void deleteComment() {
    // given
    Member member = createMember(0);
    Board board = createBoard(0, member);
    Comment comment = createComment(0, member, board);

    // when
    commentService.deleteComment(comment.getId(), member);

    // then
    Comment target = commentRepository.findById(comment.getId())
        .orElse(null);
    assertThat(target).isNull();
  }


  private Member createMember(int num) {
    return memberRepository.save(Member.builder()
        .loginId("test" + num)
        .password("test" + num)
        .nickName("test" + num)
        .email("test" + num + "@test.com")
        .build());
  }

  private Board createBoard(int num, Member member) {
    Board board = Board.builder()
        .title("board-title" + num)
        .content("board-content" + num)
        .member(member)
        .build();
    return boardRepository.save(board);
  }

  private Comment createComment(int num, Member member, Board board) {
    return commentRepository.save(Comment.builder()
        .content("comment-content" + num)
        .board(board)
        .member(member)
        .build());
  }
}