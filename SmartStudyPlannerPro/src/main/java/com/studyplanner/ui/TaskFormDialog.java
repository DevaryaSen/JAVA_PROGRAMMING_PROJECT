package com.studyplanner.ui;

import com.studyplanner.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Modal dialog for creating a new task.
 * Returns a Task or null if cancelled.
 */
public class TaskFormDialog extends JDialog {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Task result = null;

    private final JTextField     titleField       = new JTextField(25);
    private final JTextArea      descArea         = new JTextArea(3, 25);
    private final JTextField     deadlineField    = new JTextField("2025-12-31 23:59");
    private final JSpinner       prioritySpinner;
    private final JSpinner       hoursSpinner;

    public TaskFormDialog(Frame owner) {
        super(owner, "Add New Task", true);

        SpinnerNumberModel priorityModel = new SpinnerNumberModel(3, 1, 5, 1);
        SpinnerNumberModel hoursModel    = new SpinnerNumberModel(1.0, 0.5, 100.0, 0.5);
        prioritySpinner = new JSpinner(priorityModel);
        hoursSpinner    = new JSpinner(hoursModel);

        buildUI();
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void buildUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 10, 15));

        content.add(buildForm(), BorderLayout.CENTER);
        content.add(buildButtons(), BorderLayout.SOUTH);

        setContentPane(content);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 4, 4, 4);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(form, gbc, row++, "Title *",           titleField);
        addRow(form, gbc, row++, "Description",       new JScrollPane(descArea));
        addRow(form, gbc, row++, "Deadline (yyyy-MM-dd HH:mm) *", deadlineField);
        addRow(form, gbc, row++, "Priority (1–5)",    prioritySpinner);
        addRow(form, gbc, row,   "Estimated Hours",   hoursSpinner);

        return form;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton save   = new JButton("Save Task");
        JButton cancel = new JButton("Cancel");

        save.setBackground(new Color(70, 130, 180));
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);

        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());

        buttons.add(cancel);
        buttons.add(save);
        return buttons;
    }

    private void onSave() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Title is required.");
            return;
        }

        LocalDateTime deadline;
        try {
            deadline = LocalDateTime.parse(deadlineField.getText().trim(), DTF);
        } catch (DateTimeParseException ex) {
            showError("Deadline must be in format: yyyy-MM-dd HH:mm");
            return;
        }

        int    priority      = (int) prioritySpinner.getValue();
        double estimatedTime = (double) hoursSpinner.getValue();
        String description   = descArea.getText().trim();

        result = new Task(title, description, deadline, priority, estimatedTime);
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Returns the created task, or null if the dialog was cancelled. */
    public Task getResult() { return result; }
}
