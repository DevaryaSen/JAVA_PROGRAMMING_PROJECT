package com.studyplanner.utils;

import com.studyplanner.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CsvExporter {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private CsvExporter() {}

    public static void export(List<Task> tasks, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Title,Description,Deadline,Priority,EstimatedHours,Status,UrgencyScore\n");
        for (Task t : tasks) {
            sb.append(csvField(t.getId())).append(",")
              .append(csvField(t.getTitle())).append(",")
              .append(csvField(t.getDescription())).append(",")
              .append(t.getDeadline().format(DTF)).append(",")
              .append(t.getPriority()).append(",")
              .append(String.format("%.1f", t.getEstimatedTime())).append(",")
              .append(t.getStatus().name()).append(",")
              .append(String.format("%.2f", t.getUrgencyScore())).append("\n");
        }
        Files.write(Paths.get(filePath), sb.toString().getBytes());
    }

    private static String csvField(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
