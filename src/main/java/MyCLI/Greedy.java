package MyCLI;

import java.util.ArrayList;
import java.util.Collections;

public class Greedy {
    private static int timeLimit;

    public static void setTimeLimit(int timeLimit) {
        Greedy.timeLimit = timeLimit;
    }

    public static void search(Graph<Integer, Double> graph) {
        Tour<Integer, Double> greedyTour = new Tour<>("Greedy");
        Customer<Integer, Double> depot = graph.getHead();
        int vehicleID = 0;

        ProgressBar progressBar = new ProgressBar(60, timeLimit);
        Thread progressBarThread = new Thread(progressBar);
        progressBarThread.start();

        long startTime = System.nanoTime();
        long endTime = startTime + (long) timeLimit * 1_000_000_000;

        // Loop while not all customers are visited
        while (!graph.allCustomerIsVisited() && System.nanoTime() <= endTime) {

            // Vehicle starts travelling from depot
            Customer<Integer, Double> current = depot;
            Vehicle<Integer, Double> vehicle = new Vehicle<>(++vehicleID);
            vehicle.addVisitedCustomer(current.getID());
            vehicle.addCapacity(current.getDemand());

            // Loop to find the next customer to visit until no more valid customer
            while (true) {
                // Get neighbouring edges point to not visited customer sorted in ascending order
                ArrayList<Edge<Integer, Double>> sortedEdgesToChoose = graph.getUnvisitedEdges(current.getID());
                ArrayList<Double> costAL = new ArrayList<>();

                if (Vehicle.getMaxCapacity() != Vehicle.getOriMaxCapacity()) {  // heterogeneous vehicle occurs

                    // calculate and store cost for every possible unvisited edges
                    for (Edge<Integer, Double> edgeToGo : sortedEdgesToChoose) {
                        Customer<Integer, Double> customerToGo = edgeToGo.getToCustomer();
                        int demand = customerToGo.getDemand();
                        //double cost = 0;

                        if (demand % Vehicle.getMaxCapacity() == 0) {
                            costAL.add((demand / Vehicle.getMaxCapacity()) * edgeToGo.getWeight());
                        } else {
                            costAL.add(((demand / Vehicle.getMaxCapacity()) + 1) * edgeToGo.getWeight());
                        }
                    }

                    // bubble sort the whole thing in ascending order
                    for (int i = 0; i < sortedEdgesToChoose.size(); i++) {
                        for (int j = i + 1; j < sortedEdgesToChoose.size(); j++) {

                            if (costAL.get(j) < costAL.get(j)) {
                                Edge<Integer, Double> temp = sortedEdgesToChoose.get(i);
                                sortedEdgesToChoose.set(i, sortedEdgesToChoose.get(j));
                                sortedEdgesToChoose.set(j, temp);
                            }
                        }
                    }

                } else {
                    Collections.sort(sortedEdgesToChoose);
                }

                boolean backToDepot = true;  // assume vehicle is going back to depot

                // Loop through the edge to find the next customer to visit
                for (Edge<Integer, Double> edgeToGo : sortedEdgesToChoose) {
                    Customer<Integer, Double> customerToGo = edgeToGo.getToCustomer();

                    if (customerToGo.getID().equals(depot.getID())) {
                        continue;
                    }     // Skip depot
                    if (customerToGo.getVisited()) {
                        continue;
                    }                      // Skip visited customer
                    if (vehicle.isOverloaded(customerToGo.getDemand())) {
                        continue;
                    } // Skip overloaded customer demand

                    // Valid customer is found, no need to go back to depot
                    vehicle.addVisitedCustomer(customerToGo.getID());
                    vehicle.addDemandAL(customerToGo.getDemand());

                    customerToGo.setVisited(true);
                    double cost = edgeToGo.getWeight();
                    vehicle.addRouteCost(cost);
                    vehicle.addCapacity(customerToGo.getDemand());

                    backToDepot = false;
                    current = customerToGo;
                    break;
                }

                // No more valid customer, back to depot
                if (backToDepot) {
                    break;
                }
            }

            // Back to depot
            vehicle.addVisitedCustomer(depot.getID());
            vehicle.addDemandAL(depot.getDemand());
            double distanceFromDepot = graph.getDistanceBetween(depot.getID(), current.getID());
            vehicle.addRouteCost(distanceFromDepot);

            // Add vehicle storing route into tour
            greedyTour.addVehicle(vehicle);
        }

        progressBar.setStop(true);

        // Reset all customers to not visited after completing search
        graph.setAllCustomerVisited(false);

        while (progressBarThread.isAlive()) {}

        // hetero vehicle re-count tour cost, reset string
        if (Vehicle.getMaxCapacity() != Vehicle.getOriMaxCapacity()) {

            greedyTour.calculateHetero();
            ArrayList<String> routeArr = greedyTour.stringRoute;
            ArrayList<Double> costAL = new ArrayList<>();
            int costALindex = 0;
            for (int i = 0; i < routeArr.size(); i++) {
                if (routeArr.get(i).length() > 12) {
                    String[] splitRoute = routeArr.get(i).split(" ");
                    ArrayList<Integer> idAL = new ArrayList<>();

                    for (int j = 0; j < splitRoute.length; j++) {
                        if (splitRoute[j].charAt(0) == '0' || splitRoute[j].equals("->") || splitRoute[j].isEmpty()) {
                            continue;
                        }
                        idAL.add(Integer.parseInt(splitRoute[j]));
                    }
                    costAL.add(graph.getDistanceBetween(0, idAL.get(0)));

                    for (int j = 0; j < idAL.size() - 1; j++) {
                        costAL.set(costALindex, costAL.get(costALindex) + graph.getDistanceBetween(idAL.get(j), idAL.get(j + 1)));
                    }
                    costAL.set(costALindex, costAL.get(costALindex) + graph.getDistanceBetween(0, idAL.get(idAL.size() - 1)));
                    costALindex++;

                } else {
                    String[] splitRoute = routeArr.get(i).split(" ");
                    for (int j = 0; j < splitRoute.length; j++) {
                        if (splitRoute[j].charAt(0) == '0' || splitRoute[j].equals("->") || splitRoute[j].isEmpty()) {
                            continue;
                        }
                        int id = Integer.parseInt(splitRoute[j]);
                        costAL.add(graph.getDistanceBetween(0, id));
                        costAL.set(costALindex, costAL.get(costALindex) + graph.getDistanceBetween(0, id));
                        costALindex++;
                    }
                }
            }
            System.out.println(Color.colorize((greedyTour.heteroToString(costAL)).toString(), "cyan"));
            //System.out.println(Color.colorize(greedyTour.heteroToString(costAL), "cyan"));
            //greedyTour.heteroToString(costAL, "cyan");

        } else {
            System.out.println(Color.colorize(greedyTour.toString(), "cyan"));
        }
    }

}