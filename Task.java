package com.studyplanner.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    public enum Status { PENDING, COMPLETED }

    private final String id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private int priority;          // 1–5
    private double estimatedTime;  // hours
    private Status status;
    private double urgencyScore;   // computed, not persisted directly

    public Task(String title, String description, LocalDateTime deadline,
                int priority, double estimatedTime) {
        this.id            = UUID.randomUUID().toString();
        this.title         = title;
        this.description   = description;
        this.deadline      = deadline;
        this.priority      = priority;
        this.estimatedTime = estimatedTime;
        this.status        = Status.PENDING;
    }

    // Full constructor used by repository when loading from file
    public Task(String id, String title, String description, LocalDateTime deadline,
                int priority, double estimatedTime, Status status) {
        this.id            = id;
        this.title         = title;
        this.description   = description;
        this.deadline      = deadline;
        this.priority      = priority;
        this.estimatedTime = estimatedTime;
        this.status        = status;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String        getId()            { return id; }
    public String        getTitle()         { return title; }
    public String        getDescription()   { return description; }
    public LocalDateTime getDeadline()      { return deadline; }
    public int           getPriority()      { return priority; }
    public double        getEstimatedTime() { return estimatedTime; }
    public Status        getStatus()        { return status; }
    public double        getUrgencyScore()  { return urgencyScore; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setTitle(String title)               { this.title = title; }
    public void setDescription(String description)   { this.description = description; }
    public void setDeadline(LocalDateTime deadline)  { this.deadline = deadline; }
    public void setPriority(int priority)            { this.priority = priority; }
    public void setEstimatedTime(double estimatedTime){ this.estimatedTime = estimatedTime; }
    public void setStatus(Status status)             { this.status = status; }
    public void setUrgencyScore(double score)        { this.urgencyScore = score; }

    public boolean isCompleted() { return status == Status.COMPLETED; }
    public boolean isPending()   { return status == Status.PENDING; }

    @Override
    public String toString() {
        return String.format("Task{id='%s', title='%s', status=%s, score=%.2f}",
                id, title, status, urgencyScore);
    }
}
