package com.jsh.game.service;

import com.jsh.game.domain.LeaderBoardRow;
import com.jsh.game.domain.ScoreCard;
import com.jsh.game.repository.ScoreCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private ScoreCardRepository scoreCardRepository;

    public LeaderBoardServiceImpl(final ScoreCardRepository scoreCardRepository) {
        this.scoreCardRepository = scoreCardRepository;
    }

    @Override
    public List<LeaderBoardRow> getCurrentLeaderBoard() {
        return scoreCardRepository.findFirst10();
    }
}
