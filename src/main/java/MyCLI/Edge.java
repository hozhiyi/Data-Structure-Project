package MyCLI;

public class Edge <T extends Comparable<T>,
                   N extends Number & Comparable<N>> implements Comparable<Edge<T, N>> {

    private Customer<T, N> toCustomer;
    private N weight;
    private Edge<T, N> nextEdge;

    public Edge(Customer<T, N> destination, N weight, Edge<T, N> nextEdge) {
        this.toCustomer = destination;
        this.weight = weight;
        this.nextEdge = nextEdge;
    }

    public Customer<T, N> getToCustomer() {
        return toCustomer;
    }

    public void setToCustomer(Customer<T, N> destination) {
        this.toCustomer = destination;
    }

    public N getWeight() {
        return weight;
    }

    public void setWeight(N weight) {
        this.weight = weight;
    }

    public Edge<T, N> getNextEdge() {
        return nextEdge;
    }

    public void setNextEdge(Edge<T, N> nextEdge) {
        this.nextEdge = nextEdge;
    }

    public int compareTo(Edge<T, N> edge) {
        return (this.weight.compareTo(edge.getWeight()));
    }

    @Override
    public String toString() {
        return "Edge{" +
                "toCustomer=" + toCustomer +
                ", weight=" + weight +
                ", nextEdge=" + nextEdge +
                '}';
    }

    //********************************** EXTRA METHODS ***********************************



}
