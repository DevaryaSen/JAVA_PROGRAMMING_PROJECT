package com.studyplanner;

import com.studyplanner.repository.JsonTaskRepository;
import com.studyplanner.repository.TaskRepository;
import com.studyplanner.service.ScoringEngine;
import com.studyplanner.service.TaskService;
import com.studyplanner.service.UndoRedoManager;
import com.studyplanner.ui.MainWindow;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        // Use system look-and-feel for a native desktop feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Wire up dependencies manually (no framework)
        String dataDir = System.getProperty("user.home") + "/.studyplanner";

        TaskRepository  repository      = new JsonTaskRepository(dataDir);
        ScoringEngine   scoringEngine   = new ScoringEngine();
        UndoRedoManager undoRedoManager = new UndoRedoManager();
        TaskService     taskService     = new TaskService(repository, scoringEngine, undoRedoManager);

        // Launch UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(taskService);
            window.setVisible(true);
        });
    }
}
