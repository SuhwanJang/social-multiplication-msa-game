package com.jsh.game.repository;

import com.jsh.game.domain.BadgeCard;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BadgeCardRepository extends CrudRepository<BadgeCard, Long> {
    /**
     *
     * @param userId BadgeCard를 조회하고자 하는 사용자의 ID
     * @return 최근 획득한 순으로 정렬된 BadgeCard 목록
     */
    List<BadgeCard> findByUserIdOrderByBadgeTimestampDesc(final Long userId);
}
