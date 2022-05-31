package MyCLI;

import java.util.ArrayList;
import java.util.Collections;

public class Graph<T extends Comparable<T>, N extends Number & Comparable<N>> {
    private Customer<T, N> head;
    private int size;
    private static boolean DEBUG = false;   // set DEBUG to true for showing extra useful messages

    public Graph() {
        this.head = null;
        this.size = 0;
    }

    /********************************** START HELPER METHODS ***********************************/

    public void setDebugMode(boolean debug) {
        DEBUG = debug;
    }

    public boolean getDebugMode() {
        return DEBUG;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public int getSize() {
        return this.size;
    }

    public Customer<T, N> getHead() {
        return head;
    }

    public boolean isNotValidVertex(T customerInfo) {
        if (head == null) {
            if (DEBUG) {
                System.out.println("Head is null");
            }
            return true;
        }
        if (!hasCustomer(customerInfo)) {
            if (DEBUG) {
                System.out.println("Customer does not exist");
            }
            return true;
        }
        return false;
    }

    public boolean isNotValidVertices(T source, T destination) {
        return (isNotValidVertex(source) || isNotValidVertex(destination));
    }

    /**
     * Calculate the Euclidean distance (edge weight) between source and destination (if exists)
     *
     * @return Euclidean distance between source and destination if exists, otherwise -1
     */
    public double computeEuclidean(T source, T destination) {
        if (isNotValidVertices(source, destination)) {
            return -1;
        }

        // locate source vertex
        Customer<T, N> src = head;
        while (src != null) {
            if (src.getID().compareTo(source) == 0) {
                // locate destination vertex
                Customer<T, N> dest = head;
                while (dest != null) {
                    if (dest.getID().compareTo(destination) == 0) {
                        int x1 = src.getX(), y1 = src.getY();     // source coordinates
                        int x2 = dest.getX(), y2 = dest.getY();   // destination coordinates

                        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                    }
                    dest = dest.getNextCustomer();
                }
            }
            src = src.getNextCustomer();
        }
        return -1;
    }


    /********************************** END HELPER METHODS ***********************************



     /********************************** START CUSTOMER METHODS ***********************************/

    public boolean hasCustomer(T customerInfo) {
        if (head == null) {
            return false;
        }

        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                return true;
            }
            current = current.getNextCustomer();
        }
        return false;
    }


    public boolean addCustomer(T customerInfo, int x, int y, int demand) {
        // Ensure no duplicate customer
        if (hasCustomer(customerInfo)) {
            if (DEBUG) {
                System.out.println("Failed to add customer. Already existed in graph!");
            }
            return false;
        }

        Customer<T, N> newCustomer = new Customer<>(customerInfo, x, y, demand, null);
        Customer<T, N> current = head;
        if (current == null) {
            head = newCustomer;  // depot

            if (DEBUG) {
                System.out.println("Added Depot: " + newCustomer);
            }
        } else {
            Customer<T, N> previous = head;
            while (current != null) {
                previous = current;
                current = current.getNextCustomer();
            }
            previous.setNextCustomer(newCustomer);

            if (DEBUG) {
                System.out.println("Added Customer: " + newCustomer);
            }
        }
        size++;
        return true;
    }


    public int getIndegOf(T customerInfo) {
        int notFound = -1;
        if (isNotValidVertex(customerInfo)) {
            return notFound;
        }

        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                return current.getIndeg();
            }
            current = current.getNextCustomer();
        }
        return notFound;
    }


    public int getOutdegOf(T customerInfo) {
        int notFound = -1;
        if (isNotValidVertex(customerInfo)) {
            return notFound;
        }

        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                return current.getOutdeg();
            }
            current = current.getNextCustomer();
        }
        return notFound;
    }


    /**
     * Get index of specified customer in graph
     *
     * @param customerInfo Customer Info
     * @return Index of the customer if exists, otherwise -1
     */
    public int getIndexOf(T customerInfo) {
        int notFound = -1;
        if (isNotValidVertex(customerInfo)) {
            return notFound;
        }

        Customer<T, N> current = head;
        int index = 0;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                return index;
            }
            current = current.getNextCustomer();
            index++;
        }
        return notFound;
    }


    /**
     * Retrieve customer info at specified index (if index is valid)
     *
     * @param index Customer index
     * @return Customer Info
     */
    public T getCustomerAt(int index) {
        if (head == null) {
            return null;
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Customer<T, N> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNextCustomer();
        }
        return current.getID();
    }


    /**
     * Get demand size of specified customer (if exists)
     *
     * @param customerInfo Customer Info
     * @return Demand size of the customer if exists, otherwise -1
     */
    public int getDemandOf(T customerInfo) {
        int notFound = -1;
        if (isNotValidVertex(customerInfo)) {
            return notFound;
        }

        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                return current.getDemand();
            }
            current = current.getNextCustomer();
        }
        return notFound;
    }


    public ArrayList<T> getAllCustomersInfo() {
        if (head == null) {
            return null;
        }

        ArrayList<T> list = new ArrayList<>();
        Customer<T, N> current = head;
        while (current != null) {
            list.add(current.getID());
            current = current.getNextCustomer();
        }
        return list;
    }


    public ArrayList<T> getNeighbours(T customerInfo) {
        if (isNotValidVertex(customerInfo)) {
            return null;
        }

        ArrayList<T> list = new ArrayList<>();
        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                Edge<T, N> currentEdge = current.getFirstEdge();
                while (currentEdge != null) {
                    list.add(currentEdge.getToCustomer().getID());
                    currentEdge = currentEdge.getNextEdge();
                }
                break;
            }
            current = current.getNextCustomer();
        }
        return list;
    }


    public void setAllCustomerVisited(boolean visited) {
        Customer<T, N> current = head;
        while (current != null) {
            current.setVisited(visited);
            current = current.getNextCustomer();
        }
    }


    public void setAllCustomerChecked(boolean checked) {
        Customer<T, N> current = head;
        while (current != null) {
            current.setChecked(checked);
            current = current.getNextCustomer();
        }
    }


    /**
     * Get object reference to specified customer
     *
     * @param customerInfo Customer Info
     * @return Customer object of specified customer
     */
    public Customer<T, N> getCustomerObject(T customerInfo) {
        if (isNotValidVertex(customerInfo)) {
            return null;
        }

        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                return current;
            }
            current = current.getNextCustomer();
        }
        return null;
    }


    /**
     * Check if all customers are visited (excluding depot)
     *
     * @return True if all customers are visited, else False
     */
    public boolean allCustomerIsVisited() {
        if (head == null) {
            return false;
        }
        Customer<T, N> current = head.getNextCustomer();  // skip depot
        while (current != null) {
            if (!current.getVisited()) {
                return false;
            }
            current = current.getNextCustomer();
        }
        return true;
    }

    /********************************** END CUSTOMER METHODS ***********************************/


    /****************************** START EDGE METHODS **************************************/

    private boolean addDirectedEdge(T source, T destination, N weight) {
        if (isNotValidVertices(source, destination)) {
            return false;
        }

        if (hasEdge(source, destination)) {
            return false;
        }

        Customer<T, N> srcCustomer = head;
        while (srcCustomer != null) {
            if (srcCustomer.getID().compareTo(source) == 0) {

                Customer<T, N> destCustomer = head;
                while (destCustomer != null) {
                    if (destCustomer.getID().compareTo(destination) == 0) {
                        Edge<T, N> currentEdge = srcCustomer.getFirstEdge();
                        Edge<T, N> newEdge = new Edge<>(destCustomer, weight, currentEdge);

                        srcCustomer.setFirstEdge(newEdge);
                        srcCustomer.incrementOutdeg();
                        destCustomer.incrementIndeg();

                        if (DEBUG) {
                            System.out.printf("Edge added from %s to %s with weight ", source, destination);
                            System.out.println(weight);
                        }
                        return true;
                    }
                    destCustomer = destCustomer.getNextCustomer();
                }
            }
            srcCustomer = srcCustomer.getNextCustomer();
        }
        return false;
    }


    public boolean addUndirectedEdge(T source, T destination, N weight) {
        boolean a = addDirectedEdge(source, destination, weight);
        boolean b = addDirectedEdge(destination, source, weight);
        return (a && b);
    }


    public boolean removeEdge(T source, T destination) {
        if (isNotValidVertices(source, destination)) {
            return false;
        }

        // Check if path exists from source to destination
        if (!hasEdge(source, destination)) {
            return false;
        }

        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(source) == 0) {
                Edge<T, N> currentEdge = current.getFirstEdge();
                boolean removed = false;

                // Case 1: Edge to be deleted is first edge
                if (currentEdge.getToCustomer().getID().compareTo(destination) == 0) {
                    Edge<T, N> nextEdge = currentEdge.getNextEdge();
                    current.setFirstEdge(nextEdge);
                    currentEdge.setNextEdge(null);
                    removed = true;
                }
                // Case 2: Edge to be deleted is not first edge, traverse to find the edge
                else {
                    Edge<T, N> prevEdge = currentEdge;
                    currentEdge = currentEdge.getNextEdge();

                    while (currentEdge != null) {
                        if (currentEdge.getToCustomer().getID().compareTo(destination) == 0) {
                            Edge<T, N> nextEdge = currentEdge.getNextEdge();
                            prevEdge.setNextEdge(nextEdge);
                            currentEdge.setNextEdge(null);
                            removed = true;
                            break;
                        }
                        prevEdge = currentEdge;
                        currentEdge = currentEdge.getNextEdge();
                    }
                }

                // Ensure one of the remove operations is performed
                if (removed) {
                    current.decrementOutdeg();
                    currentEdge.getToCustomer().decrementIndeg();

                    if (DEBUG) {
                        System.out.printf("Edge removed from %s to %s\n", source, destination);
                    }
                    return true;
                }
            }
            current = current.getNextCustomer();
        }
        return false;
    }


    public boolean hasEdge(T source, T destination) {
        if (isNotValidVertices(source, destination)) {
            return false;
        }

        Customer<T, N> srcCustomer = head;
        while (srcCustomer != null) {
            if (srcCustomer.getID().compareTo(source) == 0) {

                Edge<T, N> currentEdge = srcCustomer.getFirstEdge();
                while (currentEdge != null) {
                    if (currentEdge.getToCustomer().getID().compareTo(destination) == 0) {
                        return true;
                    }
                    currentEdge = currentEdge.getNextEdge();
                }
            }
            srcCustomer = srcCustomer.getNextCustomer();
        }
        return false;
    }


    public void printEdges() {
        Customer<T, N> current = head;
        while (current != null) {
            System.out.printf("# %s : ", current.getID());
            Edge<T, N> currentEdge = current.getFirstEdge();
            while (currentEdge != null) {
                System.out.printf("[%s, %s] ", current.getID(), currentEdge.getToCustomer().getID());
                currentEdge = currentEdge.getNextEdge();
            }
            System.out.println();
            current = current.getNextCustomer();
        }
    }


    /**
     * Get distance between source and destination if they are connected
     *
     * @param source      Source Customer Info
     * @param destination Destination Customer Info
     * @return Edge weight between source and destination if connected, otherwise null
     */
    public N getDistanceBetween(T source, T destination) {
        if (isNotValidVertices(source, destination)) {
            return null;
        }

        if (!hasEdge(source, destination)) {
            return null;
        }

        // locate source vertex
        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(source) == 0) {
                // locate edge
                Edge<T, N> currentEdge = current.getFirstEdge();
                while (currentEdge != null) {
                    if (currentEdge.getToCustomer().getID().compareTo(destination) == 0) {
                        return currentEdge.getWeight();     // Path Cost
                    }
                    currentEdge = currentEdge.getNextEdge();
                }
            }
            current = current.getNextCustomer();
        }
        return null;
    }


    /**
     * Get the list of edges connected to customer(if exists) based on flags
     *
     * @param customerInfo  Customer Infoding order, else False
     * @return List of the edges connected to customer
     */
    public ArrayList<Edge<T, N>> getEdges(T customerInfo) {
        if (isNotValidVertex(customerInfo)) {
            return null;
        }

        Customer<T, N> current = head;
        ArrayList<Edge<T, N>> list = new ArrayList<>();
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                Edge<T, N> currentEdge = current.getFirstEdge();
                while (currentEdge != null) {
                    Customer<T, N> toCustomer = currentEdge.getToCustomer();
                    list.add(currentEdge);
                    currentEdge = currentEdge.getNextEdge();
                }
                break;
            }
            current = current.getNextCustomer();
        }
        return list;
    }


    public ArrayList<Edge<T, N>> getUnvisitedEdges(T customerInfo) {
        if (isNotValidVertex(customerInfo)) {
            return null;
        }

        ArrayList<Edge<T, N>> list = new ArrayList<>();
        Customer<T, N> current = head;
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                Edge<T, N> currentEdge = current.getFirstEdge();
                while (currentEdge != null) {
                    Customer<T, N> toCustomer = currentEdge.getToCustomer();
                    if (!toCustomer.getVisited()) {
                        list.add(currentEdge);
                    }
                    currentEdge = currentEdge.getNextEdge();
                }
                break;
            }
            current = current.getNextCustomer();
        }
        return list;
    }


    public ArrayList<Edge<T, N>> getUncheckedEdges(T customerInfo) {
        if (isNotValidVertex(customerInfo)) {
            return null;
        }

        Customer<T, N> current = head;
        ArrayList<Edge<T, N>> list = new ArrayList<>();
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                Edge<T, N> currentEdge = current.getFirstEdge();
                while (currentEdge != null) {
                    Customer<T, N> toCustomer = currentEdge.getToCustomer();
                    if (!toCustomer.getChecked()) {
                        list.add(currentEdge);
                    }
                    currentEdge = currentEdge.getNextEdge();
                }
                break;
            }
            current = current.getNextCustomer();
        }
        return list;
    }


    public ArrayList<Edge<T, N>> getUncheckedUnvisitedEdges(T customerInfo) {
        if (isNotValidVertex(customerInfo)) {
            return null;
        }

        Customer<T, N> current = head;
        ArrayList<Edge<T, N>> list = new ArrayList<>();
        while (current != null) {
            if (current.getID().compareTo(customerInfo) == 0) {
                Edge<T, N> currentEdge = current.getFirstEdge();
                while (currentEdge != null) {
                    Customer<T, N> toCustomer = currentEdge.getToCustomer();
                    if (!toCustomer.getChecked() && !toCustomer.getVisited()) {
                        list.add(currentEdge);
                    }
                    currentEdge = currentEdge.getNextEdge();
                }
                break;
            }
            current = current.getNextCustomer();
        }
        return list;
    }


/****************************** END EDGE METHODS **************************************/

}