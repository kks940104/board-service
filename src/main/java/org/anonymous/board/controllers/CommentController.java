package org.anonymous.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.rests.JSONData;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final Utils utils;

    /**
     * 댓글 작성, 수정
     * @return
     */
    @PostMapping("/save")
    public JSONData save(@RequestBody @Valid RequestComment form, Errors errors) {

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        return null;
    }

    /**
     * 댓글 한개 조회
     *  - 댓글 수정시 기초 데이터(프앤)
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {

        return null;
    }

    /**
     * 댓글 목록
     * @param seq : 게시글 번호
     * @return
     */
    @GetMapping("/list/{seq}")
    public JSONData list(@PathVariable("seq") Long seq) {

        return null;
    }

    /**
     * 댓글 한개 삭제
     * @param seq
     * @return
     */
    @DeleteMapping("/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {

        return null;
    }
}






























