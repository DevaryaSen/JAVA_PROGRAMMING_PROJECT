package com.studyplanner.ui;

import com.studyplanner.model.Task;
import com.studyplanner.service.TaskService;
import com.studyplanner.utils.CsvExporter;
import com.studyplanner.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class MainWindow extends JFrame {

    private final TaskService      taskService;
    private final TaskTableModel   tableModel;
    private final JTable           table;
    private final StatisticsPanel  statsPanel;
    private final JTextField       searchField;
    private final JComboBox<String> filterCombo;

    // Track which tasks are showing (post-filter/search)
    private List<Task> displayedTasks;

    public MainWindow(TaskService taskService) {
        super("Smart Study Planner Pro");
        this.taskService = taskService;

        tableModel     = new TaskTableModel();
        table          = buildTable();
        statsPanel     = new StatisticsPanel();
        searchField    = new JTextField(20);
        filterCombo    = new JComboBox<>(new String[]{"All Tasks", "Pending", "Completed"});

        buildFrame();
        refreshDisplay();
        startDeadlineWatcher();
    }

    // ── Frame construction ────────────────────────────────────────────────────

    private void buildFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(950, 600));
        setLayout(new BorderLayout());

        setJMenuBar(buildMenuBar());
        add(statsPanel,      BorderLayout.NORTH);
        add(buildToolbar(),  BorderLayout.NORTH);   // replaces statsPanel slot
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        // Re-stack: stats above toolbar
        JPanel topArea = new JPanel(new BorderLayout());
        topArea.add(statsPanel, BorderLayout.NORTH);
        topArea.add(buildToolbar(), BorderLayout.SOUTH);
        getContentPane().removeAll();
        add(topArea, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem exportCsv = new JMenuItem("Export to CSV…");
        exportCsv.addActionListener(e -> exportCsv());
        file.add(exportCsv);
        file.addSeparator();
        file.add(new JMenuItem("Exit")).addActionListener(e -> System.exit(0));

        JMenu edit = new JMenu("Edit");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem redo = new JMenuItem("Redo");
        undo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
        redo.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
        undo.addActionListener(e -> performUndo());
        redo.addActionListener(e -> performRedo());
        edit.add(undo);
        edit.add(redo);

        JMenu tools = new JMenu("Tools");
        JMenuItem scheduleItem = new JMenuItem("Daily Schedule Generator…");
        scheduleItem.addActionListener(e -> new ScheduleDialog(this, taskService).setVisible(true));
        tools.add(scheduleItem);

        bar.add(file);
        bar.add(edit);
        bar.add(tools);
        return bar;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bar.setBackground(new Color(240, 244, 255));
        bar.setBorder(new EmptyBorder(0, 5, 0, 5));

        // Action buttons
        bar.add(styledButton("➕ Add Task",    new Color(60, 160, 60),  e -> showAddDialog()));
        bar.add(styledButton("✅ Complete",     new Color(70, 130, 180), e -> completeSelected()));
        bar.add(styledButton("🗑 Delete",       new Color(200, 60, 60),  e -> deleteSelected()));
        bar.add(styledButton("↩ Undo",         new Color(130, 130, 130), e -> performUndo()));
        bar.add(styledButton("↪ Redo",         new Color(130, 130, 130), e -> performRedo()));

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // Filter
        bar.add(new JLabel("Show:"));
        filterCombo.addActionListener(e -> refreshDisplay());
        bar.add(filterCombo);

        // Search
        bar.add(new JLabel("🔍"));
        searchField.setToolTipText("Search by title or description");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { refreshDisplay(); }
            public void removeUpdate(DocumentEvent e)  { refreshDisplay(); }
            public void changedUpdate(DocumentEvent e) { refreshDisplay(); }
        });
        bar.add(searchField);

        return bar;
    }

    private JLabel buildBottomBar() {
        JLabel hint = new JLabel(
            "  Color legend:  🔴 Overdue   🟠 Due today   🟡 High priority   ⬜ Normal   ⬛ Completed");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(Color.DARK_GRAY);
        hint.setBorder(new EmptyBorder(4, 8, 6, 8));
        return hint;
    }

    private JTable buildTable() {
        JTable t = new JTable(tableModel);
        t.setRowHeight(28);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setAutoCreateRowSorter(true);
        t.setFillsViewportHeight(true);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));

        UrgencyRowRenderer renderer = new UrgencyRowRenderer(tableModel);
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Column widths
        int[] widths = {35, 220, 80, 130, 80, 90, 100, 110};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(50, 80, 140));
        t.getTableHeader().setForeground(Color.WHITE);

        return t;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void showAddDialog() {
        TaskFormDialog dialog = new TaskFormDialog(this);
        dialog.setVisible(true);
        Task task = dialog.getResult();
        if (task != null) {
            taskService.addTask(task);
            refreshDisplay();
        }
    }

    private void completeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showInfo("Select a task to mark complete."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        Task task = tableModel.getTaskAt(modelRow);
        if (task.isCompleted()) { showInfo("Task is already completed."); return; }
        taskService.completeTask(task.getId());
        refreshDisplay();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showInfo("Select a task to delete."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        Task task = tableModel.getTaskAt(modelRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete task: \"" + task.getTitle() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            taskService.deleteTask(task.getId());
            refreshDisplay();
        }
    }

    private void performUndo() {
        if (!taskService.canUndo()) { showInfo("Nothing to undo."); return; }
        taskService.undo();
        refreshDisplay();
    }

    private void performRedo() {
        if (!taskService.canRedo()) { showInfo("Nothing to redo."); return; }
        taskService.redo();
        refreshDisplay();
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("tasks_export.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CsvExporter.export(taskService.getAllTasks(),
                        chooser.getSelectedFile().getAbsolutePath());
                showInfo("Tasks exported successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Display refresh ───────────────────────────────────────────────────────

    private void refreshDisplay() {
        String keyword = searchField.getText().trim();
        String filter  = (String) filterCombo.getSelectedItem();

        List<Task> tasks = keyword.isEmpty()
                ? taskService.getAllTasks()
                : taskService.search(keyword);

        if ("Pending".equals(filter)) {
            tasks = tasks.stream().filter(Task::isPending)
                    .collect(java.util.stream.Collectors.toList());
        } else if ("Completed".equals(filter)) {
            tasks = tasks.stream().filter(Task::isCompleted)
                    .collect(java.util.stream.Collectors.toList());
        }

        displayedTasks = tasks;
        tableModel.setTasks(tasks);
        statsPanel.refresh(taskService.getAllTasks());
    }

    // ── Deadline watcher ──────────────────────────────────────────────────────

    private void startDeadlineWatcher() {
        Timer timer = new Timer(60_000, e -> checkDeadlineWarnings());
        timer.setInitialDelay(3000);
        timer.start();
    }

    private void checkDeadlineWarnings() {
        taskService.getPendingTasks().stream()
            .filter(t -> {
                double days = DateUtils.daysUntil(t.getDeadline());
                return days >= 0 && days < 1;
            })
            .forEach(t -> {
                String msg = String.format(
                    "⚠️ Task \"%s\" is due within 24 hours!%n%s",
                    t.getTitle(), DateUtils.deadlineLabel(t.getDeadline()));
                JOptionPane.showMessageDialog(this, msg,
                        "Deadline Warning", JOptionPane.WARNING_MESSAGE);
            });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton styledButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btn.addActionListener(action);
        return btn;
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
