package com.studyplanner.service;

import com.studyplanner.model.Task;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Stack-based undo/redo manager.
 * Each state snapshot is a deep-enough copy of the task list.
 * We store copies of task IDs + mutable fields — not the live Task objects.
 */
public class UndoRedoManager {

    private final Deque<List<TaskSnapshot>> undoStack = new LinkedList<>();
    private final Deque<List<TaskSnapshot>> redoStack = new LinkedList<>();
    private static final int MAX_HISTORY = 50;

    public void saveState(List<Task> tasks) {
        undoStack.push(snapshot(tasks));
        redoStack.clear();
        if (undoStack.size() > MAX_HISTORY) {
            ((LinkedList<List<TaskSnapshot>>) undoStack).removeLast();
        }
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    /** Restores the previous state; caller must provide current state for redo. */
    public List<Task> undo(List<Task> current) {
        if (!canUndo()) return current;
        redoStack.push(snapshot(current));
        return restore(undoStack.pop());
    }

    public List<Task> redo(List<Task> current) {
        if (!canRedo()) return current;
        undoStack.push(snapshot(current));
        return restore(redoStack.pop());
    }

    // ── Snapshot helpers ─────────────────────────────────────────────────────

    private List<TaskSnapshot> snapshot(List<Task> tasks) {
        List<TaskSnapshot> snaps = new ArrayList<>();
        for (Task t : tasks) snaps.add(new TaskSnapshot(t));
        return snaps;
    }

    private List<Task> restore(List<TaskSnapshot> snapshots) {
        List<Task> tasks = new ArrayList<>();
        for (TaskSnapshot s : snapshots) tasks.add(s.toTask());
        return tasks;
    }

    // Lightweight inner record of task state
    private static class TaskSnapshot {
        final String id, title, description, status;
        final java.time.LocalDateTime deadline;
        final int priority;
        final double estimatedTime;

        TaskSnapshot(Task t) {
            id            = t.getId();
            title         = t.getTitle();
            description   = t.getDescription();
            deadline      = t.getDeadline();
            priority      = t.getPriority();
            estimatedTime = t.getEstimatedTime();
            status        = t.getStatus().name();
        }

        Task toTask() {
            return new Task(id, title, description, deadline, priority, estimatedTime,
                    Task.Status.valueOf(status));
        }
    }
}
