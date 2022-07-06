package com.jsh.game.domain;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public final class LeaderBoardRow {
    private final Long userId;
    private final Long totalScore;
}
