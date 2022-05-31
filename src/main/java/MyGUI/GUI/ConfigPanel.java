package MyGUI.GUI;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class ConfigPanel {
    private static ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    private static File selectedFile;

    static JPanel panel1;
    static JPanel panel2;
    static JPanel panel3;
    static JPanel panel4;
    static JPanel panel5;
    static JPanel panel6;
    static JPanel panel7;

    static JTextField fileChooserField;
    static JTextField timeLimitTextField;
    static JTextField levelTextField;
    static JTextField iterationsTextField;
    static JTextField alphaTextField;

    static JButton browseBtn;
    public static JPanel createConfigPanel() {
        // -------------------------- File Chooser -----------------------------
        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel1.setBorder(BorderFactory.createTitledBorder("Input"));
        JLabel fileChooserLabel = new JLabel("Select a file     : ");
        fileChooserField = new JTextField(20);
        fileChooserLabel.setLabelFor(fileChooserField);
        fileChooserField.setEditable(false);
        browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> {
            selectedFile = null;
            fileChooserField.setText("");
            selectedFile = FileChooser.chooseFile();

            if (selectedFile != null) {
                fileChooserField.setText(selectedFile.getName());
            }
        });
        panel1.add(fileChooserLabel);
        panel1.add(fileChooserField);
        panel1.add(browseBtn);

        // -------------------------- Checkboxes -----------------------------

        panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel2.setBorder(BorderFactory.createTitledBorder("Simulation Type"));
        String[] algorithms = Solver.getAvailableAlgorithms();
        for (String algo : algorithms) {
            JCheckBox checkBox = createCheckBox(algo);
            checkBoxes.add(checkBox);
            panel2.add(checkBox);
        }
        setAccessToConfigurationParameter();

        //------------------------ Time Limit ---------------------------

        panel6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel6.setBorder(BorderFactory.createTitledBorder("Constraint"));
        JLabel timeLimitLbl = new JLabel("Time Limit (Seconds)  : ");
        timeLimitTextField = new JTextField(10);
        timeLimitTextField.setText("60");
        timeLimitTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(timeLimitTextField.getText());
                }
                catch (NumberFormatException nfe) {
                    timeLimitTextField.setText("");
                }
            }
        });
        timeLimitLbl.setLabelFor(timeLimitTextField);
        panel6.add(timeLimitLbl);
        panel6.add(timeLimitTextField);

        // ----------------- MCTS Configuration Parameters --------------------

        panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel levelLbl = new JLabel("Level              : ");
        levelTextField = new JTextField(10);
        levelTextField.setText("3");
        levelTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(levelTextField.getText());
                }
                catch (NumberFormatException nfe) {
                    levelTextField.setText("");
                }
            }
        });
        levelLbl.setLabelFor(levelTextField);
        panel3.add(levelLbl);
        panel3.add(levelTextField);

        panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel iterationLbl = new JLabel("Iterations       : ");
        iterationsTextField = new JTextField(10);
        iterationsTextField.setText("100");
        iterationsTextField.addKeyListener(new KeyAdapter() {  // Allow positive integer input only
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(levelTextField.getText());
                }
                catch (NumberFormatException nfe) {
                    levelTextField.setText("");
                }
            }
        });
        iterationLbl.setLabelFor(iterationsTextField);
        panel4.add(iterationLbl);
        panel4.add(iterationsTextField);

        panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel alphaLbl = new JLabel("Alpha              : ");
        alphaTextField = new JTextField(10);
        alphaTextField.setText("1");
        alphaTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(levelTextField.getText());
                }
                catch (NumberFormatException nfe) {
                    levelTextField.setText("");
                }
            }
        });
        alphaLbl.setLabelFor(alphaTextField);
        panel5.add(alphaLbl);
        panel5.add(alphaTextField);

        panel7 = new JPanel(new GridLayout(0, 1));
        panel7.setBorder(BorderFactory.createTitledBorder("Configuration Parameters (MCTS only)"));
        panel7.add(panel3);
        panel7.add(panel4);
        panel7.add(panel5);

        //-------------------------- Container -------------------------------

        // Container to hold all the panels
        JPanel container = new JPanel();
        container.setLayout(new GridLayout(0, 1, 5, 5));
        container.setBorder(BorderFactory.createEmptyBorder(8, 5, 0, 5));
        container.add(panel1);
        container.add(panel2);
        container.add(panel6);
        container.add(panel7);
        return container;
    }

    private static JCheckBox createCheckBox(String name) {
        JCheckBox checkBox = new JCheckBox(name);
        checkBox.setSelected(true);
        checkBox.setFocusable(false);
        return checkBox;
    }

    /**
     * Enable / disable Configuration Parameters fields based on MCTS CheckBox
     */
    private static void setAccessToConfigurationParameter() {
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.getText().equals("MCTS")) {
                checkBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean enabled = false;
                        if (checkBox.isSelected()) {
                            enabled = true;
                        }
                        levelTextField.setEnabled(enabled);
                        iterationsTextField.setEnabled(enabled);
                        alphaTextField.setEnabled(enabled);
                    }
                });
            }
        }
    }

    public static ArrayList<String> getSelectedCheckBoxValue() {
        ArrayList<String> selectedValues = new ArrayList<>();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedValues.add(checkBox.getText());
            }
        }
        return selectedValues;
    }

    public static String getSelectedFile() {
        return selectedFile.getAbsolutePath();
    }

    public static int getTimeLimit() {
        return Integer.parseInt(timeLimitTextField.getText());
    }

    public static int[] getConfigurationParameters() {
        return new int[] {
                Integer.parseInt(levelTextField.getText()),
                Integer.parseInt(iterationsTextField.getText()),
                Integer.parseInt(alphaTextField.getText()),
        };
    }
}
