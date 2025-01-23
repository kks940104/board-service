package org.anonymous.board.entities;

import lombok.Data;
import jakarta.persistence.*;
import org.anonymous.global.entities.BaseMemberEntity;

import java.io.Serializable;

@Data
@Entity
// 오름차순정렬
@Table(indexes = @Index(name="idx_comment_data_created_at", columnList = "createdAt ASC"))
public class CommentData extends BaseMemberEntity implements Serializable {
    @Id @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    private BoardData data;

    @Column(length = 40, nullable = false)
    private String commenter;

    @Column(length = 65)
    private String guestPw; // 비회원 비밀번호

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 20)
    private String ipAddr;

    @Column(length = 150)
    private String userAgent;

    @Transient
    private boolean editable; // 댓글 수정, 삭제 가능 여부
}















