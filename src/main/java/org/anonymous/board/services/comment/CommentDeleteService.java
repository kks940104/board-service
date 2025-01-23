package org.anonymous.board.services.comment;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.repositories.CommentDataRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class CommentDeleteService {

    private final CommentInfoService infoService;
    private final CommentUpdateService commentUpdateService;
    private final CommentDataRepository commentDataRepository;

    /**
     * 댓글 삭제
     * @param seq
     * @return
     */
    public BoardData delete(Long seq) {
        CommentData item = infoService.get(seq);
        BoardData data = item.getData();

        commentDataRepository.delete(item);
        commentDataRepository.flush();
        
        // 댓글 갯수 업데이트
        commentUpdateService.updateCount(data.getSeq());
        return data;
    }
}

















