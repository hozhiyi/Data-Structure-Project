package MyCLI;

import java.util.ArrayList;
import java.util.Random;

public class Test {
    private static Graph<Integer, Double> testingGraph = new Graph<>();

    /**
     * IMPORTANT!!!
     *
     * You can create a method for testing the newly added features
     *
     * Call this method to determine if the program can run successfully or not after each modification
     */
    public void startTesting() {
        System.out.println("----------- START TESTING -------------\n");

        Simulation.getInputFile(testingGraph);

        System.out.println("-- isEmpty() --\n " + testingGraph.isEmpty() + "\n");
        System.out.println("-- getHead() --\n " + testingGraph.getHead() + "\n");
        System.out.println("-- getSize() --\n " + testingGraph.getSize() + "\n");

        //TEST HERE
        testPrintEdges();
        testHasEdge();
        testHasCustomer();
        testGetNeighbours();
        testGetDistanceBetween();
        testRemoveEdge();
        testGetIndexOf();
        testGetCustomerAt();
        testGetDemandOf();


        System.out.println("\n----------- END TESTING -------------\n");
    }

    public static void testPrintEdges() {
        System.out.println("-- printEdges() --");

        testingGraph.printEdges();
        System.out.println();
    }

    public static void testHasEdge() {
        System.out.println("-- hasEdge() --");

        for (int i = 0; i < testingGraph.getSize(); i++) {
            int source = testingGraph.getCustomerAt(i);

            for (int j = 0; j < testingGraph.getSize(); j++) {
                int destination = testingGraph.getCustomerAt(j);

                System.out.printf("From %d to %d: ", source, destination);
                System.out.println(testingGraph.hasEdge(source, destination));
            }
        }
        System.out.println();
    }

    public static void testHasCustomer() {
        System.out.println("-- hasCustomer() --");

        for (int i = 0; i < testingGraph.getSize(); i++) {
            int id = testingGraph.getCustomerAt(i);
            System.out.print("Has Customer " + id + ": ");
            System.out.println(testingGraph.hasCustomer(id));
        }
        System.out.println();
    }

    public static void testGetNeighbours() {
        System.out.println("-- getNeighbours() --");

        for (int i = 0; i < testingGraph.getSize(); i++) {
            int id = testingGraph.getCustomerAt(i);
            System.out.print("Neighbours of Customer " + id + ": | ");

            ArrayList<Integer> neighbours = testingGraph.getNeighbours(id);
            neighbours.forEach(e -> {
                System.out.print(e + " | ");
            });
            System.out.println();
        }
        System.out.println();
    }

    public static void testRemoveEdge() {
        System.out.println("-- removeEdge() --");

        int index1 = 0;
        int index2 = 1;

        int source = testingGraph.getCustomerAt(index1);
        int destination = testingGraph.getCustomerAt(index2);

        System.out.println("Before:");
        testGetDegree(source, destination);

        System.out.printf("Remove Edge from %d to %d\n", source, destination);
        testingGraph.removeEdge(source, destination);

        System.out.printf("Has Path from %d to %d\n%b\n", source, destination, testingGraph.hasEdge(source, destination));
        System.out.printf("Has Path from %d to %d\n%b\n\n", destination, source, testingGraph.hasEdge(destination, source));

        System.out.println("After:");
        testGetDegree(source, destination);
    }

    public static void testGetDegree(int source, int destination) {
        System.out.println("getIndeg() of Customer " + source);
        System.out.println(testingGraph.getIndegOf(source));
        System.out.println("getOutdeg() of Customer " + source);
        System.out.println(testingGraph.getOutdegOf(source));
        System.out.println();
        System.out.println("getIndeg() of Customer " + destination);
        System.out.println(testingGraph.getIndegOf(destination));
        System.out.println("getOutdeg() of Customer " + destination);
        System.out.println(testingGraph.getOutdegOf(destination));
        System.out.println();
    }

    public void testGetEdges() {
        System.out.println("-- getEdges() --");
        System.out.println("Sorted in ascending order");

        System.out.println();

        System.out.println("Unsorted");

        System.out.println();
    }

    public void testGetIndexOf() {
        System.out.println("-- getIndexOf() --");

        System.out.println("Index of Depot: " + testingGraph.getIndexOf(0));
        System.out.println("Index of Customer '1': " + testingGraph.getIndexOf(1));
        System.out.println();
    }

    public void testGetCustomerAt() {
        System.out.println("-- getCustomerAt() --");

        for (int i = 0; i < testingGraph.getSize(); i++) {
            System.out.println("Customer at index "+ i + ": " + testingGraph.getCustomerAt(i));
        }
        System.out.println();
    }

    public void testGetDemandOf() {
        System.out.println("--getDemandOf()--");

        ArrayList<Integer> customers = testingGraph.getAllCustomersInfo();
        for (Integer c : customers) {
            System.out.printf("Demand of %d: %d\n", c, testingGraph.getDemandOf(c));
        }
        System.out.println();
    }

    public void testGetDistanceBetween() {
        System.out.println("--getDistanceBetween()--");

        for (int i = 0; i < testingGraph.getSize(); i++) {
            for (int j = i+1; j < testingGraph.getSize(); j++) {
                System.out.printf("Distance between %d and %d: ", i, j);
                System.out.println(testingGraph.getDistanceBetween(i, j));
            }
        }
        System.out.println();
    }


}
