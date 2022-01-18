package maze;

import java.io.Serializable;
import java.util.Objects;

public class Node implements Serializable {
    private static final long serialVersionUID = -3940951508454128896L;
    private final int number;
    public boolean isWall = true;

    private int y;
    private int x;

    public Node(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return number == node.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "" + number;
    }

    public void setPassage() {
        isWall = false;
    }

    public void setWall() {
        isWall = true;
    }

    public boolean isWall() {
        return isWall;
    }

    public void setCoordinates(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
