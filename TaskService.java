package com.studyplanner.service;

import com.studyplanner.model.Task;
import com.studyplanner.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Central service layer.  All UI actions funnel through here.
 * Coordinates: repository, scoring, undo/redo, and in-memory task list.
 */
public class TaskService {

    private final TaskRepository    repository;
    private final ScoringEngine     scoringEngine;
    private final UndoRedoManager   undoRedoManager;

    private List<Task> tasks; // in-memory working set

    public TaskService(TaskRepository repository, ScoringEngine scoringEngine,
                       UndoRedoManager undoRedoManager) {
        this.repository      = repository;
        this.scoringEngine   = scoringEngine;
        this.undoRedoManager = undoRedoManager;
        this.tasks           = new ArrayList<>(repository.findAll());
        scoringEngine.scoreAndSort(this.tasks);
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public void addTask(Task task) {
        undoRedoManager.saveState(tasks);
        tasks.add(task);
        refresh();
        repository.save(task);
    }

    public void deleteTask(String taskId) {
        undoRedoManager.saveState(tasks);
        tasks.removeIf(t -> t.getId().equals(taskId));
        refresh();
        repository.delete(taskId);
    }

    public void completeTask(String taskId) {
        undoRedoManager.saveState(tasks);
        findById(taskId).ifPresent(t -> {
            t.setStatus(Task.Status.COMPLETED);
            refresh();
            repository.update(t);
        });
    }

    public Optional<Task> findById(String id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getPendingTasks() {
        return tasks.stream().filter(Task::isPending).collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return tasks.stream().filter(Task::isCompleted).collect(Collectors.toList());
    }

    public List<Task> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllTasks();
        String lower = keyword.toLowerCase();
        return tasks.stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lower)
                          || t.getDescription().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ── Undo / Redo ───────────────────────────────────────────────────────────

    public boolean canUndo() { return undoRedoManager.canUndo(); }
    public boolean canRedo() { return undoRedoManager.canRedo(); }

    public void undo() {
        tasks = new ArrayList<>(undoRedoManager.undo(tasks));
        refresh();
        repository.persistAll(tasks);
    }

    public void redo() {
        tasks = new ArrayList<>(undoRedoManager.redo(tasks));
        refresh();
        repository.persistAll(tasks);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    /** Re-score and re-sort the in-memory list. */
    private void refresh() {
        scoringEngine.scoreAndSort(tasks);
    }
}
