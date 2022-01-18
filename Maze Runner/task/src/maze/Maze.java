package maze;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Maze {
    private static final String WALL ="\u2588\u2588";
    private static final String PASS ="  ";
    private Graph graph;
    private LinkedHashMap<Node, List<Edge>> graphNodes;
    private LinkedHashMap<Node, List<Edge>> spanningTree;

    private PriorityQueue<Edge> queue;
    private Set<Node> visitedNodes;

    private enum Side {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public Maze(Graph graph) {
        this.graph = graph;
        this.graphNodes = graph.getGraph();
        init(graph);
    }

    public void init(Graph graph) {
        this.graph = graph;
        this.graphNodes = graph.getGraph();
        spanningTree = new LinkedHashMap<>();
        queue = new PriorityQueue<>();
        visitedNodes = new HashSet<>();
    }

    public void generate() {
        generateSpanningTree();
        prepareMaze();
    }

    private void generateSpanningTree() {
        Node start = graph.getStart();
        spanningTree.putIfAbsent(start, new ArrayList<>());
        processEdges(start);
    }

    private void processEdges(Node node) {
        List<Edge> edges = graphNodes.get(node);
        visitedNodes.add(node);
        for (Edge edge : edges) {
            if (!visitedNodes.contains(edge.getTo())) {
                queue.add(edge);
                visitedNodes.add(edge.getTo());
            }
        }
        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            spanningTree.putIfAbsent(edge.getFrom(), new ArrayList<>());
            spanningTree.get(edge.getFrom()).add(edge);

            spanningTree.putIfAbsent(edge.getTo(), new ArrayList<>());
            spanningTree.get(edge.getTo()).add(edge.flip());

            processEdges(edge.getTo());
        }
    }

    private void prepareMaze() {
        Node[][] matrix = graph.getMatrix();
        for (Node[] row : matrix) {
            for (Node currentNode : row) {
                if (spanningTree.containsKey(currentNode)) {
                    currentNode.setPassage();
                    List<Edge> edges = spanningTree.get(currentNode);
                    for (var edge : edges) {
                        edge.getTo().setPassage();
                        Node nodeOnEdgePosition = edgeToNode(edge);
                        if (nodeOnEdgePosition != null) {
                            nodeOnEdgePosition.setPassage();
                        }
                    }
                }
            }
        }
        addEnter(Side.LEFT);
        addEnter(Side.RIGHT);
    }

    private void addEnter(Side side) {
        Node[][] matrix = graph.getMatrix();
        List<Node> nodesToConnect;
        switch (side) {
            case LEFT:
            case RIGHT:
                int x = side == Side.LEFT ? 1 : matrix[0].length - 2;
                nodesToConnect = IntStream.range(1, matrix.length - 1)
                        .mapToObj(y -> matrix[y][x])
                        .filter(node -> !node.isWall())
                        .collect(Collectors.toList());
                break;
            case TOP:
            case BOTTOM:
                int y = side == Side.TOP ? 1 : matrix.length - 2;
                nodesToConnect = Arrays.stream(matrix[y], 1, matrix[0].length - 1)
                        .filter(node -> !node.isWall())
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalStateException("No node found for enter on: " + side);
        }
        Collections.shuffle(nodesToConnect);
        connectEnterTo(side, nodesToConnect.get(0));
    }

    private void connectEnterTo(Side side, Node connection) {
        Node[][] matrix = graph.getMatrix();
        Node enter;
        switch (side) {
            case LEFT:
                enter = matrix[connection.getY()][connection.getX() - 1];
                break;
            case RIGHT:
                enter = matrix[connection.getY()][connection.getX() + 1];
                break;
            case TOP:
                enter = matrix[connection.getY() - 1][connection.getX()];
                break;
            case BOTTOM:
                enter = matrix[connection.getY() + 1][connection.getX()];
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + side);
        }
        enter.setPassage();
        spanningTree.put(enter, List.of(new Edge(enter, connection, 1)));
    }

    private Node edgeToNode(Edge edge) {
        int x = -1;
        int y = -1;
        if (edge.getFrom().getX() == edge.getTo().getX()
                && Math.abs(edge.getFrom().getY() - edge.getTo().getY()) == 2) {
            x = edge.getFrom().getX();
            y = (edge.getFrom().getY() + edge.getTo().getY()) / 2;
        }
        if (edge.getFrom().getY() == edge.getTo().getY()
                && Math.abs(edge.getFrom().getX() - edge.getTo().getX()) == 2) {
            x = (edge.getFrom().getX() + edge.getTo().getX()) / 2;
            y = edge.getFrom().getY();
        }
        return x == -1 || y == -1 ? null : graph.getMatrix()[y][x];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node[] row : graph.getMatrix()) {
            Arrays.stream(row)
                    .map(node -> node.isWall() ? WALL : PASS)
                    .forEach(sb::append);
            sb.append("\n");
        }
        return sb.toString();
    }

    public MazeState getState() {
        return new MazeState(graph, spanningTree);
    }

    public void setState(MazeState state) {
        graph = state.graphState;
        graphNodes = state.graphState.getGraph();
        spanningTree = state.spanningTreeState;
    }

    static class MazeState implements Serializable {
        private static final long serialVersionUID = 7076557791987400397L;
        private final Graph graphState;
        private final LinkedHashMap<Node, List<Edge>> spanningTreeState;

        public MazeState(Graph graph,
                         LinkedHashMap<Node, List<Edge>> spanningTree) {
            this.graphState = graph;
            this.spanningTreeState = spanningTree;
        }
    }
}
