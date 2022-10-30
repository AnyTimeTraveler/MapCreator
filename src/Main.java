import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        final JFrame frame = new JFrame("Mapper");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Init Menu Bar
        JMenuBar menuBar = new JMenuBar();

        // Init File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMap = new JMenuItem("Open Map...");
        JFileChooser mapFileChooser = new JFileChooser(new File("."));
        mapFileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg", "bmp", "gif", "wbmp"));

        openMap.addActionListener(ignored -> mapFileChooser.showOpenDialog(frame));
        fileMenu.add(openMap);
        JMenuItem openLocations = new JMenuItem("Load Locations...");
        fileMenu.add(openLocations);
        JMenuItem saveLocations = new JMenuItem("Save Locations...");
        fileMenu.add(saveLocations);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // Init Panels
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        Map map = new Map("res/no_map_loaded.png");
        JLabel fps = new JLabel("FPS: 0 Frame Time: 0 ms");
        Timer timerFPS = new Timer(200, e -> fps.setText("FPS: " + map.getFPS() + " Frame Time: " + map.getFrameTime() / 1_000_000.0 + "ms"));
        timerFPS.start();
        JTextField label = new JTextField("Kuffel", 25);

        mapFileChooser.addActionListener(ignored -> {
            try {
                map.load(mapFileChooser.getSelectedFile());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showConfirmDialog(frame, e.getMessage(), "Error: Could not open image", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });

        // Init Color Picker
        JColorChooser colorChooser = new JColorChooser();
        AbstractColorChooserPanel[] chooserPanels = colorChooser.getChooserPanels();
        final ArrayList<AbstractColorChooserPanel> activePanels = new ArrayList<>();
        for (AbstractColorChooserPanel panel : chooserPanels) {
            if (panel.getDisplayName().equals("RGB") || panel.getDisplayName().equals("Swatches")) {
                activePanels.add(panel);
            }
        }
        AbstractColorChooserPanel[] activePanelArray = new AbstractColorChooserPanel[2];
        activePanels.toArray(activePanelArray);
        colorChooser.setChooserPanels(activePanelArray);
        colorChooser.setPreviewPanel(new JPanel());
        colorChooser.getSelectionModel().addChangeListener(e -> map.addLocation(label.getText(), colorChooser.getColor()));
        bottomPanel.add(colorChooser, BorderLayout.CENTER);

        // Init Label
        label.addActionListener(e -> map.addLocation(label.getText(), colorChooser.getColor()));

        JPanel bottomRow = new JPanel();
        bottomRow.add(label);
        bottomRow.add(fps);
        bottomPanel.add(bottomRow, BorderLayout.NORTH);

        // Add everything to the main panel
        mainPanel.add(map, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}