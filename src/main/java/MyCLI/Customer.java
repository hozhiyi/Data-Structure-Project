package MyCLI;

public class Customer <T extends Comparable<T>,
                       N extends Number & Comparable<N>> extends Vertex<T, N> {

    private int demand;
    private boolean visited;
    private boolean checked;

    public Customer(T ID, int x, int y, int demand, Customer<T, N> nextCustomer) {
        super(ID, x, y, nextCustomer);
        this.demand = demand;
        this.visited = false;
        this.checked = false;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public T getID() {
        return super.getVertexInfo();
    }

    public void setID(T customerInfo) {
        super.setVertexInfo(customerInfo);
    }

    public Customer<T, N> getNextCustomer() {
        return (Customer<T, N>) super.getNextVertex();
    }

    public void setNextCustomer(Customer<T, N> nextCustomer) {
        super.setNextVertex(nextCustomer);
    }

    public boolean getVisited() {
        return this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "" + getID();
    }

    //********************************** EXTRA METHODS ***********************************


}
