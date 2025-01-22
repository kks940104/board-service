package org.anonymous.board.controllers;

import lombok.Data;
import org.anonymous.global.paging.CommonSearch;

import java.util.List;

@Data
public class BoardConfigSearch extends CommonSearch {
    private List<String> bid;
}
