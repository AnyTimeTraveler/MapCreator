import java.awt.*;

public class Location {
    String label = "<no label>";
    int x = 0;
    int y = 0;
    Color color = Color.BLUE;

    public Location() {

    }

    public Location(String label, int x, int y, Color color) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public String label() {
        return label;
    }

    public void label(String label) {
        this.label = label;
    }

    public int x() {
        return x;
    }

    public void x(int x) {
        this.x = x;
    }

    public int y() {
        return y;
    }

    public void y(int y) {
        this.y = y;
    }

    public Color color() {
        return color;
    }

    public void color(Color color) {
        this.color = color;
    }

    public Location copy() {
        return new Location(label, x, y, color);
    }
}
