package com.zerobase.community.domain.member.repository;

import com.zerobase.community.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 아이디로 조회
    Optional<Member> findByLoginId(String loginId);

    // 아이디 존재여부 체크
    boolean existsByLoginId(String loginId);

    // 닉네임 존재여부 체크
    boolean existsByNickName(String nickName);

}
