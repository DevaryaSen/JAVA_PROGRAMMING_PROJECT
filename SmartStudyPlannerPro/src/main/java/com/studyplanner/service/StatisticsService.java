package com.studyplanner.service;

import com.studyplanner.model.Task;

import java.util.List;

public class StatisticsService {

    public long countCompleted(List<Task> tasks) {
        return tasks.stream().filter(Task::isCompleted).count();
    }

    public long countPending(List<Task> tasks) {
        return tasks.stream().filter(Task::isPending).count();
    }

    public double totalEstimatedHours(List<Task> tasks) {
        return tasks.stream().filter(Task::isPending)
                .mapToDouble(Task::getEstimatedTime).sum();
    }

    public long countOverdue(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isPending)
                .filter(t -> com.studyplanner.utils.DateUtils.isOverdue(t.getDeadline()))
                .count();
    }

    public double completionRate(List<Task> tasks) {
        if (tasks.isEmpty()) return 0;
        return (double) countCompleted(tasks) / tasks.size() * 100.0;
    }
}
