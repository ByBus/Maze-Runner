package maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edge implements Comparable<Edge> {
    List<Node> connectedNodes = new ArrayList<>();
    private Node from;
    private Node to;
    int weight;

    public Edge(Node from, Node to, int weight) {
        connectedNodes.add(from);
        connectedNodes.add(to);
        this.setFrom(from);
        this.setTo(to);
        this.weight = weight;
    }

    @Override
    public String toString() {
        return getFrom() + " -> " + getTo() + " |" + weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(getFrom(), edge.getFrom()) && Objects.equals(getTo(), edge.getTo())
                || Objects.equals(getFrom(), edge.getTo()) && Objects.equals(getTo(), edge.getFrom());
    }

    @Override
    public int compareTo(Edge edge) {
        return Integer.compare(weight, edge.weight);
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }
}
