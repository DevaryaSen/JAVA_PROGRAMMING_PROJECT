package com.studyplanner.service;

import com.studyplanner.model.DailySchedule;
import com.studyplanner.model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates a daily study schedule from a scored task list.
 * Tasks are packed greedily by urgency score until available hours are consumed.
 * Returns a conflict flag if not all tasks fit.
 */
public class ScheduleGenerator {

    private final ScoringEngine scoringEngine;

    public ScheduleGenerator(ScoringEngine scoringEngine) {
        this.scoringEngine = scoringEngine;
    }

    public DailySchedule generate(List<Task> allTasks, double availableHoursPerDay) {
        List<Task> pending = allTasks.stream()
                .filter(Task::isPending)
                .collect(Collectors.toList());

        scoringEngine.scoreAndSort(pending);

        DailySchedule schedule = new DailySchedule(LocalDate.now(), availableHoursPerDay);
        for (Task task : pending) {
            schedule.addTask(task);
            if (schedule.getRemainingHours() <= 0) break;
        }
        return schedule;
    }

    /** Detects tasks whose combined estimated time exceeds a given threshold. */
    public boolean detectConflict(List<Task> tasks, double dailyCapacityHours) {
        double total = tasks.stream()
                .filter(Task::isPending)
                .mapToDouble(Task::getEstimatedTime)
                .sum();
        return total > dailyCapacityHours;
    }
}
