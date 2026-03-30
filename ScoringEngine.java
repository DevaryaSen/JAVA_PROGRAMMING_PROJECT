package com.studyplanner.service;

import com.studyplanner.model.Task;
import com.studyplanner.utils.DateUtils;

import java.util.Comparator;
import java.util.List;

/**
 * Computes and applies urgency scores to tasks.
 *
 * Formula:
 *   urgencyScore = (priority * W_PRIORITY)
 *                + (1 / daysLeft * W_URGENCY)
 *                + (estimatedTimeFactor * W_TIME)
 *
 * Overdue tasks receive a large penalty bonus so they always surface to the top.
 */
public class ScoringEngine {

    private static final double W_PRIORITY = 3.0;
    private static final double W_URGENCY  = 10.0;
    private static final double W_TIME     = 1.5;

    // Overdue tasks use this multiplier on their priority weight
    private static final double OVERDUE_BONUS = 50.0;

    // Estimated time factor: shorter tasks get a small boost (easier wins)
    private static final double MAX_REASONABLE_HOURS = 20.0;

    public double computeScore(Task task) {
        double daysLeft = DateUtils.daysUntil(task.getDeadline());

        if (DateUtils.isOverdue(task.getDeadline())) {
            // Overdue: score is heavily boosted; more priority = higher
            return OVERDUE_BONUS * task.getPriority() + Math.abs(daysLeft) * W_URGENCY;
        }

        // Guard against division by zero / very small values
        double urgencyComponent = (daysLeft > 0.01) ? (1.0 / daysLeft) * W_URGENCY : W_URGENCY * 100;

        // Normalised time factor: fewer hours → slightly higher score (easier to start)
        double timeFactor = 1.0 - Math.min(task.getEstimatedTime() / MAX_REASONABLE_HOURS, 1.0);

        return (task.getPriority() * W_PRIORITY)
             + urgencyComponent
             + (timeFactor * W_TIME);
    }

    /** Scores all tasks in-place and sorts list by descending urgency. */
    public void scoreAndSort(List<Task> tasks) {
        tasks.forEach(t -> t.setUrgencyScore(computeScore(t)));
        tasks.sort(Comparator.comparingDouble(Task::getUrgencyScore).reversed());
    }

    /** Comparator for external use (e.g., PriorityQueue). */
    public Comparator<Task> taskComparator() {
        return Comparator.comparingDouble(Task::getUrgencyScore).reversed();
    }
}
