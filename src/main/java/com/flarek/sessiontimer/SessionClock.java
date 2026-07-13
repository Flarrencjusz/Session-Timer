package com.flarek.sessiontimer;

import java.util.Locale;

public final class SessionClock {
    private long startedAtNanos;
    private boolean running;

    public void restart() {
        startedAtNanos = System.nanoTime();
        running = true;
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public long elapsedMillis() {
        if (!running) {
            return 0;
        }
        return Math.max(0, (System.nanoTime() - startedAtNanos) / 1_000_000L);
    }

    public static String format(long elapsedMillis) {
        long hours = elapsedMillis / 3_600_000L;
        long minutes = (elapsedMillis / 60_000L) % 60L;
        long seconds = (elapsedMillis / 1_000L) % 60L;
        long millis = elapsedMillis % 1_000L;
        return String.format(Locale.ROOT, "%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
