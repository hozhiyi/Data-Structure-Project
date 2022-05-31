package MyGUI.delivery;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.Scanner;

/**
 * Class to create Graph for each simulation separately to avoid data collision
 */
public class GraphFactory {
    private static final String filePath = ConfigPanel.getSelectedFile();
    private static Graph<Integer, Double> graph = null;

    /**
     * Create Graph from File
     */
    public static Graph<Integer, Double> createGraph() {
        graph = new Graph<>();
        loadDataIntoGraph();
        createUndirectedEdges();
        return graph;
    }

    /**
     * Read data into Graph from File
     */
    private static void loadDataIntoGraph() {
        int N, C, x, y, demand;

        try (Scanner inputStream = new Scanner(new FileInputStream(filePath))) {
            // Read N and C from txt file
            String[] header = inputStream.nextLine().split(" ");
            N = Integer.parseInt(header[0]);   // Number of Customers including Depot
            C = Integer.parseInt(header[1]);   // Maximum capacity of Vehicle

            Vehicle.setMaxCapacity(C);

            // Read Customer data from txt file
            for (int id = 0; (id < N) && (inputStream.hasNextLine()); id++) {
                String[] customerData = inputStream.nextLine().split(" ");
                x = Integer.parseInt(customerData[0]);
                y = Integer.parseInt(customerData[1]);
                demand = Integer.parseInt(customerData[2]);

                // Add Customer as vertex into Graph
                graph.addCustomer(id, x, y, demand);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File was not found");
        }
    }

    /**
     * Create undirected edges among all Customers.
     * Edge weight = Euclidean distance between source and destination
     */
    private static void createUndirectedEdges() {
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
