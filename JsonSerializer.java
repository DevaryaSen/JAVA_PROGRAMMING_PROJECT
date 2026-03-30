package com.studyplanner.utils;

import com.studyplanner.model.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal hand-rolled JSON serializer/deserializer for Task objects.
 * No external libraries required.
 */
public final class JsonSerializer {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private JsonSerializer() {}

    public static String serializeTasks(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(serializeTask(tasks.get(i)));
            if (i < tasks.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String serializeTask(Task t) {
        return String.format(
            "  {\"id\":\"%s\",\"title\":%s,\"description\":%s," +
            "\"deadline\":\"%s\",\"priority\":%d," +
            "\"estimatedTime\":%.2f,\"status\":\"%s\"}",
            escape(t.getId()),
            jsonString(t.getTitle()),
            jsonString(t.getDescription()),
            t.getDeadline().format(DTF),
            t.getPriority(),
            t.getEstimatedTime(),
            t.getStatus().name()
        );
    }

    public static List<Task> deserializeTasks(String json) {
        List<Task> tasks = new ArrayList<>();
        json = json.trim();
        if (json.isEmpty() || json.equals("[]")) return tasks;

        // Strip outer array brackets
        json = json.substring(1, json.lastIndexOf(']'));

        // Split on object boundaries
        List<String> objects = splitObjects(json);
        for (String obj : objects) {
            if (obj.isBlank()) continue;
            try {
                tasks.add(parseTask(obj.trim()));
            } catch (Exception e) {
                // Skip malformed entries; log is acceptable here
                System.err.println("Skipping malformed task entry: " + e.getMessage());
            }
        }
        return tasks;
    }

    private static Task parseTask(String obj) {
        String id            = extractString(obj, "id");
        String title         = extractString(obj, "title");
        String description   = extractString(obj, "description");
        String deadlineStr   = extractString(obj, "deadline");
        int    priority      = (int) extractNumber(obj, "priority");
        double estimatedTime = extractNumber(obj, "estimatedTime");
        String statusStr     = extractString(obj, "status");

        LocalDateTime deadline = LocalDateTime.parse(deadlineStr, DTF);
        Task.Status   status   = Task.Status.valueOf(statusStr);

        return new Task(id, title, description, deadline, priority, estimatedTime, status);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String extractString(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        // Skip whitespace
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (json.charAt(start) == '"') {
            // quoted string
            start++;
            StringBuilder sb = new StringBuilder();
            while (start < json.length()) {
                char c = json.charAt(start);
                if (c == '\\' && start + 1 < json.length()) {
                    char next = json.charAt(start + 1);
                    if (next == '"')  { sb.append('"');  start += 2; continue; }
                    if (next == '\\') { sb.append('\\'); start += 2; continue; }
                    if (next == 'n')  { sb.append('\n'); start += 2; continue; }
                }
                if (c == '"') break;
                sb.append(c);
                start++;
            }
            return sb.toString();
        }
        return "";
    }

    private static double extractNumber(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return 0;
        start += pattern.length();
        while (start < json.length() && json.charAt(start) == ' ') start++;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end))
                || json.charAt(end) == '.' || json.charAt(end) == '-')) end++;
        return Double.parseDouble(json.substring(start, end));
    }

    private static List<String> splitObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private static String jsonString(String s) {
        if (s == null) return "\"\"";
        return "\"" + escape(s) + "\"";
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
