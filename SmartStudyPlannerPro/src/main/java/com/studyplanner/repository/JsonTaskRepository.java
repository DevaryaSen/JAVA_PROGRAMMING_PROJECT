package com.studyplanner.repository;

import com.studyplanner.model.Task;
import com.studyplanner.utils.JsonSerializer;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * File-backed repository that persists tasks to a JSON file.
 * All reads/writes go through JsonSerializer so the format stays consistent.
 */
public class JsonTaskRepository implements TaskRepository {

    private static final Logger LOG = Logger.getLogger(JsonTaskRepository.class.getName());

    private final Path filePath;

    public JsonTaskRepository(String dataDirectory) {
        Path dir = Paths.get(dataDirectory);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            LOG.severe("Cannot create data directory: " + e.getMessage());
        }
        this.filePath = dir.resolve("tasks.json");
    }

    @Override
    public List<Task> findAll() {
        if (!Files.exists(filePath)) return new ArrayList<>();
        try {
            String json = new String(Files.readAllBytes(filePath));
            return JsonSerializer.deserializeTasks(json);
        } catch (IOException e) {
            LOG.warning("Could not read tasks file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void save(Task task) {
        List<Task> tasks = findAll();
        tasks.add(task);
        writeAll(tasks);
    }

    @Override
    public void update(Task task) {
        List<Task> tasks = findAll();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                break;
            }
        }
        writeAll(tasks);
    }

    @Override
    public void delete(String taskId) {
        List<Task> tasks = findAll();
        tasks.removeIf(t -> t.getId().equals(taskId));
        writeAll(tasks);
    }

    @Override
    public void persistAll(List<Task> tasks) {
        writeAll(tasks);
    }

    private void writeAll(List<Task> tasks) {
        try {
            String json = JsonSerializer.serializeTasks(tasks);
            Files.write(filePath, json.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOG.severe("Could not write tasks file: " + e.getMessage());
        }
    }
}
