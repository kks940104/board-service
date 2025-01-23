package org.anonymous.board.services.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.RequestComment;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.entities.QCommentData;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.member.Member;
import org.anonymous.member.MemberUtil;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class CommentInfoService {

    private final MemberUtil memberUtil;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;
    private final CommentDataRepository commentDataRepository;


    /**
     * 댓글 한개 조회
     * @param seq
     * @return
     */
    public CommentData get(Long seq) {
        CommentData item = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);
        addInfo(item);
        return item;
    }

    public RequestComment getForm(Long seq) {
        CommentData item = get(seq);
        BoardData data = item.getData();
        RequestComment form = modelMapper.map(item, RequestComment.class);
        form.setMode("edit");
        form.setBoardDataSeq(data.getSeq());

        return form;
    }

    /**
     * 게시글 번호로 작성된 댓글 목록 조회
     * @param seq
     * @return
     */
    public List<CommentData> getList(Long seq) {

        QCommentData commentData = QCommentData.commentData;
        List<CommentData> items =
                queryFactory.selectFrom(commentData)
                        .where(commentData.data.seq.eq(seq))
                        .orderBy(commentData.createdAt.asc()) // 등록된 순서대로
                        .fetch();
        items.forEach(this::addInfo); // 추가 데이터 처리
        return items;
    }

    // 추가 데이터 처리
    private void addInfo(CommentData item) {
        Member member = memberUtil.getMember();
        String createdBy = item.getCreatedBy();
        boolean editable = memberUtil.isAdmin() || createdBy == null || (createdBy != null && memberUtil.isLogin() && member.getEmail().equals(createdBy));

        item.setEditable(editable); // 댓글 수정, 삭제 기능, 다만 비회원은 비밀번호 검증 페이지로 넘어간다.
    }
}