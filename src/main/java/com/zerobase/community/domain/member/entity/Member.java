package com.zerobase.community.domain.member.entity;

import com.zerobase.community.domain.member.Grade;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", unique = true, updatable = false)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickName;

    @Column(name = "email")
    private String email;

    @Column(name = "grade")
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_at", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
