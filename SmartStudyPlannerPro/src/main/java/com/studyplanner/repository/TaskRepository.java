package com.studyplanner.repository;

import com.studyplanner.model.Task;
import java.util.List;

public interface TaskRepository {
    void   save(Task task);
    void   update(Task task);
    void   delete(String taskId);
    List<Task> findAll();
    void   persistAll(List<Task> tasks);
}
