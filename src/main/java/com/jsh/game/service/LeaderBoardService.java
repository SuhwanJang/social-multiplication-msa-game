package com.jsh.game.service;

import com.jsh.game.domain.LeaderBoardRow;

import java.util.List;

public interface LeaderBoardService {

    /**
     * 최고 점수 사용자와 함께 현재 리더 보드를 검색
     * @return 최고 점수와 사용자
     */
    List<LeaderBoardRow> getCurrentLeaderBoard();
}
