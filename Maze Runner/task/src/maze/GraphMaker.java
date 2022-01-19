package maze;

import java.io.Serializable;
import java.util.*;

public class GraphMaker implements Serializable {
    private static final long serialVersionUID = 1424735767359371007L;
    private int width;
    private int height;
    private Node[][] matrix;
    private transient Map<Node, List<Edge>> graph;

    public GraphMaker(int height, int weight) {
        make(height, weight);
    }

    public GraphMaker() { }

    public void make(int height, int weight) {
        this.width = weight;
        this.height = height;
        matrix = new Node[height][width];
        graph = new HashMap<>();
        initMatrix();
        createGraph();
    }

    private void initMatrix() {
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Node node = new Node(i);
                node.setCoordinates(y, x);
                matrix[y][x] = node;
                graph.putIfAbsent(node, new ArrayList<>());
                i++;
            }
        }
    }

    private void createGraph() {
        Random rand = new Random();
        for (int y = 1; y < height - 1; y += 2) {
            for (int x = 1; x < width - 1; x += 2) {
                Node node = matrix[y][x];
                for (int yAdjacent : List.of(y - 2, y, y + 2)) {
                    for (int xAdjacent : List.of(x - 2, x, x + 2)) {
                        if ((x == xAdjacent && y == yAdjacent) || (x != xAdjacent && y != yAdjacent)) {
                            continue;
                        }
                        if (yAdjacent == height - 1) {
                            yAdjacent -= 1;
                        }
                        if (xAdjacent == width - 1) {
                            xAdjacent -= 1;
                        }
                        try {
                            Node adjacentNode = matrix[yAdjacent][xAdjacent];
                            int weight = rand.nextInt(width * height) + 1;
                            addEdge(node, adjacentNode, weight);
                        } catch (IndexOutOfBoundsException ignore) { }
                    }
                }
            }
        }
    }

    private void addEdge(Node from, Node to, int weight) {
        Edge edge = new Edge(from, to, weight);
        List<Edge> edges = graph.get(from);
        if (!edges.contains(edge)) {
            graph.get(from).add(edge);
            graph.get(to).add(edge.flip());
        }
    }

    @Override
    public String toString() {
        return graph.toString();
    }

    public Map<Node, List<Edge>> getGraph() {
        return graph;
    }

    public Node getStart() {
        return matrix[1][1];
    }

    public Node[][] getMatrix() {
        return matrix;
    }
}
