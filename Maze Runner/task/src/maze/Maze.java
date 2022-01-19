package maze;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Maze {
    private static final String WALL ="\u2588\u2588";
    private static final String PASS ="  ";
    private static final String PATH ="//";

    private GraphMaker graphMaker;
    private Map<Node, List<Edge>> graph;
    private LinkedHashMap<Node, List<Edge>> spanningTree;
    private List<Node> pathToExit;

    private enum Side {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public Maze(GraphMaker graphMaker) {
        init(graphMaker);
    }

    public void init(GraphMaker graphMaker) {
        this.graphMaker = graphMaker;
        this.graph = graphMaker.getGraph();
        spanningTree = new LinkedHashMap<>();
        pathToExit = new ArrayList<>();
    }

    public void generate() {
        generateSpanningTree();
        prepareMaze();
    }

    private void generateSpanningTree() {
        PriorityQueue<Edge> queue = new PriorityQueue<>();
        Set<Node> visitedNodes = new HashSet<>();

        Node start = graphMaker.getStart();
        spanningTree.putIfAbsent(start, new ArrayList<>());
        addEdgesToTree(start, queue, visitedNodes);
    }

    private void addEdgesToTree(Node node,
                                PriorityQueue<Edge> queue,
                                Set<Node> visited) {
        List<Edge> edges = graph.get(node);
        visited.add(node);
        for (Edge edge : edges) {
            if (!visited.contains(edge.getTo())) {
                queue.add(edge);
                visited.add(edge.getTo());
            }
        }
        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            spanningTree.putIfAbsent(edge.getFrom(), new ArrayList<>());
            spanningTree.get(edge.getFrom()).add(edge);

            spanningTree.putIfAbsent(edge.getTo(), new ArrayList<>());
            spanningTree.get(edge.getTo()).add(edge.flip());

            addEdgesToTree(edge.getTo(), queue, visited);
        }
    }

    private void prepareMaze() {
        Node[][] matrix = graphMaker.getMatrix();
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
        Node[][] matrix = graphMaker.getMatrix();
        List<Node> nodesToConnect;
        switch (side) {
            case LEFT:
            case RIGHT:
                int x = side == Side.LEFT ? 1 : matrix[0].length - 2;
                nodesToConnect = IntStream.range(1, matrix.length - 1)
                        .mapToObj(y -> matrix[y][x])
                        .filter(node -> !node.isWall())
                        .filter(node -> spanningTree.containsKey(node))
                        .collect(Collectors.toList());
                break;
            case TOP:
            case BOTTOM:
                int y = side == Side.TOP ? 1 : matrix.length - 2;
                nodesToConnect = Arrays.stream(matrix[y], 1, matrix[0].length - 1)
                        .filter(node -> !node.isWall())
                        .filter(node -> spanningTree.containsKey(node))
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalStateException("No node found for enter on: " + side);
        }
        Collections.shuffle(nodesToConnect);
        connectEnterTo(side, nodesToConnect.get(0));
    }

    private void connectEnterTo(Side side, Node connection) {
        Node[][] matrix = graphMaker.getMatrix();
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
        Edge edge = new Edge(enter, connection, 1);
        spanningTree.put(enter, List.of(edge));
        spanningTree.get(connection).add(edge.flip());
    }

    private Node edgeToNode(Edge edge) {
        int[] coord = edge.getCoordinates();
        if (Arrays.stream(coord).anyMatch(x -> x == -1)) {
            return null;
        }
        return graphMaker.getMatrix()[coord[0]][coord[1]];
    }

    public void findShortestPath() {
        List<Node> visited = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        Node enter = getEntrance(1);
        Node exit = getEntrance(2);
        stack.push(enter);
        traverseWithDepthFirst(enter, stack, visited, exit);
        while (!stack.isEmpty()) {
            pathToExit.add(stack.pop());
        }
        addEdgesToPath();
    }

    private Node getEntrance(int indexFromEnd) {
        List<Node> nodes = new ArrayList<>(spanningTree.keySet());
        return nodes.get(nodes.size() - indexFromEnd);
    }

    public void addEdgesToPath() {
        List<Node> nodesOnEdgesPositions = IntStream.range(0, pathToExit.size() - 2)
                .mapToObj(i -> new Edge(pathToExit.get(i), pathToExit.get(i + 1), 1))
                .map(this::edgeToNode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        pathToExit.addAll(nodesOnEdgesPositions);
    }

    private boolean traverseWithDepthFirst(Node node,
                                           Deque<Node> stack,
                                           List<Node> visited,
                                           Node exit) {
        if (node.equals(exit)) {
            return true;
        }
        visited.add(node);
        for (Edge edge : spanningTree.get(node)) {
            Node to = edge.getTo();
            if (visited.contains(to)) {
                continue;
            }
            stack.push(to);
            if (traverseWithDepthFirst(to, stack, visited, exit)) {
               return true;
            }
        }
        stack.pop();
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node[] row : graphMaker.getMatrix()) {
            Arrays.stream(row)
                    .map(node -> pathToExit.contains(node) ? PATH : node.isWall() ? WALL : PASS)
                    .forEach(sb::append);
            sb.append("\n");
        }
        return sb.toString();
    }

    public MazeState getState() {
        return new MazeState(graphMaker, spanningTree);
    }

    public void setState(MazeState state) {
        graphMaker = state.graphMaker;
        graph = state.graphMaker.getGraph();
        spanningTree = state.spanningTree;
        pathToExit = new ArrayList<>();
    }

    static class MazeState implements Serializable {
        private static final long serialVersionUID = 7076557791987400397L;

        private final GraphMaker graphMaker;
        private final LinkedHashMap<Node, List<Edge>> spanningTree;

        public MazeState(GraphMaker graphMaker,
                         LinkedHashMap<Node, List<Edge>> spanningTree) {
            this.graphMaker = graphMaker;
            this.spanningTree = spanningTree;
        }
    }
}
