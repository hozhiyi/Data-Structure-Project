package MyGUI.datastruct;

import java.util.ArrayList;

public class Vehicle <T extends Comparable<T>,
                      N extends Number & Comparable<N>> {

    private static int MAX_CAPACITY;
    private int id;
    private int capacity;
    private double routeCost; // Route Cost of vehicle so far
    private final ArrayList<T> route;   // customers (vertices) visited by vehicle

    public Vehicle(int id) {
        this.id = id;
        this.capacity = 0;
        this.routeCost = 0;
        this.route = new ArrayList<>();
    }

    public static int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public static void setMaxCapacity(int C) {
        MAX_CAPACITY = C;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getRouteCost() {
        return routeCost;
    }

    public void setRouteCost(double routeCost) {
        this.routeCost = routeCost;
    }

    public ArrayList<T> getRoute() {
        return route;
    }

    /**
     * Generate the route taken by vehicle where each customer is separated by "->"
     *
     * @return Route taken by vehicle with separator "->"
     */
    private String constructRouteString() {
        String separator = "";
        StringBuilder vehicleRoute = new StringBuilder();

        for (T customer : route) {
            vehicleRoute.append(separator).append(customer);
            separator = " -> ";
        }
        vehicleRoute.append("\n");
        return vehicleRoute.toString();
    }

    @Override
    public String toString() {
        return "Vehicle " + getId() + "\n"
                + constructRouteString()
                + "Capacity: " + this.capacity + "\n"
                + "Cost: " + this.routeCost + "\n";
    }

    //---------------------------------- EXTRA METHODS ----------------------------------

    /**
     * Check if vehicle is overloaded by comparing expected capacity and MAX_CAPACITY
     * Expected Capacity = Current Capacity + MyGUI.delivery.Customer Demand
     *
     * @return True if vehicle is overloaded else False
     */
    public boolean isOverloaded(int demand) {
        int expectedCapacity = this.capacity + demand;
        return (expectedCapacity > MAX_CAPACITY);
    }

    /**
     * Add the customer demand size to vehicle capacity if not overloaded
     *
     * @return True if capacity is added else False
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addCapacity(int demand) {
        if (isOverloaded(demand)) {
            return false;
        }
        this.capacity += demand;
        return true;
    }

    /**
     * Add customer to the visited list
     */
    public void addVisitedCustomer(T customerInfo) {
        route.add(customerInfo);
    }

    /**
     * Add edge weight to the route cost of vehicle
     */
    public void addRouteCost(double cost) {
        this.routeCost += cost;
    }
}
