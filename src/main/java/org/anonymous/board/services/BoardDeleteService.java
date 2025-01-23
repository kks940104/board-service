package org.anonymous.board.services;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.global.libs.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardDeleteService {

    private final Utils utils;
    private final RestTemplate restTemplate;
    private final BoardInfoService infoService;
    private final BoardDataRepository boardRepository;

    public void delete(Long seq) {
        BoardData item = infoService.get(seq);
        String gid = item.getGid();

        // region 파일 삭제

        HttpEntity<Void> request = new HttpEntity<>(utils.getRequestHeader());
        String apiUrl = utils.serviceUrl("file-service", "/delete/" + item.getGid());
        restTemplate.exchange(URI.create(apiUrl), HttpMethod.DELETE, request, Void.class);

        // endregion

        boardRepository.delete(item);
        boardRepository.flush();

        // 비회원 인증 정보 삭제
        utils.deleteValue(utils.getUserHash() + "_board_" + seq);

    }
}
