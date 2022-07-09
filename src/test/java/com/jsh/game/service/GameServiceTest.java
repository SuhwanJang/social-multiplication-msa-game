package com.jsh.game.service;

import com.jsh.game.repository.BadgeCardRepository;
import com.jsh.game.repository.ScoreCardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;


class GameServiceTest {

    private GameServiceImpl gameService;

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @Mock
    private BadgeCardRepository badgeCardRepository;


    @Test
    void newAttemptForUser() {
    }

    @Test
    void retrieveStatsForUser() {
    }
}