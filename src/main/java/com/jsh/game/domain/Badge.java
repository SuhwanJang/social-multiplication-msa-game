package com.jsh.game.domain;

public enum Badge {

    BRONZE,
    SILVER,
    GOLD,

    FIRST_ATTEMPT,
    FIRST_WON,
    LUCKY_NUMBER;

    public int getScore() {
        if (this.equals(BRONZE)) {
            return 100;
        } else if (this.equals(SILVER)) {
            return 500;
        } else {
            return 999;
        }
    }
}
