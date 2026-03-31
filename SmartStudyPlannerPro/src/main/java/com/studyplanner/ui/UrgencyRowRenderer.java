package com.studyplanner.ui;

import com.studyplanner.model.Task;
import com.studyplanner.utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renders table rows with colour based on urgency:
 *   - Overdue          → red
 *   - Due within 1 day → orange
 *   - High score       → yellow
 *   - Completed        → grey / strikethrough-like
 */
public class UrgencyRowRenderer extends DefaultTableCellRenderer {

    private static final Color OVERDUE_BG   = new Color(255, 180, 180);
    private static final Color URGENT_BG    = new Color(255, 220, 150);
    private static final Color HIGH_BG      = new Color(255, 255, 180);
    private static final Color COMPLETED_BG = new Color(220, 220, 220);
    private static final Color SELECTED_BG  = new Color(173, 216, 230);

    private final TaskTableModel model;

    public UrgencyRowRenderer(TaskTableModel model) {
        this.model = model;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            c.setBackground(SELECTED_BG);
            c.setForeground(Color.BLACK);
            return c;
        }

        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= model.getRowCount()) return c;

        Task task = model.getTaskAt(modelRow);
        c.setForeground(Color.BLACK);

        if (task.isCompleted()) {
            c.setBackground(COMPLETED_BG);
            c.setForeground(Color.GRAY);
        } else if (DateUtils.isOverdue(task.getDeadline())) {
            c.setBackground(OVERDUE_BG);
        } else if (DateUtils.daysUntil(task.getDeadline()) < 1) {
            c.setBackground(URGENT_BG);
        } else if (task.getPriority() >= 4) {
            c.setBackground(HIGH_BG);
        } else {
            c.setBackground(Color.WHITE);
        }

        return c;
    }
}
