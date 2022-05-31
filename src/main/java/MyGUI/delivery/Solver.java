package MyGUI.delivery;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import javax.swing.*;

public class Solver implements Runnable {
    private static final String[] ALGORITHMS = {"Basic", "Greedy", "MCTS"};

    private Graph<Integer, Double> graph;
    private final String simulation;
    private final int searchTime;
    private Tour<Integer, Double> bestTour;

    // MyGUI.GUI
    private final JProgressBar progressBar;
    private final Thread progressBarUpdate;

    //CLI
    private ProgressBar progressBarCLI;
    private Thread progressBarThreadCLI;

    // Configuration parameters
    private static int level = 3;
    private static int iterations = 100;
    private static int ALPHA = 1;

    public Solver(Graph<Integer, Double> graph, String simulation, int searchTime) {
        this.graph = graph;
        this.simulation = simulation;
        this.searchTime = searchTime;

        this.progressBar = null;
        this.progressBarUpdate = null;
    }

    public Solver(Graph<Integer, Double> graph, String simulation, JProgressBar progressBar, int searchTime) {
        this.graph = graph;
        this.simulation = simulation;
        this.progressBar = progressBar;
        this.searchTime = searchTime;
        this.progressBarUpdate = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= searchTime; i++) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        progressBar.setString("Searching Completed!");
                    }
                    progressBar.setValue(i);
                }
                progressBar.setString("Searching is forced to stop!");
            }
        };
    }

    @Override
    public void run() {
        switch (simulation.toLowerCase()) {
            case "basic":
                basic();
                break;
            case "greedy":
                greedy();
                break;
            case "mcts":
                mcts();
                break;
        }

        // Update the Chart Panel with best Tour
        if (bestTour != null) {
            Window.updateChartPanel(bestTour);
        }
    }

    /**
     * Return best tour found for all simulations
     */
    public Tour<Integer, Double> getBestTour() {
        return bestTour;
    }

    /**
     * Get the available MyGUI.algorithms
     */
    public static String[] getAvailableAlgorithms() {
        return ALGORITHMS;
    }

    /**
     * Set configuration parameters (MCTS only)
     */
    public static void setConfigurationParameters(int _level, int _iterations, int _alpha) {
        level = _level;
        iterations = _iterations;
        ALPHA = _alpha;
    }

    /**
     * Basic Simulation using DFS
     */
    public void basic() {
        Basic basic = new Basic(graph);
        progressBarUpdate.start();
        bestTour = basic.dfs(searchTime);
        progressBarUpdate.interrupt();
    }

    /**
     * Greedy simulation with heuristic = shortest path
     */
    public void greedy() {
        Greedy greedy = new Greedy(graph);
        progressBarUpdate.start();
        bestTour = greedy.search();
        progressBarUpdate.interrupt();
    }

    /**
     * MCTS simulation with nested rollout policy
     */
    public void mcts() {
        MCTS mcts = new MCTS(graph);
        mcts.setConfigurationParameters(level, iterations, ALPHA);
        progressBarUpdate.start();
        bestTour = mcts.run(searchTime);
        progressBarUpdate.interrupt();
    }

    //---------------------------------- CLI ----------------------------------

    public void showProgressBar() {
        progressBarCLI = new ProgressBar(60, searchTime);
        progressBarThreadCLI = new Thread(progressBarCLI);
        progressBarThreadCLI.start();
    }

    public void stopProgressBar() {
        progressBarCLI.setStop(true);

        // Wait for progress bar thread to be dead
        while (progressBarThreadCLI.isAlive()) {}
    }
}
