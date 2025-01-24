package org.anonymous.board.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.BoardSearch;
import org.anonymous.board.controllers.RequestBoard;
import org.anonymous.board.entities.Board;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.QBoardData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.services.configs.BoardConfigInfoService;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.paging.ListData;
import org.anonymous.global.paging.Pagination;
import org.anonymous.member.Member;
import org.anonymous.member.MemberUtil;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardInfoService {


    private final BoardConfigInfoService configInfoService;
    private final BoardDataRepository boardDataRepository;
    private final JPAQueryFactory queryFactory;
    private final HttpServletRequest request;
    private final ModelMapper modelMapper;
    private final MemberUtil memberUtil;
    private final Utils utils;

    /**
     * 게시글 1개 조회
     * @param seq
     * @return
     */
    public BoardData get(Long seq) {

        BoardData item = boardDataRepository.findBySeq(seq).orElseThrow(BoardDataNotFoundException::new);

        addInfo(item, true); // 추가 정보 처리

        return item;
    }

    public RequestBoard getForm(Long seq) {
        return getForm(get(seq));
    }

    /**
     * 수정 처리 시 커멘드 객체 RequestBoard로 변환하는 기능
     * @param item
     * @return
     */
    public RequestBoard getForm(BoardData item) {
        RequestBoard form = modelMapper.map(item, RequestBoard.class);
        form.setMode("edit");
        form.setBid(item.getBoard().getBid());
        return form;
    }

    /**
     * 게시글 목록
     * @param search
     * @return
     */
    public ListData<BoardData> getList(BoardSearch search) {
        int page = Math.max(search.getPage(), 1);
        Board board = null;
        int rowsPerPage = 0;
        List<String> bids = search.getBid();
        if (bids != null && !bids.isEmpty()) {
            board = configInfoService.get(bids.get(0));
            rowsPerPage = board.getRowsPerPage();
        }
        int limit = search.getLimit() > 0 ? search.getLimit() : rowsPerPage;
        int offset = (page - 1) * limit; // 페이지의 시작 위치.

        // region 검색처리

        BooleanBuilder andBuilder = new BooleanBuilder();
        QBoardData boardData = QBoardData.boardData;

        // 게시판 아이디
        if (bids != null && !bids.isEmpty()) {
            andBuilder.and(boardData.board.bid.in(bids)); // eq랑 in 조건 어떻게 되는지 확인해보자.
        }

        // 분류 검색
        List<String> categories = search.getCategory();
        if (categories != null && !categories.isEmpty()) {
            andBuilder.and(boardData.category.in(categories));
        }
        /**
         * 키워드 검색
         * sopt
         *      - ALL - 제목 + 내용 + 작성자(작성자 + 이메일 + 회원명)
         *      - SUBJECT - 제목
         *      - CONTENT - 내용
         *      - SUBJECT_CONTENT - 제목 + 내용
         *      - POSTER - 작성자 + 이메일 + 회원명
         */

        String sopt = search.getSopt();
        String skey = search.getSkey();
        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";

        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            StringExpression subject = boardData.subject;
            StringExpression content = boardData.content;
            StringExpression poster = boardData.poster.concat(boardData.createdBy);
            StringExpression condition = null;
            if (sopt.equals("SUBJECT")) { // 제목 검색
                condition = subject;
            } else if (sopt.equals("content")) { // 내용 검색
                condition = content;
            } else if (sopt.equals("SUBJECT_CONTENT")) { // 제목 + 내용
                condition = subject.concat(content);
            } else if (sopt.equals("POSTER")) { // 작성자 검색
                condition = poster;
            } else { // 통합 검색
                condition = subject.concat(content).concat(poster);
            }
            andBuilder.and(condition.contains(skey));
        }

        // endregion

        JPAQuery<BoardData> query =
                queryFactory.selectFrom(boardData)
                        .leftJoin(boardData.board)
                        .fetchJoin()
                        .where(andBuilder)
                        .offset(offset)
                        .limit(limit);
                        // 공지사항이 가장 위로 가져오게
                        // .orderBy(boardData.notice.desc(), boardData.createdAt.desc())
                        // .fetch();

        // 회원 이메일
        List<String> emails = search.getEmail();
        if (emails != null && emails.isEmpty()) {
            andBuilder.and(boardData.createdBy.in(emails));
        }

        // region 정렬 조건 처리

        String sort = search.getSort();
        if (StringUtils.hasText(sort)) {
            String[] _sort = sort.split("_");
            String field = _sort[0];
            String direction = _sort[1];
            if (field.equals("viewCount")) {
                query.orderBy(direction.equalsIgnoreCase("DESC") ? boardData.viewCount.desc() : boardData.viewCount.asc());
            } else if (field.equals("commentCount")) {
                query.orderBy(direction.equalsIgnoreCase("DESC") ? boardData.commentCount.desc() : boardData.commentCount.asc());
            } else if (field.equals("bestCount")) {
                query.orderBy(direction.equalsIgnoreCase("DESC") ? boardData.bestCount.desc() : boardData.bestCount.asc());
            } else {
                query.orderBy(boardData.notice.desc(), boardData.createdAt.desc());
            }
        } else { // 기본 정렬 조건 - notice DESC, createdAt DESC
            query.orderBy(boardData.notice.desc(), boardData.createdAt.desc());
        }

        // endregion


        List<BoardData> items = query.fetch();

        long total = boardDataRepository.count();

        items.forEach(this::addInfo); // 추가정보처리

        int ranges = utils.isMobile() ? 5 : 10;
        if (board != null) { // 게시판별 설정이 있는 경우
            ranges = utils.isMobile() ? board.getPageRangesMobile() : board.getPageRanges();
        }

        Pagination pagination = new Pagination(page, (int) total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 게시판별 조회
     * @param bid
     * @param search
     * @return
     */
    public ListData<BoardData> getList(String bid, BoardSearch search) {
        search.setBid(List.of(bid));
        return getList(search);
    }

    /**
     * 게시판별 최신 게시글
     * @param bid
     * @param limit
     * @return
     */
    public List<BoardData> getLatest(String bid, String category, int limit) {
        BoardSearch search = new BoardSearch();
        search.setLimit(limit);
        search.setBid(List.of(bid));

        search.setCategory(category == null ? null : List.of(category));
        ListData<BoardData> data = getList(search);

        List<BoardData> items = data.getItems();
        return items == null ? List.of() : items;
    }

    public List<BoardData> getLatest(String bid, int limit) {
        return getLatest(bid, null, limit);
    }

    public List<BoardData> getLatest(String bid) {
        return getLatest(bid, 5);
    }

    /**
     * 로그인한 회원이 작성한 게시글 목록
     * @param search
     * @return
     */
    public ListData<BoardData> getMyList(BoardSearch search) {
        if (!memberUtil.isLogin()) {
            return new ListData<>(List.of(), null);
        }

        Member member = memberUtil.getMember();
        String email = member.getEmail();
        search.setEmail(List.of(email));

        return getList(search);
    }

    /**
     * 추가 정보 처리
     * @param item
     */
    private void addInfo(BoardData item, boolean isView) {

        // region 이전, 다음 게시글

        if (isView) { // 보기 페이지 데이터를 조회하는 경우만 이전, 다음글을 조회
            QBoardData boardData = QBoardData.boardData;
            Long seq = item.getSeq();
            BoardData prev = queryFactory.selectFrom(boardData)
                    .where(boardData.seq.lt(seq)) // ~보다 작은 lt
                    .orderBy(boardData.seq.desc())
                    .fetchFirst();
            BoardData next = queryFactory.selectFrom(boardData)
                    .where(boardData.seq.gt(seq)) // ~보다 큰 gt
                    .orderBy(boardData.seq.asc())
                    .fetchFirst();
            item.setPrev(prev);
            item.setNext(next);
        }

        // endregion

        // region listable, writable, editable, mine 처리

        Board board = item.getBoard();
        configInfoService.addInfo(board);

        boolean listable = board.isListable();
        boolean writable = board.isWritable();

        String createdBy = item.getCreatedBy();
        Member loggedMember = memberUtil.getMember();

        boolean editable = createdBy == null || (memberUtil.isLogin() && loggedMember.getEmail().equals(createdBy)); // 비회원은 비밀번호 확인이 필요하므로 버튼 노출, 회원 게시글은 로그인한 회원과 일치하면 노출.

        boolean mine = utils.getValue(utils.getUserHash() + "_board_" + item.getSeq()) != null || (memberUtil.isLogin() && loggedMember.getEmail().equals(createdBy));

        item.setListable(listable);
        item.setWritable(writable);
        item.setEditable(editable);
        item.setMine(mine);

        // endregion

    }

    private void addInfo(BoardData item) {
        addInfo(item, false);
    }

    /**
     * 게시글 번호와 게시판 아이디로 현재 페이지 구하기.
     *
     * @param bid
     * @param seq
     * @param limit
     * @return
     */
    public int getPage(String bid, Long seq, int limit) {
        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(boardData.board.bid.eq(bid))
                .and(boardData.seq.goe(seq)); // 크거나 같다.

        long total = boardDataRepository.count(builder);
        int page = (int) Math.ceil((double) total / limit);

        return page;
    }

}












