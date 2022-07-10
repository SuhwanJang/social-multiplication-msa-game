package com.jsh.game.service;

import com.jsh.game.client.MultiplicationResultAttemptClientImpl;
import com.jsh.game.client.dto.MultiplicationResultAttempt;
import com.jsh.game.domain.Badge;
import com.jsh.game.domain.BadgeCard;
import com.jsh.game.domain.GameStats;
import com.jsh.game.domain.ScoreCard;
import com.jsh.game.repository.BadgeCardRepository;
import com.jsh.game.repository.ScoreCardRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private GameServiceImpl gameService;

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @Mock
    private BadgeCardRepository badgeCardRepository;

    @Mock
    private MultiplicationResultAttemptClientImpl client;

    @BeforeEach
    public void setUp() {
        gameService = new GameServiceImpl(scoreCardRepository, badgeCardRepository);

        //given - 행운의 숫자를 포함하지 않는 답안
        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(
                "john_doe", 20, 70, 1400, true);
        given(client.retrieveMultiplicationResultAttemptById(anyLong()))
                .willReturn(attempt);
    }

    @Test
    void newAttemptForUser() {

    }

    @Test
    void firstCorrectAttemptTest() {
        //given
        Long userId = 10L;
        Long attemptId = 100L;
        int totalScore = 10;
        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(Collections.singletonList(scoreCard));

        //when
        GameStats stats = gameService.newAttemptForUser(userId, attemptId, true);

        //then
        assertThat(stats.getScore()).isEqualTo(scoreCard.getScore());
        assertThat(stats.getBadges()).containsOnly(Badge.FIRST_WON);
    }

    @Test
    void CorrectAttemptForScoreBadgeTest() {
        //given
        Long userId = 10L;
        Long attemptId = 100L;
        int totalScore = 100;
        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(createNScoreCards(10, userId));

        //when
        GameStats stats = gameService.newAttemptForUser(userId, attemptId, true);

        //then
        assertThat(stats.getScore()).isEqualTo(scoreCard.getScore());
        assertThat(stats.getBadges()).containsOnly(Badge.BRONZE);
    }

    @Test
    void CorrectAttemptForLuckyTest() {
        //given
        Long userId = 10L;
        Long attemptId = 100L;
        int totalScore = 10;
        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(createNScoreCards(2, userId));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(
                Collections.singletonList(new BadgeCard(userId, Badge.FIRST_WON))
        );

        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt("john_doe", 42, 10, 420, true);
        given(client.retrieveMultiplicationResultAttemptById(userId)).willReturn(attempt);

        //when
        GameStats stats = gameService.newAttemptForUser(userId, attemptId, true);

        //then
        assertThat(stats.getScore()).isEqualTo(scoreCard.getScore());
        assertThat(stats.getBadges()).containsOnly(Badge.LUCKY_NUMBER);
    }

    @Test
    void WrongAttemptTest() {
        //given
        Long userId = 10L;
        Long attemptId = 100L;

        //when
        GameStats stats = gameService.newAttemptForUser(userId, attemptId, false);

        //then
        assertThat(stats.getScore()).isEqualTo(0);
        assertThat(stats.getBadges()).isEmpty();
    }

    @Test
    void retrieveStatsForUser() {
        // given
        Long userId = 1L;
        int totalScore = 1000;
        BadgeCard badgeCard = new BadgeCard(userId, Badge.SILVER);
        given(scoreCardRepository.getTotalScoreForUser(userId))
                .willReturn(totalScore);
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId))
                .willReturn(Collections.singletonList(badgeCard));

        // when
        GameStats stats = gameService.retrieveStatsForUser(userId);

        // assert - 점수 카드 하나와 첫 번째 정답 배지를 획득
        assertThat(stats.getScore()).isEqualTo(totalScore);
        assertThat(stats.getBadges()).containsOnly(Badge.SILVER);
    }

    private List<ScoreCard> createNScoreCards(int n, Long userId) {
        return IntStream.range(0, n).mapToObj(i -> new ScoreCard(userId, (long) i)).collect(Collectors.toList());
    }
}