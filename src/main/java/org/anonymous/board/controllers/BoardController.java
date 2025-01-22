package org.anonymous.board.controllers;

import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import lombok.RequiredArgsConstructor;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.rests.JSONData;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final Utils utils;

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
    public JSONData save(@RequestBody @Valid RequestBoard form, Errors errors) {
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "write";
        commonProcess(form.getBid(), mode);
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

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

    /**
     * 게시글 번호로 공통 처리
     * @param seq
     * @param mode
     */
    private void commonProcess(Long seq, String mode) {
        
    }

    /**
     * 게시글 아이디로 공통 처리
     * @param bid
     * @param mode
     */
    private void commonProcess(String bid, String mode) {

    }
}














