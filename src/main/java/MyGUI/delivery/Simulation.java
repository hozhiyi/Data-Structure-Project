package MyGUI.delivery;

import MyGUI.datastruct.*;
import MyGUI.GUI.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class Simulation {

    public static void simulate() {
        //--------------For testing purposes---------------
        //testing();
        //-------------------------------------------------

        //showCLI();
        showGUI();
    }

    //---------------------------- Helper Methods ----------------------------

    public static void showCLI() {
        MyCLI.Simulation.simulate();
    }

    public static void showGUI() {
        Window window = new Window();
        window.createAndShowGUI();
    }

    public static void testing() {
        Test test = new Test();
        test.startTesting();
    }

    //---------------------------------- CLI ----------------------------------

    /**
     * Prompt user for test cases
     */
    public static void getInputFile(Graph<Integer, Double> graph) {
        if (graph.getDebugMode()) {
            System.out.println("------------ Getting input file ----------\n");
        }

        // Get list of files in the directory
        File directory = new File("./Sample Input");
        String[] testFiles = directory.list();
        for (int i = 0; i < Objects.requireNonNull(testFiles).length; i++) {
            System.out.printf("[%d] %s\n", (i + 1), testFiles[i]);
        }

        // Let the user choose the file to read by number
        Scanner scanner = new Scanner(System.in);
        int index = -1;
        do {
            System.out.printf("Enter the file to read in (%d to %d): ", 1, testFiles.length);
            try {
                index = scanner.nextInt() - 1;
            } catch (Exception e) {
                continue;
            }
            System.out.println();
        } while (index < 0 || index >= testFiles.length);

        String filename = "./Sample Input/" + testFiles[index];
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File did not exist");
        } else {
            loadDataIntoGraph(filename, graph);
        }
    }

    /**
     * Read the data from sample input file
     *
     * @param filename Name of the txt file
     */
    public static void loadDataIntoGraph(String filename, Graph<Integer, Double> graph) {
        if (graph.getDebugMode()) {
            System.out.println("---------- Loading data into the graph --------------");
        }

        int N, C, x, y, demand;
        try {
            Scanner inputStream = new Scanner(new FileInputStream(filename));

            // Read N and C from txt file
            String[] header = inputStream.nextLine().split(" ");
            N = Integer.parseInt(header[0]);   // Number of customers including depot
            C = Integer.parseInt(header[1]);   // Maximum capacity of vehicle

            Vehicle.setMaxCapacity(C);

            // Read customer data from txt file
            for (int id = 0; (id < N) && (inputStream.hasNextLine()); id++) {
                // MyGUI.delivery.Customer info x-coordinate, y-coordinate, customer demand
                String[] customerData = inputStream.nextLine().split(" ");
                x = Integer.parseInt(customerData[0]);
                y = Integer.parseInt(customerData[1]);
                demand = Integer.parseInt(customerData[2]);

                // Add customer as vertex into graph
                graph.addCustomer(id, x, y, demand);
            }
            inputStream.close();

            createUndirectedEdges(graph);
        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
        }
    }

    /**
     * Create undirected edges among all the customers.
     * Edge weight equals to Euclidean distance between source and destination
     */
    public static void createUndirectedEdges(Graph<Integer, Double> graph) {
        if (graph.getDebugMode()) {
            System.out.println("----------- Creating undirected edges -------------");
        }

        for (int i = 0; i < graph.getSize(); i++) {
            int source = graph.getCustomerAt(i);

            for (int j = i + 1; j < graph.getSize(); j++) {
                int destination = graph.getCustomerAt(j);

                double distance = graph.computeEuclidean(source, destination);
                graph.addUndirectedEdge(source, destination, distance);
            }
        }
    }

}
