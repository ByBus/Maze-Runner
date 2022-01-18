package maze;

import java.io.*;

public class Memory {
    private final Maze maze;

    public Memory(Maze maze) {
        this.maze = maze;
    }

    public void saveToFile(String filename) throws IOException {
        Maze.MazeState state = maze.getState();
        serialize(state, filename);
    }

    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        Object state = deserialize(filename);
        maze.setState((Maze.MazeState) state);
    }

    private void serialize(Object obj, String fileName) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fileOut)) {
            oos.writeObject(obj);
        }
    }

    private static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fileIn)) {
            return ois.readObject();
        }
    }
}
