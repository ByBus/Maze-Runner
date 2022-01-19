package maze;

import java.io.IOException;
import java.util.Scanner;

public class Facade {
    private final GraphMaker graphMaker;
    private final Maze maze;
    private final Memory memory;

    public Facade(GraphMaker graphMaker, Maze maze, Memory memory) {
        this.graphMaker = graphMaker;
        this.maze = maze;
        this.memory = memory;
    }

    public void generateMaze() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the size of a maze");
        int size = scanner.nextInt();
        graphMaker.make(size, size);
        maze.init(graphMaker);
        maze.generate();
        displayMaze();
    }

    public void displayMaze() {
        System.out.println(maze);
    }

    public void loadMaze() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        memory.loadFromFile(fileName);
    }

    public void saveMaze() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        memory.saveToFile(fileName);
    }

    public void findEscape() {
        maze.findShortestPath();
        displayMaze();
    }
}
