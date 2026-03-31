package com.studyplanner.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Represents a generated study schedule for a single day. */
public class DailySchedule {

    private final LocalDate date;
    private final double availableHours;
    private final List<Task> scheduledTasks;
    private double totalScheduledHours;
    private boolean hasConflict;

    public DailySchedule(LocalDate date, double availableHours) {
        this.date            = date;
        this.availableHours  = availableHours;
        this.scheduledTasks  = new ArrayList<>();
        this.totalScheduledHours = 0;
        this.hasConflict     = false;
    }

    public boolean addTask(Task task) {
        double remaining = availableHours - totalScheduledHours;
        if (task.getEstimatedTime() <= remaining) {
            scheduledTasks.add(task);
            totalScheduledHours += task.getEstimatedTime();
            return true;
        }
        hasConflict = true;
        return false;
    }

    public LocalDate        getDate()               { return date; }
    public double           getAvailableHours()     { return availableHours; }
    public List<Task>       getScheduledTasks()     { return Collections.unmodifiableList(scheduledTasks); }
    public double           getTotalScheduledHours(){ return totalScheduledHours; }
    public double           getRemainingHours()     { return availableHours - totalScheduledHours; }
    public boolean          hasConflict()           { return hasConflict; }
}
