import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Task class definition
class Task {
    String name;
    int duration;
    List<Task> dependencies = new ArrayList<>();
    int earlyStart = -1;
    int earlyFinish = -1;
    int lateStart = -1;
    int lateFinish = -1;
    int slack = -1;
    boolean isCritical = false;

    Task(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }
}

// CPM GUI Class
public class CPMGUInew extends JFrame {
    private JTextField numTasksField;
    private JButton submitButton;
    private JTextField[] taskNameField;
    private JTextField[] dependencyFields;
    private JTextField[] durationFields;

    public CPMGUInew() {
        setTitle("CPM Tasks Input");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel for Number of Tasks Input
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Number of Tasks:"));
        numTasksField = new JTextField(5);
        inputPanel.add(numTasksField);
        add(inputPanel, BorderLayout.NORTH);

        // Submit Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitButtonListener());
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int numTasks = Integer.parseInt(numTasksField.getText());
                if (numTasks <= 0) {
                    JOptionPane.showMessageDialog(CPMGUInew.this, "Number of tasks must be greater than 0.");
                    return;
                }

                JPanel taskInputPanel = new JPanel(new GridLayout(numTasks, 3));
                taskNameField = new JTextField[numTasks];
                durationFields = new JTextField[numTasks];
                dependencyFields = new JTextField[numTasks];

                for (int i = 0; i < numTasks; i++) {
                    taskInputPanel.add(new JLabel("Task " + (i + 1) + " Name:"));
                    taskNameField[i] = new JTextField(10);
                    taskInputPanel.add(taskNameField[i]);

                    taskInputPanel.add(new JLabel("Duration:"));
                    durationFields[i] = new JTextField(5);
                    taskInputPanel.add(durationFields[i]);

                    taskInputPanel.add(new JLabel("Dependencies (comma-separated):"));
                    dependencyFields[i] = new JTextField(15);
                    taskInputPanel.add(dependencyFields[i]);
                }

                add(new JScrollPane(taskInputPanel), BorderLayout.CENTER);

                JButton calculateButton = new JButton("Calculate");
                calculateButton.addActionListener(new CalculateButtonListener());
                add(calculateButton, BorderLayout.SOUTH);

                revalidate();
                repaint();
                submitButton.setEnabled(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CPMGUInew.this, "Please enter a valid number of tasks.");
            }
        }
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<Task> tasks = new ArrayList<>();
                for (int i = 0; i < taskNameField.length; i++) {
                    String taskName = taskNameField[i].getText().trim();
                    int taskDuration = Integer.parseInt(durationFields[i].getText().trim());
                    tasks.add(new Task(taskName, taskDuration));
                }

                for (int i = 0; i < dependencyFields.length; i++) {
                    String[] dependencies = dependencyFields[i].getText().split(",");
                    for (String dependencyName : dependencies) {
                        String trimmedName = dependencyName.trim();
                        if (!trimmedName.isEmpty()) {
                            Task dependency = tasks.stream()
                                    .filter(t -> t.name.equals(trimmedName))
                                    .findFirst()
                                    .orElse(null);
                            if (dependency == null) {
                                JOptionPane.showMessageDialog(CPMGUInew.this,
                                        "Dependency \"" + trimmedName + "\" not found for Task " +
                                                taskNameField[i].getText());
                                return;
                            }
                            tasks.get(i).dependencies.add(dependency);
                        }
                    }
                }

                calculateCPM(tasks);

                JFrame resultFrame = new JFrame("CPM Results");
                resultFrame.setSize(600, 400);
                resultFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                JTextArea resultTextArea = new JTextArea();
                resultTextArea.setEditable(false);
                resultTextArea.append("Task\tDuration\tES\tEF\tLS\tLF\tSlack\n");
                for (Task task : tasks) {
                    resultTextArea.append(task.name + "\t" + task.duration + "\t" + task.earlyStart + "\t" +
                            task.earlyFinish + "\t" + task.lateStart + "\t" + task.lateFinish + "\t" + task.slack
                            + "\n");
                }

                resultFrame.add(new JScrollPane(resultTextArea));
                resultFrame.setVisible(true);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CPMGUInew.this, "Please ensure all durations are valid integers.");
            }
        }
    }

    // Method to Calculate CPM
    public static void calculateCPM(List<Task> tasks) {
        // Forward pass
        for (Task task : tasks) {
            int maxEF = 0;
            for (Task dependency : task.dependencies) {
                maxEF = Math.max(maxEF, dependency.earlyFinish);
            }
            task.earlyStart = maxEF;
            task.earlyFinish = task.earlyStart + task.duration;
        }

        // Backward pass
        int projectDuration = tasks.get(tasks.size() - 1).earlyFinish;
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            if (i == tasks.size() - 1) {
                task.lateFinish = projectDuration;
                task.lateStart = task.lateFinish - task.duration;
            } else {
                int minLS = Integer.MAX_VALUE;
                for (Task dependentTask : tasks) {
                    if (dependentTask.dependencies.contains(task)) {
                        minLS = Math.min(minLS, dependentTask.lateStart);
                    }
                }
                task.lateFinish = minLS;
                task.lateStart = task.lateFinish - task.duration;
            }
            task.slack = task.lateFinish - task.earlyFinish;
            task.isCritical = (task.slack == 0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CPMGUInew::new);
    }
}
