package MyGUI.algorithms;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import java.util.ArrayList;
import java.util.Collections;

public class Greedy {
    private final Graph<Integer, Double> graph;

    public Greedy(Graph<Integer, Double> graph) {
        this.graph = graph;
    }

    public Tour<Integer, Double> search() {
        Tour<Integer, Double> greedyTour = new Tour<>("Greedy");
        Customer<Integer, Double> depot = graph.getHead();
        int vehicleID = 0;

        // Loop while not all customers are visited
        while (!graph.allCustomerIsVisited()) {

            // Vehicle starts at depot
            Customer<Integer, Double> currentStop = depot;
            Vehicle<Integer, Double> vehicle = new Vehicle<>(++vehicleID);
            vehicle.addVisitedCustomer(currentStop.getID());
            vehicle.addCapacity(currentStop.getDemand());

            // Loop until no more possible successors
            while (true) {
                // Get unvisited neighbours sorted in ascending order
                ArrayList<Edge<Integer, Double>> edgesToChoose = graph.getUnvisitedEdges(currentStop.getID());
                Collections.sort(edgesToChoose);

                boolean backToDepot = true;  // Assume vehicle is going back to depot

                // Find the next customer to visit
                for(Edge<Integer, Double> edge : edgesToChoose){
                    Customer<Integer, Double> nextStop = edge.getToCustomer();

                    if (nextStop.getID().equals(depot.getID())) { continue; }     // Skip depot
                    if (nextStop.isVisited()) { continue; }                       // Skip visited customer
                    if (vehicle.isOverloaded(nextStop.getDemand())) { continue; } // Skip overloaded customer demand

                    // Valid customer is found, no need to go back to depot
                    vehicle.addVisitedCustomer(nextStop.getID());
                    nextStop.setVisited(true);

                    double cost = edge.getWeight();
                    vehicle.addRouteCost(cost);
                    vehicle.addCapacity(nextStop.getDemand());

                    backToDepot = false;
                    currentStop = nextStop;
                    break;
                }

                // No more valid customer, back to depot
                if (backToDepot) {
                    break;
                }
            }

            // Back to depot
            vehicle.addVisitedCustomer(depot.getID());
            double cost = graph.getDistanceBetween(depot.getID(), currentStop.getID());
            vehicle.addRouteCost(cost);

            // Add vehicle (route) into tour
            greedyTour.addVehicle(vehicle);
        }
        graph.setAllCustomerVisited(false);

        return greedyTour;
    }
}