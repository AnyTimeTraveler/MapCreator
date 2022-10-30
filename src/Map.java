import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.RenderingHints.*;

public class Map extends JPanel implements MouseMotionListener, MouseWheelListener, MouseListener {

    public static final int CHARACTER_WIDTH = 7;
    private BufferedImage image;
    public Mode mode = Mode.SELECT;
    private boolean mouseInside = false;
    private final Location newLocation = new Location();
    private final List<Location> locations = new ArrayList<>();
    private int translationX = 0;
    private int translationY = 0;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private double zoom = 0.5;
    private int frames = 0;
    private int currentFrames = 0;
    private long renderTime = 0;
    private boolean firstTranslate = true;

    public Map(String path) throws IOException {
        image = ImageIO.read(new File(path));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        Timer timerFPS = new Timer(1000, e -> {
            currentFrames = frames;
            frames = 0;
        });
        timerFPS.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        if (firstTranslate) {
            firstTranslate = false;
            translationX = getWidth() / 2;
            translationY = getHeight() / 2;
        }
        long start = System.nanoTime();
        Graphics2D g = (Graphics2D) graphics;

        RenderingHints hints = new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        hints.add(new RenderingHints(KEY_RENDERING, VALUE_RENDER_QUALITY));
        hints.add(new RenderingHints(KEY_STROKE_CONTROL, VALUE_STROKE_PURE));
//        hints.add(new RenderingHints(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR));
        g.addRenderingHints(hints);

        g.setColor(new Color(27, 120, 255));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.translate(translationX, translationY);
        int width = (int) (image.getWidth() * zoom);
        int height = (int) (image.getHeight() * zoom);
        g.drawImage(image, -width / 2, -height / 2, width, height, this);

        g.setStroke(new BasicStroke(3));
        for (Location location : locations) {
            drawLocation(g, location);
        }

        if (mouseInside && mode == Mode.CREATE) {
            drawLocation(g, newLocation);
        }
        frames++;
        renderTime = System.nanoTime() - start;
    }

    private void drawLocation(Graphics2D g, Location location) {
        int x = (int) (location.x() * zoom);
        int y = (int) (location.y() * zoom);
        int length = location.label().length();
        g.setColor(location.color);
        g.drawOval(x - (length / 2 * CHARACTER_WIDTH) - 10, y - 12, length * CHARACTER_WIDTH + 10, 18);
        g.drawChars(location.label().toCharArray(), 0, length, x - (length / 2 * CHARACTER_WIDTH), y);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        translationX = e.getX() - dragStartX;
        translationY = e.getY() - dragStartY;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        newLocation.x((int) ((e.getX() - translationX) / zoom));
        newLocation.y((int) ((e.getY() - translationY) / zoom));
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (mode == Mode.CREATE) {
            mode = Mode.SELECT;
            locations.add(newLocation.copy());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseInside = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseInside = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragStartX = e.getX() - translationX;
        dragStartY = e.getY() - translationY;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // ignored
    }

    public void addLocation(String text, Color color) {
        newLocation.label(text);
        newLocation.color(color);
        mode = Mode.CREATE;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            zoom += 0.05;
        } else {
            zoom -= 0.05;
            if (zoom <= 0) {
                zoom = 0.05;
            }
        }
        repaint();
    }

    public int getFPS() {
        return currentFrames;
    }

    public long getFrameTime() {
        return renderTime;
    }

    public void load(File mapFile) throws IOException {
        image = ImageIO.read(mapFile);
        if (image == null) {
            image = ImageIO.read(new File("res/empty_map.png"));
            throw new IOException("Error reading image!");
        } else {
            repaint();
        }
    }
}
