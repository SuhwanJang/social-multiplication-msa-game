package com.jsh.game.service;

import com.jsh.game.domain.Badge;
import com.jsh.game.domain.BadgeCard;
import com.jsh.game.domain.GameStats;
import com.jsh.game.domain.ScoreCard;
import com.jsh.game.repository.BadgeCardRepository;
import com.jsh.game.repository.ScoreCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jsh.game.domain.Badge.*;

@Service
@Slf4j
public class GameServiceImpl implements GameService {
    private ScoreCardRepository scoreCardRepository;
    private BadgeCardRepository badgeCardRepository;

    GameServiceImpl(final ScoreCardRepository scoreCardRepository,
                    final BadgeCardRepository badgeCardRepository) {
        this.scoreCardRepository = scoreCardRepository;
        this.badgeCardRepository = badgeCardRepository;
    }

    @Override
    public GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct) {
        if (correct) {
            ScoreCard scoreCard = new ScoreCard(userId, attemptId);
            scoreCardRepository.save(scoreCard);
            log.info("사용자 ID {}, 점수 {} 점, 답안 ID {}",
                    userId, scoreCard.getScore(), attemptId);
            List<BadgeCard> badgeCards = processForBadges(userId, attemptId);
            return new GameStats(userId, scoreCard.getScore(),
                    badgeCards.stream()
                            .map(BadgeCard::getBadge)
                            .collect(Collectors.toList()));
        }
        return GameStats.emptyStats(userId);
    }

    private List<BadgeCard> processForBadges(final Long userId, final Long attemptId) {
        List<BadgeCard> badgeCards = new ArrayList<>();
        int totalScore = scoreCardRepository.getTotalScoreForUser(userId);
        List<ScoreCard> scoreCardList = scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId);
        if (scoreCardList.size() == 1) {
            BadgeCard firstWon = giveBadgeToUser(FIRST_WON, userId);
            badgeCards.add(firstWon);
        }

        checkAndGiveBadgeBasedOnScore(badgeCards, BRONZE, totalScore, userId).ifPresent(badgeCards::add);
        checkAndGiveBadgeBasedOnScore(badgeCards, SILVER, totalScore, userId).ifPresent(badgeCards::add);
        checkAndGiveBadgeBasedOnScore(badgeCards, GOLD, totalScore, userId).ifPresent(badgeCards::add);

        return badgeCards;
    }

    private Optional<BadgeCard> checkAndGiveBadgeBasedOnScore(final List<BadgeCard> badgeCards, final Badge badge,
                                                              final int score, final Long userId) {
        if (score >= badge.getScore() && !containsBadge(badgeCards, badge)) {
            return Optional.of(giveBadgeToUser(badge, userId));
        }
        return Optional.empty();
    }

    private BadgeCard giveBadgeToUser(final Badge badge, final Long userId) {
        BadgeCard badgeCard = new BadgeCard(userId, badge);
        badgeCardRepository.save(badgeCard);
        log.info("사용자 ID {} 새로운 배지 획득: {}", userId, badge);
        return badgeCard;
    }

    private boolean containsBadge(List<BadgeCard> badgeCards, Badge badge) {
        return badgeCards.stream().anyMatch(b -> b.getBadge().equals(badge));
    }

    @Override
    public GameStats retrieveStatsForUser(Long userId) {
        List<BadgeCard> badgeCards = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);
        int score = scoreCardRepository.getTotalScoreForUser(userId);
        return new GameStats(userId, score,
                badgeCards.stream().map(BadgeCard::getBadge).collect(Collectors.toList()));
    }
}
