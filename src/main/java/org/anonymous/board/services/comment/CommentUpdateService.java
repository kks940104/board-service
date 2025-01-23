package org.anonymous.board.services.comment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.RequestComment;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.entities.QCommentData;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.board.services.BoardInfoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class CommentUpdateService {

    private final HttpServletRequest request;
    private final PasswordEncoder passwordEncoder;
    private final BoardInfoService boardInfoService;
    private final BoardDataRepository boardDataRepository;
    private final CommentDataRepository commentDataRepository;

    /**
     * 댓글 등록, 수정
     * @param form
     * @return
     */
    public CommentData save(RequestComment form) {
        String mode = Objects.requireNonNullElse(form.getMode(), "write");
        Long seq = form.getSeq();
        Long boardDataSeq = form.getBoardDataSeq(); // 게시글 번호

        CommentData item = null;

        if (mode.equals("edit") && seq != null && seq > 0L) { // 댓글 수정
            item = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);
        } else { // 댓글 등록
            BoardData data = boardInfoService.get(boardDataSeq); // 게시글 데이터
            item = new CommentData();
            item.setData(data);
            item.setIpAddr(request.getRemoteAddr());
            item.setUserAgent(request.getHeader("User-Agent"));
        }

        item.setCommenter(form.getCommenter());
        item.setContent(form.getContent());

        String guestPw = form.getGuestPw();

        if (StringUtils.hasText(guestPw)) {
            item.setGuestPw(passwordEncoder.encode(guestPw));
        }

        commentDataRepository.saveAndFlush(item);

        // 댓글 개수 업데이트
        updateCount(boardDataSeq);


        return item;
    }

    /**
     * 게시글 번호로 총 댓글 갯수 반영
     * @param seq : 게시글 번호
     */
    public void updateCount(Long seq) { // 게시글 번호
        QCommentData commentData = QCommentData.commentData;
        long total = commentDataRepository.count(commentData.data.seq.eq(seq)); // 게시글별 댓글 갯수
        BoardData item = boardDataRepository.findById(seq).orElse(null);
        if (item != null) {
            item.setCommentCount(total);
            boardDataRepository.saveAndFlush(item);
        }

    }
}