package com.zerobase.community.domain.board.entity;

import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
//@Table(name = "board", indexes = @Index(name = "idx_board_title", columnList = "title"))
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  private String title;

  @Lob
  @Column(name = "content")
  private String content;

  // FK
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "board")
  private List<Comment> commentList = new ArrayList<>();

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt = LocalDateTime.now();

  @LastModifiedDate
  @Column(name = "updated_at", updatable = false)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt = LocalDateTime.now();

}
