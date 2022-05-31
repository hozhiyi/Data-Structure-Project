package MyGUI.algorithms;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Basic {
    private static final int BRANCH = -5; // a value to indicate branch

    private final Graph<Integer, Double> graph;

    public Basic(Graph<Integer, Double> graph) {
        this.graph = graph;
    }

    /**
     * Method to perform depth first search
     */
    public Tour<Integer, Double> dfs(int timeLimit) {
        double minTourCost = Double.POSITIVE_INFINITY;
        String minTour = "";

        Stack<Customer<Integer, Double>> stack = new Stack<>();
        LinkedList<Integer> basicTour = new LinkedList<>();
        Customer<Integer, Double> depot = graph.getHead();
        stack.push(depot);
        int capacity = 0;  // vehicle capacity

        Instant startTime = Instant.now();

        // Loop until exceeds time limit or stack is empty
        while (Duration.between(startTime, Instant.now()).getSeconds() <= timeLimit && !stack.isEmpty()) {

            // Pop invalid customer in stack, skip to next iteration
            Customer<Integer, Double> top = stack.peek();
            if (top.isVisited() || (top.getDemand() + capacity > Vehicle.getMaxCapacity())) {
                stack.pop();
                continue;
            }

            // Valid customer
            Customer<Integer, Double> current = stack.pop();
            if (current.getID().equals(depot.getID())) {
                basicTour.addLast(depot.getID());
                capacity = 0;
            }
            else if (!current.isVisited() && (current.getDemand() + capacity <= Vehicle.getMaxCapacity())) {
                basicTour.addLast(current.getID());
                capacity += current.getDemand();
                current.setVisited(true);
            }

            // Remove invalid customers from children
            ArrayList<Edge<Integer, Double>> children = graph.getUnvisitedEdges(current.getID());
            for (int i = 0; i < children.size(); i++) {
                Customer<Integer, Double> child = children.get(i).getToCustomer();
                if (child.isVisited() || (child.getDemand() + capacity > Vehicle.getMaxCapacity())) {
                    children.remove(children.get(i));
                    i--;        // Ensure correct indexing after remove
                }
            }

            // Reach end of tour, all customers are visited, no more children can be expanded from current node
            if (children.size() == 0) {
                // Store the complete tour if smaller
                String tour = cleanTour(basicTour.toString());
                double tourCost = computeTourCost(tour);
                if (tourCost < minTourCost) {
                    minTourCost = tourCost;
                    minTour = tour;
                }

                // Backtrack to the closest branch if exists
                if (basicTour.lastIndexOf(BRANCH) > 0) {
                    // Reset the customers to unvisited while backtracking
                    while (!basicTour.getLast().equals(BRANCH)) {
                        int customerID = basicTour.getLast();
                        graph.getCustomerObject(customerID).setVisited(false);
                        basicTour.removeLast();
                    }
                    // Ensure BRANCH is removed
                    int result = basicTour.removeLast();
                    assert result == BRANCH;

                    // Calculate new vehicle capacity before starting to explore next branch
                    for (Integer stop : basicTour) {
                        if (stop.equals(BRANCH)) {
                            continue;
                        }
                        else if (stop.equals(depot.getID())) {
                            capacity = 0;
                        }
                        else {
                            capacity += graph.getDemandOf(stop);
                        }
                    }
                }
                continue;
            }
            // Shuffle the order of children to add randomness to searching
            Collections.shuffle(children);

            // Push valid children onto stack
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i).getToCustomer());
            }

            // More than 1 child indicates branching
            if (children.size() > 1) {
                for (int i = 1; i < children.size(); i++) {
                    basicTour.addLast(BRANCH);
                }
            }
        } // End while
        graph.setAllCustomerVisited(false);
        //System.out.println(Color.colorize(basicTour.toString(), "yellow"));
        return generateTour(minTour);
    }

    //---------------------------------- HELPER METHODS ----------------------------------

    /**
     * Example: "[0, -5, -5, 1]" must be cleaned to remove '[', ']', -5
     * @param tour The raw tour
     * @return The tour after being cleaned
     */
    private String cleanTour(String tour) {
        tour = tour.substring(1, tour.length() - 1);    // Remove '[' and ']'
        String[] customers = tour.split(", ");

        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (String customer : customers) {
            if (Integer.parseInt(customer) == BRANCH) {
                continue;
            }
            sb.append(separator).append(customer);
            separator = ",";
        }
        return sb.toString();
    }

    /**
     * Calculate tour cost for a given clean tour
     * @param tour The clean tour
     */
    private double computeTourCost(String tour) {
        double tourCost = 0;
        String[] customers = tour.split(",");
        for (int i = 0; i < customers.length - 1; i++) {
            int src = Integer.parseInt(customers[i]);
            int dest = Integer.parseInt(customers[i + 1]);
            tourCost += graph.getDistanceBetween(src, dest);
        }
        return tourCost;
    }

    /**
     * Generate Tour object from string
     */
    private Tour<Integer, Double> generateTour(String tour) {
        Tour<Integer, Double> basicTour = new Tour<>("Basic");
        String[] customers = tour.split(",");
        int vehicleID = 1;
        Vehicle<Integer, Double> vehicle = new Vehicle<>(vehicleID);

        for (int i = 0; i < customers.length - 1; i++) {
            int src = Integer.parseInt(customers[i]);
            int dest = Integer.parseInt(customers[i + 1]);

            if (src == graph.getHead().getID()) {
                vehicle = new Vehicle<>(vehicleID++);
                vehicle.addVisitedCustomer(src);
            }

            double cost = graph.getDistanceBetween(src, dest);
            vehicle.addVisitedCustomer(dest);
            vehicle.addRouteCost(cost);
            vehicle.addCapacity(graph.getDemandOf(dest));

            if (dest == graph.getHead().getID()) {
                basicTour.addVehicle(vehicle);
            }
        }
        return basicTour;
    }


}

