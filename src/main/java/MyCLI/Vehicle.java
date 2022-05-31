package MyCLI;

import java.util.ArrayList;
import java.util.Random;

public class Vehicle<T extends Comparable<T>,
        N extends Number & Comparable<N>> {

    private static int ORI_MAX_CAPACITY;
    private static int MAX_CAPACITY;
    private int id;
    private int capacity;
    private double routeCost; // Route Cost of vehicle so far
    private ArrayList<T> route;   // customers (vertices) visited by vehicle

    private String[] vehicleTypes = {"motorcycle", "van", "truck"};

    public Vehicle(int id) {
        this.id = id;
        this.capacity = 0;
        this.routeCost = 0;
        this.route = new ArrayList<>();
    }

    public static int getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public static int getOriMaxCapacity() {
        return ORI_MAX_CAPACITY;
    }

    public static void setOriMaxCapacity(int C) {
        ORI_MAX_CAPACITY = C;
    }

    public static void createMaxCapacity(int C) {
        setOriMaxCapacity(C);

        Random randomIndex = new Random();
        int indexOfVehicleType = randomIndex.nextInt(3);

        switch (indexOfVehicleType) {
            case 0:
                MAX_CAPACITY = C / 3;
                displayVehicleType(indexOfVehicleType);
                break;
            case 1:
                MAX_CAPACITY = C / 2;
                displayVehicleType(indexOfVehicleType);
                break;
            case 2:
                MAX_CAPACITY = C;
                displayVehicleType(indexOfVehicleType);
                break;
        }
    }

    public static void setMaxCapacity(int C) {
        MAX_CAPACITY = C;
    }

    private static void displayVehicleType(int indexOfVehicleType) {
        String vehicleDescription = "";
        switch (indexOfVehicleType) {
            case 0:
                vehicleDescription = "Oh no... There's only motorcycles left in the hub."
                        + "\nI can only carry " + MAX_CAPACITY + " items.";
                System.out.println(vehicleDescription);
                String motorcycle = "";
                motorcycle += "\n                       =======                                    ";
                motorcycle += "\n            ______    //@@@@@||                         ";
                motorcycle += "\n           ______    //@@@@@@||        ==[###]             ";
                motorcycle += "\n           ______    ||@@@@@@||          \\\\               ";
                motorcycle += "\n                      ========   ~~~~     \\\\               ";
                motorcycle += "\n           ______     ____________||      //\\\\                   ";
                motorcycle += "\n           ______     ____________\\\\     //  \\\\                      ";
                motorcycle += "\n          ______     //  *******     =====  **\\\\***                      ";
                motorcycle += "\n         ______     // ***********  //_//  ****\\\\****                  ";
                motorcycle += "\n         ______       *****________//     ************                       ";
                motorcycle += "\n                       ***********         **********                   ";
                motorcycle += "\n                        *********           ********                   ";

                System.out.println(motorcycle);

                break;
            case 1:
                vehicleDescription = "Phew, Although there's no truck, a van should suffice!"
                        + "\nI can carry " + MAX_CAPACITY + " items!";
                System.out.println(vehicleDescription);
                String van = "";
                van += "\n             ______________________________________                    ";
                van += "\n             ||                                   \\\\             ";
                van += "\n      ______ ||                                     \\\\            ";
                van += "\n     ______  ||                                       \\\\           ";
                van += "\n    ______   ||                                         \\\\           ";
                van += "\n   ______    ||                                           \\\\          ";
                van += "\n  ______     ||                                            ||      ";
                van += "\n ______      ||    *******                 *******         ||      ";
                van += "\n             [|  ***********             ***********       |]      ";
                van += "\n             ===*************===========*************=======      ";
                van += "\n                 ***********             ***********             ";
                van += "\n                  ********                ********           ";

                System.out.println(van);
                break;
            case 2:
                vehicleDescription = "Hooray, a truck will be used! I can finish my work earlier today."
                        + "\nI can carry " + MAX_CAPACITY + " items!";
                System.out.println(vehicleDescription);
                String truck = "";
                truck += "\n                  ______________________________________                                                                   ";
                truck += "\n                  ||                                  ||                   ";
                truck += "\n                  ||                                  ||                   ";
                truck += "\n                  ||                                  ||                   ";
                truck += "\n                  ||                                  ||                  ";
                truck += "\n                  ||                                  ||    _________         ";
                truck += "\n      ______      ||                                  ||    ||       \\\\        ";
                truck += "\n     ______       ||                                  ||    ||        \\\\      ";
                truck += "\n    ______        ||                                  ||    ||         ||      ";
                truck += "\n   ______         ||                                  ||    ||         ||      ";
                truck += "\n  ______          ||                                  ||====||         ||       ";
                truck += "\n ______           ||    *******                 *******     ||  ****   ||              ";
                truck += "\n                  [|  ***********             ***********   ===******====                           ";
                truck += "\n                  ===*************===========*************    ********                   ";
                truck += "\n                      ***********             ***********      ******                 ";
                truck += "\n                       ********                ********         ****                                                  ";

                System.out.println(truck);
                break;
        }
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

    // HETERO GREEDY
    public ArrayList<Integer> extractCustomerID() {
        ArrayList<Integer> idArrayList = new ArrayList<>();
        //System.out.println("CHECK ROUTE: " + route.toString());
        for (int i = 0; i < route.size(); i++) {
            if (!route.get(i).equals(0)) {
                idArrayList.add((Integer) route.get(i));
            }
        }
        return idArrayList;
    }

    public ArrayList<Integer> extractDemand() {
        ArrayList<Integer> demand = new ArrayList<>();
        for (int i = 0; i < demandAL.size(); i++) {
            if (!demandAL.get(i).equals(0)) {
                demand.add(demandAL.get(i));
            }
        }
        return demand;
    }
    private ArrayList<Integer> demandAL = new ArrayList<>();

    public void addDemandAL(int id) {
        demandAL.add(id);
    }

    public ArrayList<Integer> getDemandAL() {
        return demandAL;
    }


    //********************************** EXTRA METHODS ***********************************

    /**
     * Check if vehicle is overloaded by comparing expected capacity and MAX_CAPACITY
     * Expected Capacity = Current Capacity + Customer Demand
     *
     * @param demand Customer demand size
     * @return True if vehicle is overloaded else False
     */
    public boolean isOverloaded(int demand) {
        int expectedCapacity = this.capacity + demand;
        return (expectedCapacity > ORI_MAX_CAPACITY);
    }

    /**
     * Check if the demand of customer is greater than max capacity
     *
     * @param demand
     * @return
     */
    public boolean demandMoreThanCapacity(int demand) {
        return (demand > MAX_CAPACITY);
    }

    /**
     * Add the customer demand size to vehicle capacity if not overloaded
     *
     * @param demand Customer demand size
     * @return True if capacity is added else False
     */
    public boolean addCapacity(int demand) {
        if (isOverloaded(demand)) {
            return false;
        }
        this.capacity += demand;
        return true;
    }

    /**
     * Add customer to the visited list
     *
     * @param customerInfo Customer Info
     */
    public void addVisitedCustomer(T customerInfo) {
        route.add(customerInfo);
    }

    /**
     * Add edge weight to the route cost of vehicle
     *
     * @param cost Edge weight
     */
    public void addRouteCost(double cost) {
        this.routeCost += cost;
    }

}

//public class Vehicle<T extends Comparable<T>,
//        N extends Number & Comparable<N>> {
//
//    private static int ORI_MAX_CAPACITY;
//    private static int MAX_CAPACITY;
//    private int id;
//    private int capacity;
//    private double routeCost; // Route Cost of vehicle so far
//    private ArrayList<T> route;   // customers (vertices) visited by vehicle
//
//    private String[] vehicleTypes = {"motorcycle", "van", "truck"};
//
//    public Vehicle(int id) {
//        this.id = id;
//        this.capacity = 0;
//        this.routeCost = 0;
//        this.route = new ArrayList<>();
//    }
//
//    public static int getMaxCapacity() {
//        return MAX_CAPACITY;
//    }
//
//    public static int getOriMaxCapacity() {
//        return ORI_MAX_CAPACITY;
//    }
//
//    public static void setOriMaxCapacity(int C) {
//        ORI_MAX_CAPACITY = C;
//    }
//
//    public static void setMaxCapacity(int C) {
//        setOriMaxCapacity(C);
//
//        Random randomIndex = new Random();
//        int indexOfVehicleType = randomIndex.nextInt(3);
//
//        switch (indexOfVehicleType) {
//            case 0:
//                MAX_CAPACITY = C / 3;
//                displayVehicleType(indexOfVehicleType);
//                break;
//            case 1:
//                MAX_CAPACITY = C / 2;
//                displayVehicleType(indexOfVehicleType);
//                break;
//            case 2:
//                MAX_CAPACITY = C;
//                displayVehicleType(indexOfVehicleType);
//                break;
//        }
//    }
//
//
//    private static void displayVehicleType(int indexOfVehicleType) {
//        String vehicleDescription = "";
//        switch (indexOfVehicleType) {
//            case 0:
//                vehicleDescription = "Oh no... There's only motorcycles left in the hub."
//                        + "\nI can only carry " + MAX_CAPACITY + " items.";
//                System.out.println(vehicleDescription);
//                String motorcycle = "";
//                motorcycle += "\n                       =======                                    ";
//                motorcycle += "\n            ______    //@@@@@||                         ";
//                motorcycle += "\n           ______    //@@@@@@||        ==[###]             ";
//                motorcycle += "\n           ______    ||@@@@@@||          \\\\               ";
//                motorcycle += "\n                      ========   ~~~~     \\\\               ";
//                motorcycle += "\n           ______     ____________||      //\\\\                   ";
//                motorcycle += "\n           ______     ____________\\\\     //  \\\\                      ";
//                motorcycle += "\n          ______     //  *******     =====  **\\\\***                      ";
//                motorcycle += "\n         ______     // ***********  //_//  ****\\\\****                  ";
//                motorcycle += "\n         ______       *****________//     ************                       ";
//                motorcycle += "\n                       ***********         **********                   ";
//                motorcycle += "\n                        *********           ********                   ";
//
//                System.out.println(motorcycle);
//
//                break;
//            case 1:
//                vehicleDescription = "Phew, Although there's no truck, a van should suffice!"
//                        + "\nI can carry " + MAX_CAPACITY + " items!";
//                System.out.println(vehicleDescription);
//                String van = "";
//                van += "\n             ______________________________________                    ";
//                van += "\n             ||                                   \\\\             ";
//                van += "\n      ______ ||                                     \\\\            ";
//                van += "\n     ______  ||                                       \\\\           ";
//                van += "\n    ______   ||                                         \\\\           ";
//                van += "\n   ______    ||                                           \\\\          ";
//                van += "\n  ______     ||                                            ||      ";
//                van += "\n ______      ||    *******                 *******         ||      ";
//                van += "\n             [|  ***********             ***********       |]      ";
//                van += "\n             ===*************===========*************=======      ";
//                van += "\n                 ***********             ***********             ";
//                van += "\n                  ********                ********           ";
//
//                System.out.println(van);
//                break;
//            case 2:
//                vehicleDescription = "Hooray, a truck will be used! I can finish my work earlier today."
//                        + "\nI can carry " + MAX_CAPACITY + " items!";
//                System.out.println(vehicleDescription);
//                String truck = "";
//                truck += "\n                  ______________________________________                                                                   ";
//                truck += "\n                  ||                                  ||                   ";
//                truck += "\n                  ||                                  ||                   ";
//                truck += "\n                  ||                                  ||                   ";
//                truck += "\n                  ||                                  ||                  ";
//                truck += "\n                  ||                                  ||    _________         ";
//                truck += "\n      ______      ||                                  ||    ||       \\\\        ";
//                truck += "\n     ______       ||                                  ||    ||        \\\\      ";
//                truck += "\n    ______        ||                                  ||    ||         ||      ";
//                truck += "\n   ______         ||                                  ||    ||         ||      ";
//                truck += "\n  ______          ||                                  ||====||         ||       ";
//                truck += "\n ______           ||    *******                 *******     ||  ****   ||              ";
//                truck += "\n                  [|  ***********             ***********   ===******====                           ";
//                truck += "\n                  ===*************===========*************    ********                   ";
//                truck += "\n                      ***********             ***********      ******                 ";
//                truck += "\n                       ********                ********         ****                                                  ";
//
//                System.out.println(truck);
//                break;
//        }
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getCapacity() {
//        return capacity;
//    }
//
//    public void setCapacity(int capacity) {
//        this.capacity = capacity;
//    }
//
//    public double getRouteCost() {
//        return routeCost;
//    }
//
//    public void setRouteCost(double routeCost) {
//        this.routeCost = routeCost;
//    }
//
//    public ArrayList<T> getRoute() {
//        return route;
//    }
//
//    /**
//     * Generate the route taken by vehicle where each customer is separated by "->"
//     *
//     * @return Route taken by vehicle with separator "->"
//     */
//    private String constructRouteString() {
//        String separator = "";
//        StringBuilder vehicleRoute = new StringBuilder();
//
//        for (T customer : route) {
//            vehicleRoute.append(separator).append(customer);
//            separator = " -> ";
//        }
//        vehicleRoute.append("\n");
//        return vehicleRoute.toString();
//    }
//
//    @Override
//    public String toString() {
//        return "Vehicle " + getId() + "\n"
//                + constructRouteString()
//                + "Capacity: " + this.capacity + "\n"
//                + "Cost: " + this.routeCost + "\n";
//    }
//
//    // HETERO GREEDY
//    public ArrayList<Integer> extractCustomerID() {
//        ArrayList<Integer> idArrayList = new ArrayList<>();
//        //System.out.println("CHECK ROUTE: " + route.toString());
//        for (int i = 0; i < route.size(); i++) {
//            if (!route.get(i).equals(0)) {
//                idArrayList.add((Integer) route.get(i));
//            }
//        }
//        return idArrayList;
//    }
//
//    public ArrayList<Integer> extractDemand() {
//        ArrayList<Integer> demand = new ArrayList<>();
//        for (int i = 0; i < demandAL.size(); i++) {
//            if (!demandAL.get(i).equals(0)) {
//                demand.add(demandAL.get(i));
//            }
//        }
//        return demand;
//    }
//    private ArrayList<Integer> demandAL = new ArrayList<>();
//
//    public void addDemandAL(int id) {
//        demandAL.add(id);
//    }
//
//    public ArrayList<Integer> getDemandAL() {
//        return demandAL;
//    }
//
//
//    //********************************** EXTRA METHODS ***********************************
//
//    /**
//     * Check if vehicle is overloaded by comparing expected capacity and MAX_CAPACITY
//     * Expected Capacity = Current Capacity + Customer Demand
//     *
//     * @param demand Customer demand size
//     * @return True if vehicle is overloaded else False
//     */
//    public boolean isOverloaded(int demand) {
//        int expectedCapacity = this.capacity + demand;
//        return (expectedCapacity > ORI_MAX_CAPACITY);
//    }
//
//    /**
//     * Check if the demand of customer is greater than max capacity
//     *
//     * @param demand
//     * @return
//     */
//    public boolean demandMoreThanCapacity(int demand) {
//        return (demand > MAX_CAPACITY);
//    }
//
//    /**
//     * Add the customer demand size to vehicle capacity if not overloaded
//     *
//     * @param demand Customer demand size
//     * @return True if capacity is added else False
//     */
//    public boolean addCapacity(int demand) {
//        if (isOverloaded(demand)) {
//            return false;
//        }
//        this.capacity += demand;
//        return true;
//    }
//
//    /**
//     * Add customer to the visited list
//     *
//     * @param customerInfo Customer Info
//     */
//    public void addVisitedCustomer(T customerInfo) {
//        route.add(customerInfo);
//    }
//
//    /**
//     * Add edge weight to the route cost of vehicle
//     *
//     * @param cost Edge weight
//     */
//    public void addRouteCost(double cost) {
//        this.routeCost += cost;
//    }
//
//}
