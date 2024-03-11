package com.zerobase.community.domain.board.service;

import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.board.dto.request.BoardEditRequestDto;
import com.zerobase.community.web.board.dto.request.BoardWriteRequestDto;
import com.zerobase.community.web.board.dto.response.BoardListViewResponse;
import com.zerobase.community.web.board.dto.response.DetailViewBoardResponse;
import com.zerobase.community.web.board.exception.BoardException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;

  // 게시글 찾기 (PK 기준)
  @Transactional(readOnly = true)
  public Board findById(Long id) {
    return boardRepository.findById(id)
        .orElseThrow(() -> new BoardException("게시글이 없습니다."));
  }

  // 게시글 작성
  @Transactional
  public Board writeBoard(BoardWriteRequestDto dto, Member loginMember) {
    return boardRepository.save(Board.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .member(loginMember)
        .build());
  }

  // 게시글 수정
  @Transactional
  public void editBoard(BoardEditRequestDto dto, Member loginMember) {
    // 1. 수정 게시글 조회
    Board target = boardRepository.findById(dto.getBoardId())
        .orElseThrow(() -> new BoardException("게시글이 없습니다."));

    // 2. 수정 게시글 작성 유저와 수정하려는 유저가 같은지 체크
    checkUser(loginMember, target);

    // 3. 수정 데이터 반영.
    target.setTitle(dto.getTitle());
    target.setContent(dto.getContent());
  }

  // 게시글 삭제
  @Transactional
  public void deleteBoard(Long boardId, Member loginMember) {
    // 1. 삭제 게시글 조회
    Board target = boardRepository.findById(boardId)
        .orElseThrow(() -> new BoardException("게시글이 없습니다."));

    // 2. 삭제 게시글 작성 유저와 삭제하려는 유저가 같은지 체크
    checkUser(loginMember, target);

    // 3. 삭제 데이터 반영.
    boardRepository.deleteById(boardId);
  }

  // 게시글 상세 조회
  @Transactional(readOnly = true)
  public DetailViewBoardResponse detailViewBoard(Long boardId) {
    // 1. 조회 게시글 조회
    Board target = boardRepository.findById(boardId)
        .orElseThrow(() -> new BoardException("게시글이 없습니다."));

    // 2. 댓글 내용 리스트에 담기
    List<String> commentList = target.getCommentList().stream()
        .map(Comment::getContent).toList();

    return DetailViewBoardResponse.builder()
        .title(target.getTitle())
        .content(target.getContent())
        .writeMemberNickName(target.getMember().getNickName())
        .commentList(commentList)
        .createdAt(target.getCreatedAt())
        .updatedAt(target.getUpdatedAt())
        .build();
  }

  // 게시글 리스트 조회 (페이징 처리)
  @Transactional(readOnly = true)
  public List<BoardListViewResponse> boardListview(Pageable pageable) {
    Page<Board> boardList = boardRepository.findAll(pageable);
    return boardList.stream().map(board -> BoardListViewResponse.builder()
            .boardId(board.getId())
            .title(board.getTitle())
            .commentCount(board.getCommentList().size())
            .createdAt(board.getCreatedAt())
            .build())
        .collect(Collectors.toList());
  }

  private static void checkUser(Member loginMember, Board target) {
    Member writeMember = target.getMember();
    if (!writeMember.equalsMember(loginMember)) {
      throw new BoardException("작성자가 다릅니다.");
    }
  }
}
