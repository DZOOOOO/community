package com.zerobase.community.domain.comment.repositroy;

import static org.assertj.core.api.Assertions.assertThat;

import com.zerobase.community.IntegrationTestSupport;
import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentRepositoryTest extends IntegrationTestSupport {

  @Autowired
  CommentRepository commentRepository;

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
  @DisplayName("게시글에 달린 댓글 일치 체크")
  void existsByBoardIdAndId() {
    // given
    Member member = createMember(0);
    Board board = createBoard(0, member);
    Comment comment = createComment(0, member, board);

    // when
    boolean existsComment = commentRepository
        .existsByBoardIdAndId(board.getId(), comment.getId());

    // then
    assertThat(existsComment).isTrue();
  }

  @Test
  @DisplayName("맴버가 작성한 댓글 모두 불러오기")
  void findAllByMember() {
    // given
    Member member = createMember(0);
    Board board = createBoard(0, member);
    createComment(0, member, board);
    createComment(1, member, board);
    createComment(2, member, board);
    // when
    List<Comment> commentList = commentRepository.findAllByMember(member);

    assertThat(commentList).hasSize(3)
        .extracting("content")
        .containsExactlyInAnyOrder(
            "comment-content0",
            "comment-content1",
            "comment-content2"
        );
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