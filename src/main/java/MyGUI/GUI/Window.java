package MyGUI.GUI;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Window {
    private static final String APP_ICON_PATH = "./assets/img/MyGUI.delivery-truck.png",
                                BACKGROUND_IMG_PATH = "./assets/img/background_image.jpg",
                                MAIN_PANEL = "Card with Main Panel",
                                CONFIG_PANEL = "Card with Config Panel",
                                TABBED_PANE_PANEL = "Card with Tabbed Pane to hold multiple Chart Panels";

    private static JFrame frame;
    private static JDialog tourResultDialog;

    private ArrayList<Thread> solverThreads;

    private static JPanel cards;
    private static JPanel card1;
    private static JPanel card2;
    private static JTabbedPane card3;

    public Window() {
    }

    public void createAndShowGUI() {
        Music music = new Music();
        music.play();

        frame = new JFrame();
        frame.setTitle("Always On Time Delivery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Full Screen
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String message = "Are you sure you want to exit?";
                int confirm = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    music.stop();
                    music.close();
                    System.exit(0);
                }
                else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        //--------------------- App Icon -------------------------
        Image icon = Toolkit.getDefaultToolkit().getImage(APP_ICON_PATH);
        frame.setIconImage(icon);

        /*
          Card 1 = Background Image Panel
          Card 2 = Config Panel
          Card 3 = Tabbed Pane to hold multiple Chart Panels
         */
        card1 = new BackgroundPanel(BACKGROUND_IMG_PATH);
        card2 = ConfigPanel.createConfigPanel();
        card3 = new JTabbedPane();

        // Panel to hold cards
        cards = new JPanel(new CardLayout());
        cards.add(card1, MAIN_PANEL);
        cards.add(card2, CONFIG_PANEL);
        cards.add(card3, TABBED_PANE_PANEL);

        // -------------------------- Card 1 -----------------------------

        JPanel mainMenu = new JPanel();

        // Start button (Proceed to Config Panel)
        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cards.getLayout();
            cardLayout.show(cards, CONFIG_PANEL);
        });

        // Exit button (Exit the program)
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> {
            String message = "Are you sure you want to exit?";
            int confirm = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                frame.dispose();
                System.exit(0);
            }
        });

        mainMenu.add(startBtn);
        mainMenu.add(exitBtn);
        card1.add(mainMenu);

        // -------------------------- Card 2 -----------------------------

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Simulate button (Start the simulation)
        JButton simulateBtn = new JButton("Simulate");
        simulateBtn.addActionListener(e -> {
            if (ConfigPanel.getSelectedFile() != null) {
                int timeLimit = ConfigPanel.getTimeLimit();

                // Generate Solver for each selected simulation type
                solverThreads = new ArrayList<>();
                ArrayList<String> selectedSimulations = ConfigPanel.getSelectedCheckBoxValue();
                for (String simulation : selectedSimulations) {
                    // If MCTS is selected, set Configuration Parameters
                    if (simulation.equals("MCTS")) {
                        int[] parameters = ConfigPanel.getConfigurationParameters();
                        int level = parameters[0];
                        int iterations = parameters[1];
                        int alpha = parameters[2];
                        Solver.setConfigurationParameters(level, iterations, alpha);
                    }

                    Graph<Integer, Double> graph = GraphFactory.createGraph();

                    // Add Progress Bar for each simulation to Tabbed Pane
                    JProgressBar solverProgressBar = createProgressBar(timeLimit);
                    Panel progressBarPanel = new Panel();
                    progressBarPanel.add(solverProgressBar, BorderLayout.CENTER);
                    card3.add(simulation, progressBarPanel);

                    // Create Thread for each Solver
                    Solver solver = new Solver(graph, simulation, solverProgressBar, timeLimit);
                    Thread solverThread = new Thread(solver);
                    solverThread.setName(simulation);
                    solverThreads.add(solverThread);
                }

                // Start all Solver Threads
                for (Thread t : solverThreads) {
                    t.start();
                }

                // Switch to Tabbed Pane
                CardLayout cardLayout = (CardLayout) cards.getLayout();
                cardLayout.show(cards, TABBED_PANE_PANEL);
            }
        });

        // Cancel button (Back to Main Menu)
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) cards.getLayout();
            cardLayout.show(cards, MAIN_PANEL);
        });

        btnPanel.add(simulateBtn);
        btnPanel.add(cancelBtn);
        card2.add(btnPanel);

        //-------------------------- Card 3 -----------------------------


        //--------------------------------------------------------------
        frame.add(cards, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    //------------------------------ CHART PANEL -------------------------------
    /**
     * Create toolbar to hold "Zoom" buttons and "Result" button
     */
    private static JToolBar createToolBar(ChartPanel chartPanel, Tour<Integer, Double> tour) {
        JToolBar toolBar = new JToolBar();

        // Button to go back to Main Menu
//        JButton mainMenuBtn = new JButton("Main Menu");
//        mainMenuBtn.addActionListener(e -> {
//            card3.removeAll();
//            frame.dispose();
//            Window window = new Window();
//            window.createAndShowGUI();
//        });
        //toolBar.add(mainMenuBtn);

        String[] names = {"Zoom In X", "Zoom Out X", "Zoom In Y", "Zoom Out Y"};
        String[] actionCommands = {ChartPanel.ZOOM_IN_DOMAIN_COMMAND, ChartPanel.ZOOM_OUT_DOMAIN_COMMAND,
                                   ChartPanel.ZOOM_IN_RANGE_COMMAND, ChartPanel.ZOOM_OUT_RANGE_COMMAND};
        for (int i = 0; i < names.length; i++) {
            toolBar.add(createZoomButton(names[i], actionCommands[i], chartPanel));
        }

        JButton resultBtn = createResultButton(tour);
        toolBar.add(resultBtn);
        return toolBar;
    }

    /**
     * Create buttons for the chart to zoom in and out
     */
    private static JButton createZoomButton(String name, String actionCommand, ChartPanel chartPanel) {
        JButton button = new JButton(name);
        button.setActionCommand(actionCommand);
        button.setFocusable(false);

        button.addActionListener(e -> {
            String command = e.getActionCommand();
            switch (command) {
                case ChartPanel.ZOOM_IN_DOMAIN_COMMAND -> chartPanel.zoomInDomain(0, 0);
                case ChartPanel.ZOOM_OUT_DOMAIN_COMMAND -> chartPanel.zoomOutDomain(0.95, 0.95);
                case ChartPanel.ZOOM_IN_RANGE_COMMAND -> chartPanel.zoomInRange(1.05, 1.05);
                case ChartPanel.ZOOM_OUT_RANGE_COMMAND -> chartPanel.zoomOutRange(0.95, 0.95);
            }
        });

        return button;
    }

    /**
     * Create button for click to pop a dialog for showing tour result
     */
    private static JButton createResultButton(Tour<Integer, Double> tour) {
        // Button to show dialog (tour result)
        JButton showResultBtn = new JButton("Result");
        showResultBtn.setFocusable(false);

        showResultBtn.addActionListener(e -> {
            // Dialog to show the tour result
            if (tourResultDialog != null) {
                tourResultDialog.dispose();
            }
            String title = tour.getSimulation() + " Simulation";
            tourResultDialog = new JDialog(frame, title, Dialog.ModalityType.MODELESS);
            tourResultDialog.setLocationRelativeTo(null);

            // Make JTextPane looks like JLabel, text is copyable
            JTextPane textPane = new JTextPane();
            textPane.setContentType("text/html");
            textPane.setEditable(false);
            textPane.setBackground(null);
            textPane.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Show Tour result in Text Pane
            //todo hetero
            String result = tour.toString().replaceAll("\\n", "<br/>");
            textPane.setText("<html>" + result + "</html>");

            // Change font in JTextPane
            textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
            textPane.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

            // Scroll Panel to hold Text Pane
            JScrollPane scrollPane = new JScrollPane(textPane);

            // Prevent scroll Pane size to increase infinitely
            scrollPane.setPreferredSize(new Dimension(500, 600));

            tourResultDialog.add(scrollPane, BorderLayout.NORTH);
            tourResultDialog.pack();
            tourResultDialog.setVisible(true);
        });

        return showResultBtn;
    }

    /**
     * Create Progress Bar to de displayed for each selected simulation in Tabbed Pane
     */
    private static JProgressBar createProgressBar(int duration) {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setForeground(Color.GREEN);
        progressBar.setBackground(Color.BLACK);
        progressBar.setMinimum(0);
        progressBar.setMaximum(duration);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(300, 30));
        progressBar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        return progressBar;
    }

    /**
     * Update Tab that holds Chart Panel in Tabbed Pane with best tour after completing each Simulation
     */
    public static void updateChartPanel(Tour<Integer, Double> tour) {
        ChartPanel chartPanelTab = ChartPanelFactory.createChart(tour);
        JToolBar toolBar = createToolBar(chartPanelTab, tour);
        chartPanelTab.add(toolBar);

        String simulation = tour.getSimulation();
        int insert = card3.indexOfTab(simulation);
        if (insert != -1) {
            card3.setComponentAt(insert, chartPanelTab);
        }
    }

}
