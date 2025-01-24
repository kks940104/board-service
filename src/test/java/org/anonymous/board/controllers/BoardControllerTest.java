package org.anonymous.board.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.anonymous.board.entities.Board;
import org.anonymous.board.services.configs.BoardConfigUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@ActiveProfiles({"default", "test"})
@AutoConfigureMockMvc
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoardConfigUpdateService configUpdateService;

    private Board board;
    private RequestBoard form;

    @BeforeEach
    void init() {
        RequestConfig config = new RequestConfig();
        config.setBid("freetalk");
        config.setName("자유게시판");
        config.setOpen(true);

        board = configUpdateService.process(config);
        form = new RequestBoard();
        form.setBid(board.getBid());
        form.setSubject("제목");
        form.setContent("내용");
        form.setPoster("작성자");
    }

    @Test
    @DisplayName("게시글 작성 테스트")
    void writeTest() throws Exception{
        String body = om.writeValueAsString(form);

        mockMvc.perform(post("/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andDo(print());
    }
}


























