package MyCLI;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class MCTS {
    ////////////////////// CONFIGURATION PARAMETERS ///////////////////
    private int level = 3;
    private int iterations = 100000;
    private int ALPHA = 1;
    ///////////////////////////////////////////////////////////////////

    private final Graph<Integer, Double> graph;
    private double[][] globalPolicy;
    private double[][][] policy;

    private int n;
    private Instant startTime;
    private int timeLimit;
    private ProgressBar progressBar;
    private Thread progressBarThread;

    public MCTS(Graph<Integer, Double> graph, int timeLimit) {
        this.graph = graph;
        n = graph.getSize();
        this.timeLimit = timeLimit;
    }

    public void setConfigurationParameters(int level, int iterations, int alpha) {
        this.level = level;
        this.iterations = iterations;
        this.ALPHA = alpha;

        this.globalPolicy = new double[n][n];
        this.policy = new double[level][n][n];
    }

    public void run() {
        startTime = Instant.now();

        // Create and start a progress bar thread
        progressBar = new ProgressBar(60, timeLimit);
        progressBarThread = new Thread(progressBar);
        progressBarThread.start();

        Tour<Integer, Double> bestTour = search(level - 1, iterations);
        progressBar.setStop(true);    // searching is completed

        // Wait for progress bar thread to be dead
        while (progressBarThread.isAlive()) {
        }

        if (Vehicle.getMaxCapacity() != Vehicle.getOriMaxCapacity()) { // hetero occurs

            bestTour.calculateHetero();
            ArrayList<String> routeArr = bestTour.stringRoute;
            ArrayList<Double> costAL = new ArrayList<>();
            int costALindex = 0;

            for (int i = 0; i < routeArr.size(); i++) {
                String[] splitRouteArr = routeArr.get(i).split(" -> ");

                int j = 1;

                int currentStop = Integer.parseInt(splitRouteArr[j]);
                int previousStop = Integer.parseInt(splitRouteArr[j - 1]);
                costAL.add(graph.getDistanceBetween(previousStop, currentStop));

                j++;
                while (j < splitRouteArr.length) {
                    currentStop = Integer.parseInt(splitRouteArr[j]);
                    previousStop = Integer.parseInt(splitRouteArr[j - 1]);
                    costAL.set(costALindex, costAL.get(costALindex) + graph.getDistanceBetween(previousStop, currentStop));
                    j++;
                }
                costALindex++;
            }

            System.out.println(Color.colorize((bestTour.heteroToString(costAL)).toString(),"purple"));
            //System.out.println(Color.colorize(bestTour.heteroToString(costAL),"purple"));
            //bestTour.heteroToString(costAL, "purple");

        } else {
            System.out.println(Color.colorize(bestTour.toString(), "purple"));
        }

        //graph.setAllCustomerVisited(false);
    }



    // Method function to perform MCTS with NPRA search
    public Tour<Integer, Double> search(int level, int iterations) {
        Tour<Integer, Double> bestTour = new Tour<>("MCTS");
        bestTour.setTourCost(Double.POSITIVE_INFINITY);

        // Reach level 0, perform rollout() to find tour
        if (level == 0) {
            return rollout();
        } else {
            policy[level] = globalPolicy;

            for (int i = 0; i < iterations; i++) {
                Tour<Integer, Double> newTour = search(level - 1, i);
                if (newTour.getTourCost() < bestTour.getTourCost()) {
                    bestTour = newTour;
                    adapt(bestTour, level);
                }

                // Searching takes too long, already exceeds time limit
                if (Duration.between(startTime, Instant.now()).getSeconds() > timeLimit) {
                    progressBar.setTimeUp(true);
                    return bestTour;
                }
            }
            globalPolicy = policy[level];
        }
        //System.out.println("Search\n" + bestTour);  //DEBUG
        return bestTour;
    }


    // Method to update policy based on rollout result (a tour)
    public void adapt(Tour<Integer, Double> tour, int level) {
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
                    if (!possibleMove.getToCustomer().getVisited()) {
                        int move = possibleMove.getToCustomer().getID();
                        z += Math.exp(globalPolicy[stop][move]);
                    }
                }
                for (Edge<Integer, Double> possibleMove : possibleMoves) {
                    if (!possibleMove.getToCustomer().getVisited()) {
                        int move = possibleMove.getToCustomer().getID();
                        policy[level][stop][move] -= ALPHA * (Math.exp(globalPolicy[stop][move]) / z);
                    }
                }
                graph.getCustomerObject(stop).setVisited(true);
            }
        }
        graph.setAllCustomerVisited(false);
    }


    // Method that search for routes for the tour according to policy
    public Tour<Integer, Double> rollout() {
        Tour<Integer, Double> newTour = new Tour<>("MCTS");
        Customer<Integer, Double> depot = graph.getHead();
        int vehicleID = 1;
        Vehicle<Integer, Double> vehicle = new Vehicle<>(vehicleID);
        vehicle.addVisitedCustomer(depot.getID());
        vehicle.addDemandAL(depot.getDemand());
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
                vehicle.addDemandAL(depot.getDemand());
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
                vehicle.addDemandAL(depot.getDemand());

                graph.setAllCustomerChecked(false);
                continue;
            }

            //Customer<Integer, Double> currentStopCustomer = graph.getCustomerObject(currentStop);
            Customer<Integer, Double> nextStop = selectNextMove(currentStop, possibleSuccessors);

            if (!vehicle.isOverloaded(nextStop.getDemand()) && !nextStop.getVisited()) {
                vehicle.addVisitedCustomer(nextStop.getID());
                vehicle.addDemandAL(nextStop.getDemand());
                vehicle.addCapacity(nextStop.getDemand());
                vehicle.addRouteCost(graph.getDistanceBetween(currentStop, nextStop.getID()));

                nextStop.setVisited(true);
            } else {
                nextStop.setChecked(true);
            }

        }
        //System.out.println("Rollout\n" + newTour); // DEBUG

        graph.setAllCustomerChecked(false);
        graph.setAllCustomerVisited(false);
        return newTour;
    }


    // Method that decide which node to go next from current node based on the policy
    public Customer<Integer, Double> selectNextMove(int currentStop, ArrayList<Edge<Integer, Double>> possibleSuccessors) {
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

