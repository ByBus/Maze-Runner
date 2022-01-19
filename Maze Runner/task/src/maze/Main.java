package maze;

import java.io.IOException;
import java.util.*;

public class Main {
    private static boolean isMazeLoaded = false;

    public static void main(String[] args) throws IOException {
        GraphMaker graphMaker = new GraphMaker();
        Maze maze = new Maze(graphMaker);
        Memory memory = new Memory(maze);
        Facade facade = new Facade(graphMaker, maze, memory);
        menu(facade);
    }

    private static void menu(Facade facade) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        List<String> allowedCommand = new ArrayList<>(List.of("1", "2", "0"));
        while (!Objects.equals(input, "0")) {
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze");
            System.out.println("2. Load a maze");
            if (isMazeLoaded) {
                System.out.println("3. Save the maze");
                System.out.println("4. Display the maze");
                System.out.println("5. Find the escape");
            }
            System.out.println("0. Exit");
            input = scanner.nextLine();
            if (!allowedCommand.contains(input)) {
                System.out.println("Incorrect option. Please try again");
                continue;
            }
            switch (input) {
                case "1":
                    facade.generateMaze();
                    enableAdditionalOptions(allowedCommand);
                    break;
                case "2":
                    try {
                        facade.loadMaze();
                        enableAdditionalOptions(allowedCommand);
                    } catch (IOException e) {
                        System.out.println("The file ... does not exist");
                    } catch (ClassNotFoundException e) {
                        System.out.println("Cannot load the maze. It has an invalid format");
                    }
                    break;
                case "3":
                    facade.saveMaze();
                    break;
                case "4":
                    facade.displayMaze();
                    break;
                case "5":
                    facade.findEscape();
                    break;
            }
        }
        System.out.println("Bye!");
        scanner.close();
    }

    private static void enableAdditionalOptions(List<String> allowedCommand) {
        if (!isMazeLoaded) {
            isMazeLoaded = true;
            allowedCommand.add("3");
            allowedCommand.add("4");
            allowedCommand.add("5");
        }
    }
}
