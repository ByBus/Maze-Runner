package maze;

import java.io.IOException;
import java.util.Scanner;

public class Facade {
    private final Graph graph;
    private final Maze maze;
    private final Memory memory;

    public Facade(Graph graph, Maze maze, Memory memory) {
        this.graph = graph;
        this.maze = maze;
        this.memory = memory;
    }

    public void generateMaze() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the size of a maze");
        int size = scanner.nextInt();
        graph.initGraph(size, size);
        maze.init(graph);
        maze.generate();
        displayMaze();
        //scanner.close();
    }

    public void displayMaze() {
        System.out.println(maze);
    }

    public void loadMaze() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        memory.loadFromFile(fileName);
        //scanner.close();
    }

    public void saveMaze() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        memory.saveToFile(fileName);
        //scanner.close();
    }
}
