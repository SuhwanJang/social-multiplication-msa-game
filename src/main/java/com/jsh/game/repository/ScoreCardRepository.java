package com.jsh.game.repository;

import com.jsh.game.domain.LeaderBoardRow;
import com.jsh.game.domain.ScoreCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScoreCardRepository extends CrudRepository<ScoreCard, Long> {

    @Query("SELECT SUM(s.score) " +
           "FROM com.jsh.game.domain.ScoreCard s " +
           "WHERE s.userId = :userId GROUP BY s.userId")
    int getTotalScoreForUser(@Param("userId") final Long userId);

    @Query("SELECT NEW com.jsh.game.domain.LeaderBoardRow(s.userId, SUM(s.score)) " +
            "FROM com.jsh.game.domain.ScoreCard s " +
            "GROUP BY s.userId ORDER BY SUM(s.score) DESC")
    List<LeaderBoardRow> findFirst10();

    List<ScoreCard> findByUserIdOrderByScoreTimestampDesc(final Long userId);
}
