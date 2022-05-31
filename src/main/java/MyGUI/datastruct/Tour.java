package MyGUI.datastruct;

import java.util.ArrayList;

public class Tour <T extends Comparable<T>,
                   N extends Number & Comparable<N>> implements Comparable<Tour<T, N>> {

    private final String simulation;
    private double tourCost;
    private final ArrayList<Vehicle<T, N>> vehicles;

    public Tour(String simulation) {
        this.simulation = simulation;
        this.tourCost = 0;
        this.vehicles = new ArrayList<>();
    }

    public String getSimulation() {
        return simulation;
    }

    public double getTourCost() {
        return tourCost;
    }

    public void setTourCost(double tourCost) {
        this.tourCost = tourCost;
    }

    public ArrayList<Vehicle<T, N>> getVehicles() {
        return vehicles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String header = simulation + " Simulation\n"
                + "Tour\n"
                + "Tour Cost: " + tourCost
                + "\nMaximum Capacity: " + Vehicle.getMaxCapacity() + "\n\n";
        sb.append(header);
        for (Vehicle<T, N> v : vehicles) {
            sb.append(v.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Tour<T, N> aTour) {
        if (this.tourCost < aTour.getTourCost()) {
            return -1;
        }
        else if (this.tourCost > aTour.getTourCost()) {
            return 1;
        }
        return 0;
    }

    //-------------------------------- EXTRA METHODS --------------------------------

    /**
     * Add route cost of vehicle into tour cost
     * @param routeCost Route Cost of the vehicle
     * @return True if added, else False
     */
    @SuppressWarnings("UnusedReturnValue")
    private boolean addTourCost(double routeCost) {
        if (routeCost < 0) {
            return false;
        }
        tourCost += routeCost;
        return true;
    }

    /**
     * Add vehicle after completing a route into the tour
     * @param vehicle MyGUI.delivery.Vehicle after completing a route
     * @return True if added, else False
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addVehicle(Vehicle<T, N> vehicle) {
        if (vehicle == null) {
            return false;
        }
        vehicles.add(vehicle);
        addTourCost(vehicle.getRouteCost());
        return true;
    }

}
