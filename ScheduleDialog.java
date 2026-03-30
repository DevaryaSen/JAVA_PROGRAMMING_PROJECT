package com.studyplanner.ui;

import com.studyplanner.model.DailySchedule;
import com.studyplanner.model.Task;
import com.studyplanner.service.ScheduleGenerator;
import com.studyplanner.service.ScoringEngine;
import com.studyplanner.service.TaskService;
import com.studyplanner.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ScheduleDialog extends JDialog {

    public ScheduleDialog(Frame owner, TaskService taskService) {
        super(owner, "Daily Schedule Generator", true);
        buildUI(taskService);
        pack();
        setMinimumSize(new Dimension(520, 420));
        setLocationRelativeTo(owner);
    }

    private void buildUI(TaskService taskService) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Hours input
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputRow.add(new JLabel("Available study hours today:"));
        SpinnerNumberModel model = new SpinnerNumberModel(6.0, 0.5, 24.0, 0.5);
        JSpinner hoursSpinner = new JSpinner(model);
        inputRow.add(hoursSpinner);

        JButton generateBtn = new JButton("Generate Schedule");
        generateBtn.setBackground(new Color(70, 130, 180));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);
        inputRow.add(generateBtn);

        JTextArea output = new JTextArea();
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setEditable(false);
        output.setLineWrap(true);

        generateBtn.addActionListener(e -> {
            double hours = (double) hoursSpinner.getValue();
            ScheduleGenerator gen = new ScheduleGenerator(new ScoringEngine());
            DailySchedule schedule = gen.generate(taskService.getAllTasks(), hours);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("📅 Schedule for Today  |  Available: %.1fh%n%n", hours));

            if (schedule.getScheduledTasks().isEmpty()) {
                sb.append("No pending tasks to schedule. Enjoy your day! 🎉");
            } else {
                int slot = 1;
                for (Task t : schedule.getScheduledTasks()) {
                    sb.append(String.format("[%d] %s%n", slot++, t.getTitle()));
                    sb.append(String.format("    Priority: %s  |  Est: %.1fh  |  %s%n",
                            "★".repeat(t.getPriority()),
                            t.getEstimatedTime(),
                            DateUtils.deadlineLabel(t.getDeadline())));
                    sb.append(String.format("    Score: %.1f%n%n", t.getUrgencyScore()));
                }
                sb.append(String.format("─────────────────────────────%n"));
                sb.append(String.format("Scheduled: %.1fh / %.1fh%n",
                        schedule.getTotalScheduledHours(), hours));
                sb.append(String.format("Remaining: %.1fh%n", schedule.getRemainingHours()));

                if (schedule.hasConflict()) {
                    sb.append("\n⚠️  Some tasks didn't fit today — consider splitting them.");
                }
            }

            output.setText(sb.toString());
        });

        root.add(inputRow, BorderLayout.NORTH);
        root.add(new JScrollPane(output), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(close);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
