package maze;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the size of a maze");
        int[] sizeOfMaze = Arrays.stream(scanner.nextLine().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
        Graph graph = new Graph(sizeOfMaze[0], sizeOfMaze[1]);
        Maze maze = new Maze(graph);
        maze.generate();
        System.out.println(maze);
    }
}
