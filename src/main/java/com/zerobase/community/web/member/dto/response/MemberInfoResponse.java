package com.zerobase.community.web.member.dto.response;

import com.zerobase.community.web.board.dto.response.BoardInfoResponse;
import com.zerobase.community.web.comment.dto.response.CommentInfoResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoResponse {

  // 유저 아이디
  private String loginId;

  // 유저 이메일
  private String email;

  // 유저 닉네임
  private String nickName;

  // 작성한 게시글 목록
  private List<BoardInfoResponse> myBoardList;

  // 작성한 댓글 목록
  private List<CommentInfoResponse> myCommentList;

}
