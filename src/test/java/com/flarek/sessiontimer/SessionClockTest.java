package com.flarek.sessiontimer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionClockTest {
    @Test
    void formatsElapsedTimeWithMilliseconds() {
        assertEquals("00:00:00.000", SessionClock.format(0));
        assertEquals("01:02:03.004", SessionClock.format(3_723_004));
        assertEquals("27:00:00.000", SessionClock.format(97_200_000));
    }
}
