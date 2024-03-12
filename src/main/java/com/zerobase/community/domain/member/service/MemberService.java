package com.zerobase.community.domain.member.service;

import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.comment.repositroy.CommentRepository;
import com.zerobase.community.domain.member.Grade;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import com.zerobase.community.web.board.dto.response.BoardInfoResponse;
import com.zerobase.community.web.comment.dto.response.CommentInfoResponse;
import com.zerobase.community.web.member.dto.request.MemberEditRequestDto;
import com.zerobase.community.web.member.dto.request.MemberJoinRequestDto;
import com.zerobase.community.web.member.dto.request.MemberLoginRequestDto;
import com.zerobase.community.web.member.dto.request.MemberPasswordRequestDto;
import com.zerobase.community.web.member.dto.response.MemberInfoResponse;
import com.zerobase.community.web.member.exception.MemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final BoardRepository boardRepository;
  private final CommentRepository commentRepository;

  // 회원조회 메서드(PK 이용 조회)
  @Transactional(readOnly = true)
  public Member findById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException("회원이 없습니다."));
  }

  // 회원조회 메서드(회원 아이디기 이용 조회)
  @Transactional(readOnly = true)
  public Member findByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId)
        .orElseThrow(() -> new MemberException("회원이 없습니다."));
  }

  // 회원가입 메서드
  @Transactional
  public Member join(MemberJoinRequestDto request) {
    // 1. 아이디 중복검사
    boolean byLoginIdExists = memberRepository.existsByLoginId(request.getLoginId());
    if (byLoginIdExists) {
      throw new MemberException("이미 존재하는 아이디입니다.");
    }

    // 2. 닉네임 중복검사
    boolean byNickNameExists = memberRepository.existsByNickName(request.getNickName());
    if (byNickNameExists) {
      throw new MemberException("이미 존재하는 닉네임 입니다.");
    }

    // 3. 회원가입
    return memberRepository.save(Member.builder()
        .loginId(request.getLoginId())
        .password(request.getPassword())
        .nickName(request.getNickName())
        .email(request.getEmail())
        .grade(Grade.USER)
        .build());
  }

  // 로그인 메서드
  @Transactional
  public Member login(MemberLoginRequestDto request) {
    // 1. 아이디 + 비밀번호 체크
    return memberRepository.findByLoginId(request.getLoginId())
        .filter(m -> m.getPassword().equals(request.getPassword()))
        .orElseThrow(() -> new MemberException("아이디, 비밀번호를 다시 확인해주세요."));
  }

  // 회원정보 수정 메서드(닉네임, 이메일)
  @Transactional
  public void editMember(MemberEditRequestDto request, Member loginMember) {
    Member findMember = memberRepository.findById(loginMember.getId())
        .orElseThrow(() -> new MemberException("회원이 없습니다."));

    findMember.setNickName(request.getNickName());
    findMember.setEmail(request.getEmail());
  }

  // 비밀번호 수정 메서드
  @Transactional
  public void editPassword(MemberPasswordRequestDto request, Member loginMember) {
    Member findMember = memberRepository.findById(loginMember.getId())
        .orElseThrow(() -> new MemberException("회원이 없습니다."));

    findMember.setPassword(request.getPassword());

    memberRepository.save(findMember);
  }

  // 회원탈퇴 메서드
  @Transactional
  public void deleteMember(Long memberId) {
    Member findMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException("회원이 없습니다."));

    memberRepository.delete(findMember);
  }

  // 마이페이지 조회 메서드
  @Transactional(readOnly = true)
  public MemberInfoResponse getMyPage(Member loginMember) {
    // 1. 유저정보
    String loginId = loginMember.getLoginId();
    String email = loginMember.getEmail();
    String nickName = loginMember.getNickName();

    // 2. 작성한 게시글 리스트
    List<BoardInfoResponse> boardList = boardRepository.findAllByMember(loginMember)
        .stream()
        .map(b -> BoardInfoResponse.builder()
            .id(b.getId())
            .title(b.getTitle())
            .createdAt(b.getCreatedAt())
            .build()).toList();

    // 3. 작성한 댓글 리스트
    List<CommentInfoResponse> commentList = commentRepository.findAllByMember(loginMember)
        .stream()
        .map(c -> CommentInfoResponse.builder()
            .id(c.getId())
            .content(c.getContent())
            .createdAt(c.getCreatedAt())
            .build()).toList();

    return MemberInfoResponse.builder()
        .loginId(loginId)
        .email(email)
        .nickName(nickName)
        .myBoardList(boardList)
        .myCommentList(commentList)
        .build();
  }
}
