package org.anonymous.board.controllers;

import jakarta.ws.rs.DELETE;
import lombok.RequiredArgsConstructor;
import org.anonymous.global.rests.JSONData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    /**
     * 게시판 설정 한개 조회
     * @param bid
     * @return
     */
    @GetMapping("/config/{bid}")
    public JSONData config(@PathVariable("bid") String bid) {

        return null;
    }

    /**
     * 게시글 등록, 수정 처리
     * @return
     */
    @PostMapping("/save")
    public JSONData save() {

        return null;
    }

    /**
     * 게시글 한개 조회
     *  - 글보기, 글 수정시에 활용될 수 있음(프론트앤드)
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {

        return null;
    }

    /**
     * 게시글 목록 조회
     * @param bid
     * @return
     */
    @GetMapping("/list/{bid}")
    public JSONData list(@PathVariable("bid") String bid) {

        return null;
    }

    /**
     * 게시글 한개 삭제
     * @param seq
     * @return
     */
    @DeleteMapping("/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {

        return null;
    }
}














