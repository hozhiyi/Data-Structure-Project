package MyCLI;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Tour<T extends Comparable<T>,
        N extends Number & Comparable<N>> implements Comparable<Tour<T, N>> {

    private String simulation;
    private double tourCost;
    private ArrayList<Vehicle<T, N>> vehicles;
    private int index;

    public Tour(String simulation) {
        this.simulation = simulation;
        this.tourCost = 0;
        this.vehicles = new ArrayList<>();
    }

    public Tour(String simulation, int index) {
        this.simulation = simulation;
        this.tourCost = 0;
        this.vehicles = new ArrayList<>();
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getSimulation() {
        return simulation;
    }

    public void setSimulation(String simulation) {
        this.simulation = simulation;
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
                + "Tour Cost: " + tourCost + "\n";
        sb.append(header);
        for (Vehicle<T, N> v : vehicles) {
            sb.append(v.toString());
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Tour<T, N> aTour) {
        if (this.tourCost < aTour.getTourCost()) {
            return -1;
        } else if (this.tourCost > aTour.getTourCost()) {
            return 1;
        }
        return 0;
    }

    /******************************** EXTRA METHODS ********************************/

    /**
     * Add route cost of vehicle into tour cost
     *
     * @param routeCost Route Cost of the vehicle
     * @return True if added, else False
     */
    private boolean addTourCost(double routeCost) {
        if (routeCost < 0) {
            return false;
        }
        tourCost += routeCost;
        return true;
    }

    /**
     * Add vehicle after completing a route into the tour
     *
     * @param vehicle Vehicle after completing a route
     * @return True if added, else False
     */
    public boolean addVehicle(Vehicle<T, N> vehicle) {
        if (vehicle == null) {
            return false;
        }
        vehicles.add(vehicle);
        addTourCost(vehicle.getRouteCost());
        return true;
    }

    /******************************** HETERO GREEDY METHODS ********************************/

    private ArrayList<Integer> idList = new ArrayList<>();
    private ArrayList<Integer> demandList = new ArrayList<>();
    private ArrayList<String> stringVehicle = new ArrayList<>();
    ArrayList<String> stringRoute = new ArrayList<>();
    private ArrayList<String> stringCapacity = new ArrayList<>();
    private int vehicleIndex = 1;
    private int stringIndex = 0;
    private ArrayList<String> stringCost = new ArrayList<>();

    public void calculateHetero() {

        for (Vehicle<T, N> v : vehicles) {
            ArrayList<Integer> idArrayList = v.extractCustomerID();
            ArrayList<Integer> demandArrayList = v.extractDemand();
            for (int i = 0; i < idArrayList.size(); i++) {
                idList.add(idArrayList.get(i));
                demandList.add(demandArrayList.get(i));
            }
        }

        // start counting
        stringVehicle.add("Vehicle " + vehicleIndex);

        int capacityA = 0;
        stringRoute.add("0 -> ");

        for (int i = 0; i < idList.size(); i++) {
            int id = idList.get(i);
            int demand = demandList.get(i);

            if (capacityA != 0) {
                int leftOverCapacity = Vehicle.getMaxCapacity() - capacityA;

                while (demand > 0) {
                    if (demand > leftOverCapacity) {
                        demand -= leftOverCapacity;
                        capacityA = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());

                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");

                        leftOverCapacity = Vehicle.getMaxCapacity();

                    } else if (demand < leftOverCapacity) {

                        capacityA += demand;
                        demand = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");

                    } else {
                        demand = 0;
                        capacityA = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());

                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");
                    }
                }
            } else {

                while (demand > 0) {
                    if (demand > Vehicle.getMaxCapacity()) {
                        int tempCapacity = Vehicle.getMaxCapacity();
                        demand -= Vehicle.getMaxCapacity();

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        stringCapacity.add("Capacity: " + tempCapacity);

                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");

                    } else if (demand < Vehicle.getMaxCapacity()) {

                        capacityA = demand;
                        demand = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");

                    } else {
                        int tempCapacity = Vehicle.getMaxCapacity();
                        demand = 0;
                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        stringCapacity.add("Capacity: " + tempCapacity);


                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");
                    }
                }
            }
        }

        if (stringRoute.get(stringIndex).lastIndexOf("0") != stringIndex) {
            if (stringRoute.get(stringIndex).length() < 10) {
                stringRoute.remove(stringIndex);
            } else {
                stringRoute.set(stringIndex, stringRoute.get(stringIndex) + "0");
                if (capacityA != 0) {
                    stringCapacity.add("Capacity: " + capacityA);
                }
            }
        }
    }

    private StringBuilder heteroBasicTour = new StringBuilder();

    public String getHeteroBasicTour() {
        return heteroBasicTour.toString();
    }

    public void calculateHeteroBasic(String tour, ArrayList<Integer> demandAL) {
        idList.clear();

        String[] tourArr = tour.split(",");
        for (int i = 0; i < tourArr.length; i++) {
            if (Integer.parseInt(tourArr[i]) > 0) {
                idList.add(Integer.parseInt(tourArr[i]));
            }
        }

        for (int i = 0; i < demandAL.size(); i++) {
            demandList.add(demandAL.get(i));
        }

        // start counting
        stringVehicle.add("Vehicle " + vehicleIndex);

        int capacityA = 0;
        stringRoute.add("0 -> ");
        heteroBasicTour.append("0,");

        for (int i = 0; i < idList.size(); i++) {
            int id = idList.get(i);
            int demand = demandList.get(i);

            if (capacityA != 0) {
                int leftOverCapacity = Vehicle.getMaxCapacity() - capacityA;

                while (demand > 0) {
                    if (demand > leftOverCapacity) {
                        demand -= leftOverCapacity;
                        capacityA = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        heteroBasicTour.append(id).append(",0,");
                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());

                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");
                        heteroBasicTour.append("0,");

                        leftOverCapacity = Vehicle.getMaxCapacity();

                    } else if (demand < leftOverCapacity) {

                        capacityA += demand;
                        demand = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");
                        heteroBasicTour.append(id).append(",");

                    } else {
                        demand = 0;
                        capacityA = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        heteroBasicTour.append(id).append(",0,");
                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());

                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");
                        heteroBasicTour.append("0,");
                    }
                }
            } else {

                while (demand > 0) {
                    if (demand > Vehicle.getMaxCapacity()) {
                        int tempCapacity = Vehicle.getMaxCapacity();
                        demand -= Vehicle.getMaxCapacity();

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        heteroBasicTour.append(id).append(",0,");
                        stringCapacity.add("Capacity: " + tempCapacity);

                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");
                        heteroBasicTour.append("0,");

                    } else if (demand < Vehicle.getMaxCapacity()) {

                        capacityA = demand;
                        demand = 0;

                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");
                        heteroBasicTour.append(id).append(",");

                    } else {
                        int tempCapacity = Vehicle.getMaxCapacity();
                        //capacityA += demand;
                        demand = 0;
                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
                        heteroBasicTour.append(id).append(",0,");
                        stringCapacity.add("Capacity: " + tempCapacity);

                        vehicleIndex++;
                        stringVehicle.add("Vehicle " + vehicleIndex);

                        stringIndex++;
                        stringRoute.add("0 -> ");
                        heteroBasicTour.append("0,");
                    }
                }
            }
        }

        if (stringRoute.get(stringIndex).lastIndexOf("0") != stringIndex) {
            if (stringRoute.get(stringIndex).length() < 10) {
                stringRoute.remove(stringIndex);
            } else {
                stringRoute.set(stringIndex, stringRoute.get(stringIndex) + "0");
                heteroBasicTour.append("0,0");
                if (capacityA != 0) {
                    stringCapacity.add("Capacity: " + capacityA);
                }
            }
        }
        //System.out.println("Check heteroBasicString: " + heteroBasicTour);
    }

    public Tour<Integer, Double> heteroToString(ArrayList<Double> costAL) {
        stringCost.clear();
        double totalCost = 0;
        StringBuilder sb = new StringBuilder();

        //System.out.println(Color.colorize("\n" + simulation + " Simulation", color));
        sb.append("\n").append(simulation).append(" Simulation");
        //System.out.println(Color.colorize("Tour", color));
        sb.append("\nTour");

        for (int i = 0; i < costAL.size(); i++) {
            stringCost.add(costAL.get(i).toString());
            totalCost += costAL.get(i);
            stringCost.set(i, "Cost: " + costAL.get(i));
        }
        //System.out.println(Color.colorize("Tour Cost: " + totalCost, color));
        sb.append("\nTour Cost: ").append(totalCost);
        for (int i = 0; i < stringRoute.size(); i++) {
            //System.out.println(Color.colorize(stringVehicle.get(i), color));
            sb.append("\n").append(stringVehicle.get(i));
            //System.out.println(Color.colorize(stringRoute.get(i), color));
            sb.append("\n").append(stringRoute.get(i));
            //System.out.println(Color.colorize(stringCapacity.get(i), color));
            sb.append("\n").append(stringCapacity.get(i));
            //System.out.println(Color.colorize(stringCost.get(i), color));
            sb.append("\n").append(stringCost.get(i));
        }

        return createHeteroTour(simulation, totalCost, stringRoute, stringCapacity, stringCost);
        //System.out.println();
        //return sb.toString();
    }
//    public String heteroToString(ArrayList<Double> costAL) {
//        stringCost.clear();
//        double totalCost = 0;
//        StringBuilder sb = new StringBuilder();
//
//        //System.out.println(Color.colorize("\n" + simulation + " Simulation", color));
//        sb.append("\n").append(simulation).append(" Simulation");
//        //System.out.println(Color.colorize("Tour", color));
//        sb.append("\nTour");
//
//        for (int i = 0; i < costAL.size(); i++) {
//            stringCost.add(costAL.get(i).toString());
//            totalCost += costAL.get(i);
//            stringCost.set(i, "Cost: " + costAL.get(i));
//        }
//        //System.out.println(Color.colorize("Tour Cost: " + totalCost, color));
//        sb.append("\nTour Cost: ").append(totalCost);
//        for (int i = 0; i < stringRoute.size(); i++) {
//            //System.out.println(Color.colorize(stringVehicle.get(i), color));
//            sb.append("\n").append(stringVehicle.get(i));
//            //System.out.println(Color.colorize(stringRoute.get(i), color));
//            sb.append("\n").append(stringRoute.get(i));
//            //System.out.println(Color.colorize(stringCapacity.get(i), color));
//            sb.append("\n").append(stringCapacity.get(i));
//            //System.out.println(Color.colorize(stringCost.get(i), color));
//            sb.append("\n").append(stringCost.get(i));
//        }
//
//        //createHeteroTour(simulation, totalCost, stringRoute, stringCapacity, stringCost);
//        //System.out.println();
//        return sb.toString();
//    }
//    public void heteroToString(ArrayList<Double> costAL, String color) {
//        double totalCost = 0;
//        System.out.println(Color.colorize("\n" + simulation + " Simulation", color));
//        System.out.println(Color.colorize("Tour", color));
//
//        ArrayList<String> stringCost = new ArrayList<>();
//        for (int i = 0; i < costAL.size(); i++) {
//            stringCost.add(costAL.get(i).toString());
//            totalCost += costAL.get(i);
//            stringCost.set(i, "Cost: " + costAL.get(i));
//        }
//        System.out.println(Color.colorize("Tour Cost: " + totalCost, color));
//        for (int i = 0; i < stringRoute.size(); i++) {
//            System.out.println(Color.colorize(stringVehicle.get(i), color));
//            System.out.println(Color.colorize(stringRoute.get(i), color));
//            System.out.println(Color.colorize(stringCapacity.get(i), color));
//            System.out.println(Color.colorize(stringCost.get(i), color));
//        }
//        System.out.println();
//    }

    public Tour<Integer, Double> createHeteroTour(String simulation, double totalCost, ArrayList<String> stringRoute,
                                                  ArrayList<String> stringCapacity, ArrayList<String> stringCost) {

        Tour<Integer, Double> heteroTour = new Tour<>(simulation);
        heteroTour.setTourCost(totalCost);

        for (int i = 0; i < stringRoute.size(); i++) {
            Vehicle<Integer, Double> vehicle = new Vehicle<>(i + 1);
            String[] temp = stringRoute.get(i).split(" ");
            for (int j = 0; j < temp.length; j++) {
                if (temp[j].equals("->")) {
                    continue;
                }
                vehicle.addVisitedCustomer(Integer.parseInt(temp[j]));
            }
            String[] tempCost = stringCost.get(i).split(" ");

            for (int j = 0; j < tempCost.length; j++) {
                if (tempCost[j].equals("Cost:")) {
                    //System.out.println("no, it is cost: " + tempCost[j]);
                    continue;
                }
                //System.out.println("check: " + stringCost.get(j));
                vehicle.addRouteCost(Double.parseDouble(tempCost[j]));
            }

            String[] tempCapacity = stringCapacity.get(i).split(" ");
            for (int j = 0; j < tempCapacity.length; j++) {
                if (tempCapacity[j].equals("Capacity:")) {
                    continue;
                }
                vehicle.addCapacity(Integer.parseInt(tempCapacity[j]));
            }
            heteroTour.addVehicle(vehicle);
        }

        return heteroTour;
    }
}

//public class Tour<T extends Comparable<T>,
//        N extends Number & Comparable<N>> implements Comparable<Tour<T, N>> {
//
//    private String simulation;
//    private double tourCost;
//    private ArrayList<Vehicle<T, N>> vehicles;
//
//    public Tour(String simulation) {
//        this.simulation = simulation;
//        this.tourCost = 0;
//        this.vehicles = new ArrayList<>();
//    }
//
//    public String getSimulation() {
//        return simulation;
//    }
//
//    public void setSimulation(String simulation) {
//        this.simulation = simulation;
//    }
//
//    public double getTourCost() {
//        return tourCost;
//    }
//
//    public void setTourCost(double tourCost) {
//        this.tourCost = tourCost;
//    }
//
//    public ArrayList<Vehicle<T, N>> getVehicles() {
//        return vehicles;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        String header = simulation + " Simulation\n"
//                + "Tour\n"
//                + "Tour Cost: " + tourCost + "\n";
//        sb.append(header);
//        for (Vehicle<T, N> v : vehicles) {
//            sb.append(v.toString());
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public int compareTo(Tour<T, N> aTour) {
//        if (this.tourCost < aTour.getTourCost()) {
//            return -1;
//        } else if (this.tourCost > aTour.getTourCost()) {
//            return 1;
//        }
//        return 0;
//    }
//
//    /******************************** EXTRA METHODS ********************************/
//
//    /**
//     * Add route cost of vehicle into tour cost
//     *
//     * @param routeCost Route Cost of the vehicle
//     * @return True if added, else False
//     */
//    private boolean addTourCost(double routeCost) {
//        if (routeCost < 0) {
//            return false;
//        }
//        tourCost += routeCost;
//        return true;
//    }
//
//    /**
//     * Add vehicle after completing a route into the tour
//     *
//     * @param vehicle Vehicle after completing a route
//     * @return True if added, else False
//     */
//    public boolean addVehicle(Vehicle<T, N> vehicle) {
//        if (vehicle == null) {
//            return false;
//        }
//        vehicles.add(vehicle);
//        addTourCost(vehicle.getRouteCost());
//        return true;
//    }
//
//    /******************************** HETERO GREEDY METHODS ********************************/
//
//    private ArrayList<Integer> idList = new ArrayList<>();
//    private ArrayList<Integer> demandList = new ArrayList<>();
//    private ArrayList<String> stringVehicle = new ArrayList<>();
//    ArrayList<String> stringRoute = new ArrayList<>();
//    private ArrayList<String> stringCapacity = new ArrayList<>();
//    private int vehicleIndex = 1;
//    private int stringIndex = 0;
//    private ArrayList<String> stringCost = new ArrayList<>();
//
//    public void calculateHetero() {
//
//        for (Vehicle<T, N> v : vehicles) {
//            ArrayList<Integer> idArrayList = v.extractCustomerID();
//            ArrayList<Integer> demandArrayList = v.extractDemand();
//            for (int i = 0; i < idArrayList.size(); i++) {
//                idList.add(idArrayList.get(i));
//                demandList.add(demandArrayList.get(i));
//            }
//        }
//
//        // start counting
//        stringVehicle.add("Vehicle " + vehicleIndex);
//
//        int capacityA = 0;
//        stringRoute.add("0 -> ");
//
//        for (int i = 0; i < idList.size(); i++) {
//            int id = idList.get(i);
//            int demand = demandList.get(i);
//
//            if (capacityA != 0) {
//                int leftOverCapacity = Vehicle.getMaxCapacity() - capacityA;
//
//                while (demand > 0) {
//                    if (demand > leftOverCapacity) {
//                        demand -= leftOverCapacity;
//                        capacityA = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//
//                        leftOverCapacity = Vehicle.getMaxCapacity();
//
//                    } else if (demand < leftOverCapacity) {
//
//                        capacityA += demand;
//                        demand = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");
//
//                    } else {
//                        demand = 0;
//                        capacityA = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//                    }
//                }
//            } else {
//
//                while (demand > 0) {
//                    if (demand > Vehicle.getMaxCapacity()) {
//                        int tempCapacity = Vehicle.getMaxCapacity();
//                        demand -= Vehicle.getMaxCapacity();
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        stringCapacity.add("Capacity: " + tempCapacity);
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//
//                    } else if (demand < Vehicle.getMaxCapacity()) {
//
//                        capacityA = demand;
//                        demand = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");
//
//                    } else {
//                        int tempCapacity = Vehicle.getMaxCapacity();
//                        demand = 0;
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        stringCapacity.add("Capacity: " + tempCapacity);
//
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//                    }
//                }
//            }
//        }
//
//        if (stringRoute.get(stringIndex).lastIndexOf("0") != stringIndex) {
//            if (stringRoute.get(stringIndex).length() < 10) {
//                stringRoute.remove(stringIndex);
//            } else {
//                stringRoute.set(stringIndex, stringRoute.get(stringIndex) + "0");
//                if (capacityA != 0) {
//                    stringCapacity.add("Capacity: " + capacityA);
//                }
//            }
//        }
//    }
//
//    private StringBuilder heteroBasicTour = new StringBuilder();
//
//    public String getHeteroBasicTour() {
//        return heteroBasicTour.toString();
//    }
//
//    public void calculateHeteroBasic(String tour, ArrayList<Integer> demandAL) {
//        idList.clear();
//
//        String[] tourArr = tour.split(",");
//        for (int i = 0; i < tourArr.length; i++) {
//            if (Integer.parseInt(tourArr[i]) > 0) {
//                idList.add(Integer.parseInt(tourArr[i]));
//            }
//        }
//
//        for (int i = 0; i < demandAL.size(); i++) {
//            demandList.add(demandAL.get(i));
//        }
//
//        // start counting
//        stringVehicle.add("Vehicle " + vehicleIndex);
//
//        int capacityA = 0;
//        stringRoute.add("0 -> ");
//        heteroBasicTour.append("0,");
//
//        for (int i = 0; i < idList.size(); i++) {
//            int id = idList.get(i);
//            int demand = demandList.get(i);
//
//            if (capacityA != 0) {
//                int leftOverCapacity = Vehicle.getMaxCapacity() - capacityA;
//
//                while (demand > 0) {
//                    if (demand > leftOverCapacity) {
//                        demand -= leftOverCapacity;
//                        capacityA = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        heteroBasicTour.append(id).append(",0,");
//                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//                        heteroBasicTour.append("0,");
//
//                        leftOverCapacity = Vehicle.getMaxCapacity();
//
//                    } else if (demand < leftOverCapacity) {
//
//                        capacityA += demand;
//                        demand = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");
//                        heteroBasicTour.append(id).append(",");
//
//                    } else {
//                        demand = 0;
//                        capacityA = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        heteroBasicTour.append(id).append(",0,");
//                        stringCapacity.add("Capacity: " + Vehicle.getMaxCapacity());
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//                        heteroBasicTour.append("0,");
//                    }
//                }
//            } else {
//
//                while (demand > 0) {
//                    if (demand > Vehicle.getMaxCapacity()) {
//                        int tempCapacity = Vehicle.getMaxCapacity();
//                        demand -= Vehicle.getMaxCapacity();
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        heteroBasicTour.append(id).append(",0,");
//                        stringCapacity.add("Capacity: " + tempCapacity);
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//                        heteroBasicTour.append("0,");
//
//                    } else if (demand < Vehicle.getMaxCapacity()) {
//
//                        capacityA = demand;
//                        demand = 0;
//
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> ");
//                        heteroBasicTour.append(id).append(",");
//
//                    } else {
//                        int tempCapacity = Vehicle.getMaxCapacity();
//                        demand = 0;
//                        stringRoute.set(stringIndex, stringRoute.get(stringIndex) + id + " -> 0");
//                        heteroBasicTour.append(id).append(",0,");
//                        stringCapacity.add("Capacity: " + tempCapacity);
//
//
//                        vehicleIndex++;
//                        stringVehicle.add("Vehicle " + vehicleIndex);
//
//                        stringIndex++;
//                        stringRoute.add("0 -> ");
//                        heteroBasicTour.append("0,");
//                    }
//                }
//            }
//        }
//
//        if (stringRoute.get(stringIndex).lastIndexOf("0") != stringIndex) {
//            if (stringRoute.get(stringIndex).length() < 10) {
//                stringRoute.remove(stringIndex);
//            } else {
//                stringRoute.set(stringIndex, stringRoute.get(stringIndex) + "0");
//                heteroBasicTour.append("0,0");
//                if (capacityA != 0) {
//                    stringCapacity.add("Capacity: " + capacityA);
//                }
//            }
//        }
//    }
//
//    public Tour<Integer, Double> heteroToString(ArrayList<Double> costAL) {
//        stringCost.clear();
//        double totalCost = 0;
//        StringBuilder sb = new StringBuilder();
//
//        //System.out.println(Color.colorize("\n" + simulation + " Simulation", color));
//        sb.append("\n").append(simulation).append(" Simulation");
//        //System.out.println(Color.colorize("Tour", color));
//        sb.append("\nTour");
//
//        for (int i = 0; i < costAL.size(); i++) {
//            stringCost.add(costAL.get(i).toString());
//            totalCost += costAL.get(i);
//            stringCost.set(i, "Cost: " + costAL.get(i));
//        }
//        //System.out.println(Color.colorize("Tour Cost: " + totalCost, color));
//        sb.append("\nTour Cost: ").append(totalCost);
//        for (int i = 0; i < stringRoute.size(); i++) {
//            //System.out.println(Color.colorize(stringVehicle.get(i), color));
//            sb.append("\n").append(stringVehicle.get(i));
//            //System.out.println(Color.colorize(stringRoute.get(i), color));
//            sb.append("\n").append(stringRoute.get(i));
//            //System.out.println(Color.colorize(stringCapacity.get(i), color));
//            sb.append("\n").append(stringCapacity.get(i));
//            //System.out.println(Color.colorize(stringCost.get(i), color));
//            sb.append("\n").append(stringCost.get(i));
//        }
//
//        return createHeteroTour(simulation, totalCost, stringRoute, stringCapacity, stringCost);
//        //System.out.println();
//        //return sb.toString();
//    }
////    public String heteroToString(ArrayList<Double> costAL) {
////        stringCost.clear();
////        double totalCost = 0;
////        StringBuilder sb = new StringBuilder();
////
////        //System.out.println(Color.colorize("\n" + simulation + " Simulation", color));
////        sb.append("\n").append(simulation).append(" Simulation");
////        //System.out.println(Color.colorize("Tour", color));
////        sb.append("\nTour");
////
////        for (int i = 0; i < costAL.size(); i++) {
////            stringCost.add(costAL.get(i).toString());
////            totalCost += costAL.get(i);
////            stringCost.set(i, "Cost: " + costAL.get(i));
////        }
////        //System.out.println(Color.colorize("Tour Cost: " + totalCost, color));
////        sb.append("\nTour Cost: ").append(totalCost);
////        for (int i = 0; i < stringRoute.size(); i++) {
////            //System.out.println(Color.colorize(stringVehicle.get(i), color));
////            sb.append("\n").append(stringVehicle.get(i));
////            //System.out.println(Color.colorize(stringRoute.get(i), color));
////            sb.append("\n").append(stringRoute.get(i));
////            //System.out.println(Color.colorize(stringCapacity.get(i), color));
////            sb.append("\n").append(stringCapacity.get(i));
////            //System.out.println(Color.colorize(stringCost.get(i), color));
////            sb.append("\n").append(stringCost.get(i));
////        }
////
////        //createHeteroTour(simulation, totalCost, stringRoute, stringCapacity, stringCost);
////        //System.out.println();
////        return sb.toString();
////    }
////    public void heteroToString(ArrayList<Double> costAL, String color) {
////        double totalCost = 0;
////        System.out.println(Color.colorize("\n" + simulation + " Simulation", color));
////        System.out.println(Color.colorize("Tour", color));
////
////        ArrayList<String> stringCost = new ArrayList<>();
////        for (int i = 0; i < costAL.size(); i++) {
////            stringCost.add(costAL.get(i).toString());
////            totalCost += costAL.get(i);
////            stringCost.set(i, "Cost: " + costAL.get(i));
////        }
////        System.out.println(Color.colorize("Tour Cost: " + totalCost, color));
////        for (int i = 0; i < stringRoute.size(); i++) {
////            System.out.println(Color.colorize(stringVehicle.get(i), color));
////            System.out.println(Color.colorize(stringRoute.get(i), color));
////            System.out.println(Color.colorize(stringCapacity.get(i), color));
////            System.out.println(Color.colorize(stringCost.get(i), color));
////        }
////        System.out.println();
////    }
//
//    public Tour<Integer, Double> createHeteroTour(String simulation, double totalCost, ArrayList<String> stringRoute,
//                                                  ArrayList<String> stringCapacity, ArrayList<String> stringCost) {
//
//        Tour<Integer, Double> heteroTour = new Tour<>(simulation);
//        heteroTour.setTourCost(totalCost);
//
//        for (int i = 0; i < stringRoute.size(); i++) {
//            Vehicle<Integer, Double> vehicle = new Vehicle<>(i + 1);
//            String[] temp = stringRoute.get(i).split(" ");
//            for (int j = 0; j < temp.length; j++) {
//                if (temp[j].equals("->")) {
//                    continue;
//                }
//                vehicle.addVisitedCustomer(Integer.parseInt(temp[j]));
//            }
//            String[] tempCost = stringCost.get(i).split(" ");
//
//            for (int j = 0; j < tempCost.length; j++) {
//                if (tempCost[j].equals("Cost:")) {
//                    //System.out.println("no, it is cost: " + tempCost[j]);
//                    continue;
//                }
//                //System.out.println("check: " + stringCost.get(j));
//                vehicle.addRouteCost(Double.parseDouble(tempCost[j]));
//            }
//
//            String[] tempCapacity = stringCapacity.get(i).split(" ");
//            for (int j = 0; j < tempCapacity.length; j++) {
//                if (tempCapacity[j].equals("Capacity:")) {
//                    continue;
//                }
//                vehicle.addCapacity(Integer.parseInt(tempCapacity[j]));
//            }
//            heteroTour.addVehicle(vehicle);
//        }
//
//        return heteroTour;
//    }
//}
