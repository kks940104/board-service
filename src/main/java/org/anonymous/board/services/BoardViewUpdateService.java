package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.BoardView;
import org.anonymous.board.entities.QBoardView;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.BoardViewRepository;
import org.anonymous.global.libs.Utils;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardViewUpdateService {
    private final BoardDataRepository boardDataRepository;
    private final BoardViewRepository boardViewRepository;
    private final Utils utils;

    public long process(Long seq) {
        BoardData item = boardDataRepository.findById(seq).orElse(null);
        if (item == null) return 0L;



        try {
            BoardView view = new BoardView();
            view.setSeq(seq);
            view.setHash(utils.getMemberHash());
            boardViewRepository.saveAndFlush(view);
        } catch (Exception e) {}

        // 조회수 업데이트
        QBoardView boardView = QBoardView.boardView;
        long total = boardViewRepository.count(boardView.seq.eq(seq));

        item.setViewCount(total);
        boardDataRepository.saveAndFlush(item);

        return total;
    }
}


























