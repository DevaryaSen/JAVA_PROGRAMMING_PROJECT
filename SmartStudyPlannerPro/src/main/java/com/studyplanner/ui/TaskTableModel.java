package com.studyplanner.ui;

import com.studyplanner.model.Task;
import com.studyplanner.utils.DateUtils;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
        "#", "Title", "Priority", "Deadline", "Est. Hours", "Status", "Urgency Score", "Due"
    };
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

    private List<Task> tasks = new ArrayList<>();

    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        fireTableDataChanged();
    }

    public Task getTaskAt(int row) {
        return tasks.get(row);
    }

    @Override public int getRowCount()    { return tasks.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Task t = tasks.get(row);
        return switch (col) {
            case 0 -> row + 1;
            case 1 -> t.getTitle();
            case 2 -> "★".repeat(t.getPriority());
            case 3 -> t.getDeadline().format(DTF);
            case 4 -> String.format("%.1fh", t.getEstimatedTime());
            case 5 -> t.getStatus().name();
            case 6 -> String.format("%.1f", t.getUrgencyScore());
            case 7 -> DateUtils.deadlineLabel(t.getDeadline());
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return col == 0 ? Integer.class : String.class;
    }
}
