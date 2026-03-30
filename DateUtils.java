package com.studyplanner.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class DateUtils {

    private DateUtils() {}

    /** Returns fractional days remaining until the deadline from now. */
    public static double daysUntil(LocalDateTime deadline) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), deadline);
        return minutes / 1440.0; // 1440 minutes in a day
    }

    public static boolean isOverdue(LocalDateTime deadline) {
        return deadline.isBefore(LocalDateTime.now());
    }

    /** Human-readable deadline label, e.g. "Due in 2 days" or "OVERDUE". */
    public static String deadlineLabel(LocalDateTime deadline) {
        double days = daysUntil(deadline);
        if (days < 0)    return "OVERDUE";
        if (days < 1)    return String.format("Due in %.0f hours", days * 24);
        if (days < 2)    return "Due tomorrow";
        return String.format("Due in %.0f days", Math.ceil(days));
    }
}
