package org.anonymous.board.controllers;

import lombok.RequiredArgsConstructor;
import org.anonymous.global.rests.JSONData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BoardAdminController {

    /**
     * 게시판 설정 등록, 수정 처리
     *
     * @return
     */
    @PostMapping("/config")
    public JSONData save() {

        return null;
    }

    /**
     * 게시판 설정 목록
     * @return
     */
    @GetMapping("/config")
    public JSONData list() {

        return null;
    }

    /**
     * 게시판 한개 또는 여러개 일괄 수정
     * @return
     */
    @PatchMapping("/config")
    public JSONData update() {

        return null;
    }

    /**
     * 게시판 한개 또는 여러개 삭제
     * @param bids
     * @return
     */
    @DeleteMapping("/config")
    public JSONData delete(@RequestParam("bid") List<String> bids) {

        return null;
    }
}













