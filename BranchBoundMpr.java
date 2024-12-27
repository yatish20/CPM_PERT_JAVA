import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.Border;



class Job {
    String id;
    int deadline;
    int profit;

    public Job(String id, int deadline, int profit) {
        this.id = id;
        this.deadline = deadline;
        this.profit = profit;
    }
}

class BranchBoundMpr {

    public BranchBoundMpr(){
        JFrame f = new JFrame();
        JLabel l1 = new JLabel();
        f.setTitle("TaskFLow");
        l1.setText("TaskFlow");
        l1.setFont(new Font("Book Antiqua", Font.PLAIN, 60));
        l1.setForeground(Color.WHITE);
        l1.setHorizontalAlignment(JLabel.CENTER);
        l1.setVerticalAlignment(JLabel.CENTER);
        l1.setBounds(50, 50, 400, 100);
        JButton b1 = new JButton();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 500);
        f.getContentPane().setBackground(new Color(0x0e2f39));
        b1.setText("Get Started");
        b1.setBackground(new Color(0xc3ced2));
        b1.setBounds(150, 200, 200, 50);
        f.setLayout(null);
        f.setVisible(true);
        f.add(l1);
        f.add(b1);


        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                f.dispose();
                new SecondFrame();
            }
        });

    }


    public static void main(String[] args) {

        new BranchBoundMpr();

    }
}

class SecondFrame extends JFrame{
    private JFrame f = new JFrame("Second");
    private JTextField jobIdField, deadlineField, profitField;
    private JButton addJobButton, calculateButton;
    JTextArea outputArea;
    private TextArea t1 , t2;
    private DefaultListModel<Job> jobListModel;

    public SecondFrame() {

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("TaskFlow");
        f.setSize(500,500);
        f.setLayout(null);
        f.setVisible(true);

        JPanel p1=new JPanel();
        p1.setSize(500,200);
        p1.setBackground(new Color(0x0077b6));
        f.add(p1);

        p1.setLayout(new GridBagLayout());
        GridBagConstraints gui = new GridBagConstraints();

        gui.insets = new Insets(5, 5, 5, 5);
        gui.anchor = GridBagConstraints.LINE_START;

        gui.gridx = 0;
        gui.gridy = 0;
        p1.add(new JLabel("Job ID:"), gui);
        jobIdField = new JTextField(10);
        gui.gridx = 1;
        p1.add(jobIdField, gui);


        gui.gridx = 0;
        gui.gridy = 1;
        p1.add(new JLabel("Deadline:"), gui);
        deadlineField = new JTextField(10);
        gui.gridx = 1;
        p1.add(deadlineField, gui);

        gui.gridx = 0;
        gui.gridy = 2;
        p1.add(new JLabel("Profit:"), gui);
        profitField = new JTextField(10);
        gui.gridx = 1;
        p1.add(profitField, gui);

        addJobButton = new JButton("Add Job");
        addJobButton.addActionListener(new AddJobListener());
        gui.gridx = 0;
        gui.gridy = 3;
        gui.gridwidth = 2;
        p1.add(addJobButton, gui);

        calculateButton = new JButton("Calculate Schedule");
        calculateButton.addActionListener(new CalculateListener());
        gui.gridy = 10;
        p1.add(calculateButton, gui);

        t2=new TextArea();
        t2.setBackground(new Color(0xade8f4));
        t2.setBounds(0,200,500,150);
        f.add(t2);

        jobListModel = new DefaultListModel<>();
        JList<Job> jobList = new JList<>(jobListModel);




        t1=new TextArea();
        t1.setBounds(0,350,500,500);
        t1.setBackground(new Color(0xcaf0f8));
        f.add(t1);

        JLabel l3=new JLabel();
        l3.setText("Optimum Job Schedule");



    }

    private class AddJobListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String id = jobIdField.getText();
            int deadline = Integer.parseInt(deadlineField.getText());
            int profit = Integer.parseInt(profitField.getText());

            Job job = new Job(id, deadline, profit);
            jobListModel.addElement(job);

            jobIdField.setText("");
            deadlineField.setText("");
            profitField.setText("");
        }
    }

    private class CalculateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Job[] jobs = new Job[jobListModel.size()];
            for (int i = 0; i < jobListModel.size(); i++) {
                jobs[i] = jobListModel.elementAt(i);
            }

            Arrays.sort(jobs, (a, b) -> b.deadline - a.deadline);

            int maxProfit = 0;
            String[] sequence = new String[jobs.length];
            boolean[] slot = new boolean[jobs.length];
            for (int i = 0; i < jobs.length; i++) {
                for (int j = Math.min(jobs.length, jobs[i].deadline) - 1; j >= 0; j--) {
                    if (!slot[j]) {
                        sequence[j] = jobs[i].id;
                        slot[j] = true;

                        maxProfit += jobs[i].profit;
                        break;
                    }
                }
            }

            t1.append("Optimal Job Sequence: " + Arrays.toString(sequence) + "\n");
            t1.append("Maximum Profit: " + maxProfit + "\n");
        }
    }
}

