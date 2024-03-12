package com.zerobase.community.domain.comment.repositroy;

import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  boolean existsByBoardIdAndId(Long boardId, Long id);

  List<Comment> findAllByMember(Member loginMember);
}
