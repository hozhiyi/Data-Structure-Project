package MyCLI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class Basic {
    private static final int BRANCH = -5; // a value to indicate branch
    private static int timeLimit;

    public static void setTimeLimit(int timeLimit) {
        Basic.timeLimit = timeLimit;
    }

    public static void search(Graph<Integer, Double> graph) {
        double minTourCost = Double.POSITIVE_INFINITY;
        String minTour = "";

        Stack<Customer<Integer, Double>> stack = new Stack<>();  // DFS

        LinkedList<Integer> currentTour = new LinkedList<>();
        Customer<Integer, Double> depot = graph.getHead();
        stack.push(depot);
        int capacity = 0;                                       // Vehicle capacity

        ProgressBar progressBar = new ProgressBar(60, timeLimit);
        Thread progressBarThread = new Thread(progressBar);
        progressBarThread.start();
        long startTime = System.nanoTime();
        long endTime = startTime + (long) timeLimit * 1_000_000_000;        // Duration of searching

        // Loop until exceeds specific duration or stack is empty
        while (System.nanoTime() <= endTime && !stack.isEmpty()) {

            // Pop invalid customer in stack, skip to next iteration
            Customer<Integer, Double> top = stack.peek();
            if (top.getVisited() || (top.getDemand() + capacity > Vehicle.getOriMaxCapacity())) {
                stack.pop();
                continue;
            }

            // Valid customer
            Customer<Integer, Double> current = stack.pop();
            if (current.getID().equals(depot.getID())) {
                currentTour.addLast(depot.getID());
                capacity = 0;
            } else if (!current.getVisited() && (current.getDemand() + capacity <= Vehicle.getOriMaxCapacity())) {
                currentTour.addLast(current.getID());
                capacity += current.getDemand();
                current.setVisited(true);
            }

            // Filter valid customers from children
            ArrayList<Edge<Integer, Double>> children = graph.getUnvisitedEdges(current.getID());
            for (int i = 0; i < children.size(); i++) {
                Customer<Integer, Double> child = children.get(i).getToCustomer();
                if (child.getVisited() || (child.getDemand() + capacity > Vehicle.getOriMaxCapacity())) {
                    children.remove(children.get(i));
                    i--;        // Ensure correct indexing after remove()
                }
            }

            // Reach end of tour, all customers are visited, no more children can be expanded from current node
            if (children.size() == 0) {

                // Store the complete tour if smaller
                String completeTour = cleanTour(currentTour.toString(), graph);
                double currentTourCost = computeTourCost(completeTour, graph);
                if (currentTourCost < minTourCost) {
                    minTourCost = currentTourCost;
                    minTour = completeTour;

                    demandAL.clear();
                    for (int i = 0; i < minTour.length(); i++) {
                        if (minTour.charAt(i) == ',' || minTour.charAt(i) == '0') {
                            continue;
                        } else {
                            demandAL.add(graph.getDemandOf(Integer.parseInt(String.valueOf(minTour.charAt(i)))));
                        }
                    }

                }

                // Backtrack to the closest branch if exists
                if (currentTour.lastIndexOf(BRANCH) > 0) {
                    // Reset the customers to unvisited while backtracking
                    while (!currentTour.getLast().equals(BRANCH)) {
                        int customerID = currentTour.getLast();
                        graph.getCustomerObject(customerID).setVisited(false);
                        currentTour.removeLast();
                    }

                    // Ensure BRANCH is removed
                    int result = currentTour.removeLast();
                    assert result == BRANCH;

                    // Calculate new vehicle capacity before starting to explore next branch
                    for (int i = 0; i < currentTour.size(); i++) {
                        if (currentTour.get(i).equals(BRANCH)) {
                            continue;
                        } else if (currentTour.get(i).equals(depot.getID())) {
                            capacity = 0;
                        } else {
                            capacity += graph.getDemandOf(currentTour.get(i));
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
                    currentTour.addLast(BRANCH);
                }
            }
        } // End while
        progressBar.setStop(true);
        graph.setAllCustomerVisited(false);

        while (progressBarThread.isAlive()) {}

        if (Vehicle.getMaxCapacity() != Vehicle.getOriMaxCapacity()) {
            display(minTour, graph);
        } else {
            display(minTour, graph);
        }

//        System.out.println("Tour: " + minTour);
//        System.out.println("Tour Cost: " + minTourCost);
//        System.out.println("Time elapsed (ms): " + (System.nanoTime() - startTime) / 1_000_000);
    }


    //*************************************** HELPER METHODS ***************************************

    private static ArrayList<Integer> demandAL = new ArrayList<>();

    /**
     * Example: [0, -5, -5, 1] must be cleaned to remove -5
     *
     * @param tour The raw tour
     * @return The tour after being cleaned
     */
    public static String cleanTour(String tour, Graph<Integer, Double> graph) {
        tour = tour.substring(1, tour.length() - 1);    // Remove '[' and ']'
        String[] vertices = tour.split(", ");

        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (int i = 0; i < vertices.length; i++) {
            if (Integer.parseInt(vertices[i]) == BRANCH) {
                continue;
            }
            sb.append(separator).append(vertices[i]);
            separator = ",";
        }
        return sb.toString();
    }


    /**
     * Calculate tour cost for a given clean tour
     *
     * @param tour  The clean tour
     * @param graph
     * @return
     */
    public static double computeTourCost(String tour, Graph<Integer, Double> graph) {
        double tourCost = 0;
        String[] customers = tour.split(",");
        for (int i = 0; i < customers.length - 1; i++) {
            int src = Integer.parseInt(customers[i]);
            int dest = Integer.parseInt(customers[i + 1]);
            tourCost += graph.getDistanceBetween(src, dest);
        }
        return tourCost;
    }


    public static void display(String tour, Graph<Integer, Double> graph) {
        Tour<Integer, Double> basicTour = new Tour<>("Basic");
        String[] customers = tour.split(",");
        int vehicleID = 1;
        Vehicle<Integer, Double> vehicle = new Vehicle<>(vehicleID);

        for (int i = 0; i < customers.length - 1; i++) {
            int src = Integer.parseInt(customers[i]);
            int dest = Integer.parseInt(customers[i + 1]);

            if (src == graph.getHead().getID()) {
                vehicle = new Vehicle<>(vehicleID);
                vehicle.addVisitedCustomer(src);
                //vehicle.addDemandAL(graph.getDemandOf(src));
                vehicleID++;
            }

            double cost = graph.getDistanceBetween(src, dest);
            vehicle.addVisitedCustomer(dest);
            //vehicle.addDemandAL(graph.getDemandOf(dest));
            vehicle.addRouteCost(cost);
            vehicle.addCapacity(graph.getDemandOf(dest));

            if (dest == graph.getHead().getID()) {
                basicTour.addVehicle(vehicle);
            }
        }

        if (Vehicle.getMaxCapacity() != Vehicle.getOriMaxCapacity()) {

            basicTour.calculateHeteroBasic(tour, demandAL);
            String heteroTour = basicTour.getHeteroBasicTour();

            String[] heteroTourArr = heteroTour.split(",");

            ArrayList<Double> costAL = new ArrayList<>();
            int currentStop;
            int costALindex = 0;
            for (int i = 1; i < heteroTourArr.length; ) {
                if (Integer.parseInt(heteroTourArr[i]) == 0 && Integer.parseInt(heteroTourArr[i- 1]) == 0) {
                    i++;
                }
                currentStop = Integer.parseInt(heteroTourArr[i]);
                int previousStop = Integer.parseInt(heteroTourArr[i - 1]);
                costAL.add(graph.getDistanceBetween(currentStop, previousStop));

                i++;
                if (i < heteroTourArr.length) {
                    do {
                        currentStop = Integer.parseInt(heteroTourArr[i]);
                        previousStop = Integer.parseInt(heteroTourArr[i - 1]);
                        costAL.set(costALindex, costAL.get(costALindex) + graph.getDistanceBetween(currentStop, previousStop));
                        i++;
                    } while (currentStop != 0);

                    i++;
                    costALindex++;
                }
            }

            System.out.println(Color.colorize((basicTour.heteroToString(costAL)).toString(),"yellow"));
            //System.out.println(Color.colorize(basicTour.heteroToString(costAL),"yellow"));
        } else {
            System.out.println(Color.colorize(basicTour.toString(), "yellow"));
        }

    }

    /**
     * Accept String from cleanTour in the form of 0,1,0,2,3,0,4
     *
     * @param sb
     * @param graph
     * @return
     */
    public static String heterogeneousVehicle(String sb, Graph<Integer, Double> graph) {
        // extract customer ID
        // get pureCustomerID: ArrayList<Integer> = 1 2 3 4
        // pair the above with customerDemand: ArrayList<Integer> = 1 8 6 5
        String[] str = sb.split(",");

        ArrayList<Integer> pureCustomerID = new ArrayList<>();
        ArrayList<Integer> customerDemand = new ArrayList<>();

        for (int i = 0; i < str.length; i++) {
            pureCustomerID.add(Integer.parseInt(str[i]));
            customerDemand.add(graph.getDemandOf(pureCustomerID.get(i)));
        }

        // start counting for hetero
        StringBuilder heteroTour = new StringBuilder();
        heteroTour.append("0,");

        int capacity = 0;

        // heteroTour = 0,
        for (int i = 0; i < pureCustomerID.size(); i++) {
            int demand = customerDemand.get(i);
            int currentID = pureCustomerID.get(i);

            if (capacity != 0) {
                int leftOverCapacity = Vehicle.getMaxCapacity() - capacity;
                while (demand > 0) {
                    if (demand > leftOverCapacity) {
                        demand -= leftOverCapacity;

                        heteroTour.append(currentID);
                        heteroTour.append(",0,");
                    } else if (demand < leftOverCapacity) {
                        demand = 0;
                        capacity += demand;

                        heteroTour.append(currentID);
                        heteroTour.append(",");
                    } else {
                        demand = 0;
                        capacity = 0;
                        heteroTour.append(currentID);
                        heteroTour.append(",0,");
                    }
                }
            } else {

                while (demand > 0) {
                    if (demand > Vehicle.getMaxCapacity()) {
                        demand -= Vehicle.getMaxCapacity();

                        heteroTour.append(currentID);
                        heteroTour.append(",0,");
                        // heteroTour = 0,1,0,
                    } else if (demand < Vehicle.getMaxCapacity()) {

                        capacity = demand;
                        demand = 0;

                        heteroTour.append(currentID);
                        heteroTour.append(",");
                        // heteroTour = 0,1,0,1,
                        // capacity != 0
                    } else {

                        demand = 0;
                        heteroTour.append(currentID);
                        heteroTour.append(",0,");
                    }
                }

            }
        }
        String temp = heteroTour.toString();
        char lastChar = temp.charAt(temp.length() - 1);
        char secondLastChar = temp.charAt(temp.length() - 2);
        if (secondLastChar != '0') {
            heteroTour.append("0");
            return heteroTour.toString();
        } else {
            if (lastChar == ',') {
                temp = temp.substring(0, temp.length() - 1);
                return temp;
            }
            return heteroTour.toString();
        }
    }
}
