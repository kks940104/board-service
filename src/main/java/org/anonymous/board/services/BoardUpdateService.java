package org.anonymous.board.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.RequestBoard;
import org.anonymous.board.entities.Board;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.services.configs.BoardConfigInfoService;
import org.anonymous.global.libs.Utils;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@Lazy
@Service
@Transactional // 이거 어디껀지 봐야겠다
@RequiredArgsConstructor
public class BoardUpdateService {

    private final BoardConfigInfoService configInfoService; // 게시판 설정 가져오기
    private final BoardDataRepository boardDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest request;
    private final RestTemplate restTemplate;
    private final MemberUtil memberUtil;
    private final Utils utils;

    public BoardData process(RequestBoard form) {

        Long seq = Objects.requireNonNullElse(form.getSeq(), 0L);
        String mode = Objects.requireNonNullElse(form.getMode(), "write");

        BoardData data = null;
        if (mode.equals("edit")) { // 수정 시
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);
        } else { // 추가 시
            /**
             * 등록될때만 최초 한번 기록되는 데이터
             * - 게시판 설정, 회원
             * - gid
             * - 아이피, UserAgent
             */
            Board board = configInfoService.get(form.getBid());
            data = new BoardData();
            data.setBoard(board);
            data.setGid(form.getGid());
            data.setIpAddr(request.getRemoteAddr()); // IP 주소...
            data.setUserAgent(request.getHeader("User-Agent"));
        }

        // 글등록, 글 수정 공통 반영 사항
        String guestPw = form.getGuestPw();
        if (StringUtils.hasText(guestPw)) { // 비회원 비밀번호가 있을 경우...
            data.setGuestPw(passwordEncoder.encode(guestPw));
        }

        data.setPoster(form.getPoster());

        if (memberUtil.isAdmin()) { // 공지글 여부는 관리자만 반영 가능.
            data.setNotice(form.isNotice());
        }

        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setExternalLink(form.getExternalLink());
        data.setYoutubeUrl(form.getYoutubeUrl());
        data.setCategory(form.getCategory());

        boardDataRepository.saveAndFlush(data);

        // region 게시글 파일 첨부 작업 완료 처리

        String apiUrl = utils.serviceUrl("file-service", "/done/" + data.getGid());
        HttpEntity<Void> request = new HttpEntity<>(utils.getRequestHeader());
        restTemplate.exchange(URI.create(apiUrl), HttpMethod.GET, request, Void.class);

        // endregion

        // 비회원 게시글 인증 정보 삭제
        utils.deleteValue(utils.getUserHash() + "_board_" + seq);

        return data;
    }
}














