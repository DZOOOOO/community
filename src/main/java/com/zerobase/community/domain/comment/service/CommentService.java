package com.zerobase.community.domain.comment.service;

import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.comment.repositroy.CommentRepository;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.board.exception.BoardException;
import com.zerobase.community.web.comment.dto.request.CommentEditRequestDto;
import com.zerobase.community.web.comment.dto.request.CommentWriteRequestDto;
import com.zerobase.community.web.comment.exception.CommentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final BoardRepository boardRepository;

  // 댓글 작성
  @Transactional
  public Comment writeComment(CommentWriteRequestDto dto, Member loginMember) {

    Board target = boardRepository.findById(dto.getBoardId())
        .orElseThrow(() -> new BoardException("게시글이 없습니다."));

    return commentRepository.save(Comment.builder()
        .content(dto.getContent())
        .board(target)
        .member(loginMember)
        .build());
  }

  // 댓글 수정
  @Transactional
  public void editComment(CommentEditRequestDto dto, Member loginMember) {

    // 댓글이 작성된 게시글 && 댓글 작성자 일치 체크
    boolean checkBoardAndComment = commentRepository
        .existsByBoardIdAndId(dto.getBoardId(), dto.getCommentId());

    if (!checkBoardAndComment) {
      throw new CommentException("잘못된 요청입니다.");
    }

    // 댓글 작성자 일치 체크
    Comment target = commentRepository.findById(dto.getCommentId())
        .orElseThrow(() -> new CommentException("댓글이 없습니다."));
    checkUser(loginMember, target);

    // 댓글 수정
    target.setContent(dto.getContent());
  }

  // 댓글 삭제
  @Transactional
  public void deleteComment(Long commentId, Member loginMember) {
    Comment target = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException("댓글이 없습니다."));
    checkUser(loginMember, target);
    commentRepository.deleteById(commentId);
  }

  private static void checkUser(Member loginMember, Comment target) {
    Member writeMember = target.getMember();
    if (!writeMember.equalsMember(loginMember)) {
      throw new CommentException("작성자가 다릅니다.");
    }
  }
}
