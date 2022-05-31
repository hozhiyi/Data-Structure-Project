package MyCLI;

public abstract class Vertex <T extends Comparable<T>,
                              N extends Number & Comparable<N>> {

    private T vertexInfo;
    private int x, y;                   // x, y coordinates
    private int indeg, outdeg;          // In-Degree, Out-Degree
    private Vertex<T, N> nextVertex;
    private Edge<T, N> firstEdge;

    public Vertex(T vertexInfo, int x, int y, Vertex<T, N> nextVertex) {
        this.x = x;
        this.y = y;
        this.indeg = 0;
        this.outdeg = 0;
        this.vertexInfo = vertexInfo;
        this.nextVertex = nextVertex;
        this.firstEdge = null;
    }

    public T getVertexInfo() {
        return vertexInfo;
    }

    public void setVertexInfo(T vertexInfo) {
        this.vertexInfo = vertexInfo;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIndeg() {
        return indeg;
    }

    public void setIndeg(int indeg) {
        this.indeg = indeg;
    }

    public int getOutdeg() {
        return outdeg;
    }

    public void setOutdeg(int outdeg) {
        this.outdeg = outdeg;
    }

    public Vertex<T, N> getNextVertex() {
        return nextVertex;
    }

    public void setNextVertex(Vertex<T, N> nextVertex) {
        this.nextVertex = nextVertex;
    }

    public Edge<T, N> getFirstEdge() {
        return firstEdge;
    }

    public void setFirstEdge(Edge<T, N> edge) {
        this.firstEdge = edge;
    }

    //********************************** EXTRA METHODS ***********************************

    public void incrementIndeg() {
        this.indeg += 1;
    }

    public void decrementIndeg() {
        this.indeg -= 1;
    }

    public void incrementOutdeg() {
        this.outdeg += 1;
    }

    public void decrementOutdeg() {
        this.outdeg -= 1;
    }

}
