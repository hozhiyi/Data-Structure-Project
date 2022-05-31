package MyGUI.algorithms;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class MCTS {
    //---------------------------------- CONFIGURATION PARAMETERS ----------------------------------
    private int level;
    private int iterations;
    private int ALPHA;
    //----------------------------------------------------------------------------------------------

    private final Graph<Integer, Double> graph;
    private double[][] globalPolicy;
    private double[][][] policy;

    private Instant startTime;
    private int timeLimit;

    public MCTS(Graph<Integer, Double> graph) {
        this.graph = graph;
    }

    public void setConfigurationParameters(int level, int iterations, int alpha) {
        this.level = level;
        this.iterations = iterations;
        this.ALPHA = alpha;

        int n = graph.getSize();
        this.globalPolicy = new double[n][n];
        this.policy = new double[level][n][n];
    }

    public Tour<Integer, Double> run(int timeLimit) {
        this.timeLimit = timeLimit;
        startTime = Instant.now();

        Tour<Integer, Double> bestTour = search(level - 1, iterations);
        graph.setAllCustomerVisited(false);
        return bestTour;
    }

    // Method function to perform MCTS with NPRA search
    private Tour<Integer, Double> search(int level, int iterations) {
        Tour<Integer, Double> bestTour = new Tour<>("MCTS");
        bestTour.setTourCost(Double.POSITIVE_INFINITY);

        // Reach level 0, perform rollout() to find tour
        if (level == 0) {
            return rollout();
        }
        else {
            policy[level] = globalPolicy;

            for (int i = 0; i < iterations; i++) {
                Tour<Integer, Double> newTour = search(level - 1, i);
                if (newTour.getTourCost() < bestTour.getTourCost()) {
                    bestTour = newTour;
                    adapt(bestTour, level);
                }

                // Searching takes too long, already exceeds time limit
                if (Duration.between(startTime, Instant.now()).getSeconds() > timeLimit) {
                    return bestTour;
                }
            }
            globalPolicy = policy[level];
        }
        return bestTour;
    }

    /**
     * Method to update policy based on rollout result (a tour)
     */
    private void adapt(Tour<Integer, Double> tour, int level) {
        // Routes are stored in Vehicle
        ArrayList<Vehicle<Integer, Double>> vehicles = tour.getVehicles();

        for (Vehicle<Integer, Double> vehicle : vehicles) {
            ArrayList<Integer> route = vehicle.getRoute();

            for (int i = 0; i < route.size() - 1; i++) {
                int stop = route.get(i);
                int nextStop = route.get(i + 1);

                policy[level][stop][nextStop] += ALPHA;
                double z = 0;

                ArrayList<Edge<Integer, Double>> possibleMoves = graph.getEdges(stop);
                for (Edge<Integer, Double> possibleMove : possibleMoves) {
                    if (!possibleMove.getToCustomer().isVisited()) {
                        int move = possibleMove.getToCustomer().getID();
                        z += Math.exp(globalPolicy[stop][move]);
                    }
                }
                for (Edge<Integer, Double> possibleMove : possibleMoves) {
                    if (!possibleMove.getToCustomer().isVisited()) {
                        int move = possibleMove.getToCustomer().getID();
                        policy[level][stop][move] -= ALPHA * (Math.exp(globalPolicy[stop][move]) / z);
                    }
                }
                graph.getCustomerObject(stop).setVisited(true);
            }
        }
        graph.setAllCustomerVisited(false);
    }

    /**
     * Method that search for routes for the tour according to policy
     */
    private Tour<Integer, Double> rollout() {
        Tour<Integer, Double> newTour = new Tour<>("MCTS");
        Customer<Integer, Double> depot = graph.getHead();

        int vehicleID = 1;
        Vehicle<Integer, Double> vehicle = new Vehicle<>(vehicleID);
        vehicle.addVisitedCustomer(depot.getID());
        depot.setVisited(true);

        while (true) {
            int last = vehicle.getRoute().size() - 1;
            int currentStop = vehicle.getRoute().get(last);

            // Possible successors are unchecked and unvisited
            ArrayList<Edge<Integer, Double>> possibleSuccessors = graph.getUncheckedUnvisitedEdges(currentStop);

            // No successors available
            if (possibleSuccessors.isEmpty()) {
                // current route completed, should return to depot
                vehicle.addVisitedCustomer(depot.getID());
                double cost = graph.getDistanceBetween(currentStop, depot.getID());
                vehicle.addRouteCost(cost);

                // Tour is completed
                if (graph.allCustomerIsVisited()) {
                    newTour.addVehicle(vehicle);
                    break;
                }

                // Start next route
                newTour.addVehicle(vehicle);
                vehicle = new Vehicle<>(++vehicleID);
                vehicle.addVisitedCustomer(depot.getID());
                graph.setAllCustomerChecked(false);
                continue;
            }

            Customer<Integer, Double> nextStop = selectNextMove(currentStop, possibleSuccessors);

            if (!vehicle.isOverloaded(nextStop.getDemand()) && !nextStop.isVisited()) {
                vehicle.addVisitedCustomer(nextStop.getID());
                vehicle.addCapacity(nextStop.getDemand());
                vehicle.addRouteCost(graph.getDistanceBetween(currentStop, nextStop.getID()));
                nextStop.setVisited(true);
            }
            else {
                nextStop.setChecked(true);
            }
        }

        graph.setAllCustomerChecked(false);
        graph.setAllCustomerVisited(false);
        return newTour;
    }


    /**
     * Method that decide which node to go next from current node based on the policy
     */
     private Customer<Integer, Double> selectNextMove(int currentStop, ArrayList<Edge<Integer, Double>> possibleSuccessors) {
        double[] probability = new double[possibleSuccessors.size()];
        double sum = 0;

        for (int i = 0; i < possibleSuccessors.size(); i++) {
            int possibleSuccessor = possibleSuccessors.get(i).getToCustomer().getID();
            probability[i] = Math.exp(globalPolicy[currentStop][possibleSuccessor]);
            sum += probability[i];
        }

        double mrand = new Random().nextDouble() * sum;
        int i = 0;
        sum = probability[0];
        while (sum < mrand) {
            sum += probability[++i];
        }
        return possibleSuccessors.get(i).getToCustomer();
    }
}