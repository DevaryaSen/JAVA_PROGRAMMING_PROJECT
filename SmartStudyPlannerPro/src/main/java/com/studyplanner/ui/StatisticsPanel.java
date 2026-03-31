package com.studyplanner.ui;

import com.studyplanner.model.Task;
import com.studyplanner.service.StatisticsService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/** Compact statistics bar shown at the top of the main window. */
public class StatisticsPanel extends JPanel {

    private final StatisticsService stats = new StatisticsService();

    private final JLabel pendingLabel    = stat("0");
    private final JLabel completedLabel  = stat("0");
    private final JLabel overdueLabel    = stat("0");
    private final JLabel hoursLabel      = stat("0.0");
    private final JLabel rateLabel       = stat("0%");

    public StatisticsPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 8));
        setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            new EmptyBorder(4, 10, 4, 10)
        ));
        setBackground(new Color(245, 248, 255));

        add(card("📋 Pending",    pendingLabel));
        add(card("✅ Completed",  completedLabel));
        add(card("⚠️ Overdue",   overdueLabel));
        add(card("⏱ Hours Left",  hoursLabel));
        add(card("📈 Done Rate",  rateLabel));
    }

    public void refresh(List<Task> tasks) {
        pendingLabel.setText(String.valueOf(stats.countPending(tasks)));
        completedLabel.setText(String.valueOf(stats.countCompleted(tasks)));
        overdueLabel.setText(String.valueOf(stats.countOverdue(tasks)));
        hoursLabel.setText(String.format("%.1fh", stats.totalEstimatedHours(tasks)));
        rateLabel.setText(String.format("%.0f%%", stats.completionRate(tasks)));

        // Highlight overdue in red if non-zero
        long overdue = stats.countOverdue(tasks);
        overdueLabel.setForeground(overdue > 0 ? new Color(200, 0, 0) : new Color(50, 50, 50));
    }

    private JPanel card(String label, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(4, 2));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 210, 230), 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(Color.GRAY);

        value.setFont(new Font("SansSerif", Font.BOLD, 18));
        value.setForeground(new Color(40, 40, 80));
        value.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lbl, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private static JLabel stat(String initial) {
        return new JLabel(initial);
    }
}
