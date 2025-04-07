import javax.swing.*;
import java.awt.*;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class DigitalPetEvolution extends JFrame {
    // Pet attributes
    private String name;
    private int hunger = 50;
    private int happiness = 50;
    private int energy = 50;
    private int intelligence = 1;
    private int evolutionStage = 1;
    private int age = 0;
    private String petType = "Egg";
    private String personality = "Curious";

    // Evolution paths and personalities
    private final String[] EVOLUTION_PATHS = {"Warrior", "Scholar", "Mystic", "Jester"};
    private final String[] PERSONALITIES = {"Shy", "Energetic", "Curious", "Grumpy", "Friendly", "Mischievous"};

    // UI Components
    private JPanel mainPanel;
    private JPanel statsPanel;
    private JPanel buttonPanel;
    private JTextArea petDisplay;
    private JLabel statusLabel;
    private JProgressBar hungerBar, happinessBar, energyBar, intelligenceBar;
    private JButton feedButton, playButton, sleepButton, teachButton, evolveButton;
    private JLabel ageLabel, typeLabel, personalityLabel, stageLabel;

    // Pet appearance (uses ASCII art for simplicity)
    private final String[] PET_APPEARANCES = {
                                "                              " + "  ▄▄▄  \n" + "                              " + " /(⚆_⚆)\\\n" + "                              " + "  ▀▀▀  ", // Egg
                                "                              " + "  ●-●  \n" + "                              " + " /(•v•)\\\n" + "                              " + "  ▄▄▄  ", // Baby
                                "                              " + "  ^-^  \n" + "                              " + " /(•w•)\\\n" + "                              " + " /|   |\\\n" + "                              " + "  ▀▀▀  ", // Child
                                "                              " + "  ^-^  \n" + "                              " + " /(•w•)\\\n" + "                              " + " /|   |\\\n" + "                              " + " / |   | \\\n" + "                              " + "   ▀▀▀   " // Adult
    };

    private final Color[] BG_COLORS = {
                                new Color(255, 250, 240), // Egg - off white
                                new Color(173, 216, 230), // Baby - light blue
                                new Color(144, 238, 144), // Child - light green
                                new Color(255, 182, 193)  // Adult - light pink
    };

    // Timer for automatic stat decreases
    private Timer timer;
    private Random random = new Random();

    public DigitalPetEvolution() {
        // Initialize the window
        super("Digital Pet Evolution");


        // Make application fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the window
        setUndecorated(false); // Removes the window border and title bar (optional)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ask for pet name
        name = JOptionPane.showInputDialog(this, "Name your digital pet:", "My Pet");
        if (name == null || name.trim().isEmpty()) {
                                name = "Pixel";
        }

        // Create the UI
        createUI();

        // Start the timer for automatic stat decreases
        timer = new Timer(5000, e -> decreaseStats());
        timer.start();

        // Display the window
        setLocationRelativeTo(null);
        setVisible(true);

        // Initial status update
        updateStatus();
    }

    private void createUI() {
        // Main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BG_COLORS[0]);

        // Pet display area
        JPanel petDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        petDisplay = new JTextArea(PET_APPEARANCES[0]);
        petDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
        petDisplay.setOpaque(false);
        petDisplay.setEditable(false);
        petDisplay.setFocusable(false);
        petDisplay.setHighlighter(null);
        petDisplay.setLineWrap(false);
        petDisplay.setWrapStyleWord(false);
        petDisplay.setBackground(BG_COLORS[0]);
        petDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        petDisplay.setAlignmentY(Component.CENTER_ALIGNMENT);
        petDisplayPanel.setPreferredSize(new Dimension(500, 500)); // Adjust the size if necessary

        // Status message
        statusLabel = new JLabel("Your pet " + name + " is hatching...", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Stats panel
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));  // Make panel stretch vertically
        statsPanel.setBackground(Color.BLACK);  // Set background color to black

        // Create basic stat bars
        hungerBar = createProgressBar("Hunger", Color.ORANGE);
        happinessBar = createProgressBar("Happiness", Color.PINK);
        energyBar = createProgressBar("Energy", Color.BLUE);
        intelligenceBar = createProgressBar("Intelligence", Color.GREEN);

        // Info labels
        stageLabel = new JLabel("Stage: " + evolutionStage);
        ageLabel = new JLabel("Age: " + age);
        typeLabel = new JLabel("Type: " + petType);
        personalityLabel = new JLabel("Personality: " + personality);

        // Set the font color of the labels showing stats (like "Hunger:", "Happiness:", etc.) to white
        JLabel hungerLabel = new JLabel("Hunger:");
        hungerLabel.setForeground(Color.WHITE);

        JLabel happinessLabel = new JLabel("Happiness:");
        happinessLabel.setForeground(Color.WHITE);

        JLabel energyLabel = new JLabel("Energy:");
        energyLabel.setForeground(Color.WHITE);

        JLabel intelligenceLabel = new JLabel("Intelligence:");
        intelligenceLabel.setForeground(Color.WHITE);

        stageLabel.setForeground(Color.WHITE);
        ageLabel.setForeground(Color.WHITE);
        typeLabel.setForeground(Color.WHITE);
        personalityLabel.setForeground(Color.WHITE);

        // Add stats to panel with updated label colors
        statsPanel.add(hungerLabel);
        statsPanel.add(hungerBar);
        statsPanel.add(happinessLabel);
        statsPanel.add(happinessBar);
        statsPanel.add(energyLabel);
        statsPanel.add(energyBar);
        statsPanel.add(intelligenceLabel);
        statsPanel.add(intelligenceBar);
        statsPanel.add(stageLabel);
        statsPanel.add(ageLabel);
        statsPanel.add(typeLabel);
        statsPanel.add(personalityLabel);

        // Buttons
        buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        buttonPanel.setOpaque(false);

        feedButton = createButton("Feed");
        playButton = createButton("Play");
        sleepButton = createButton("Sleep");
        teachButton = createButton("Teach");
        evolveButton = createButton("Evolve");
        evolveButton.setEnabled(false);

        buttonPanel.add(feedButton);
        buttonPanel.add(playButton);
        buttonPanel.add(sleepButton);
        buttonPanel.add(teachButton);
        buttonPanel.add(evolveButton);

        // Add components to main panel
        mainPanel.add(petDisplay, BorderLayout.NORTH);
        mainPanel.add(statusLabel, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.EAST);  // The statsPanel will now stretch vertically
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to frame
        add(mainPanel);
    }

    private JProgressBar createProgressBar(String name, Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(50);
        bar.setStringPainted(true);
        bar.setForeground(color);
        return bar;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.addActionListener(e -> handleAction(text));
        return button;
    }

    private void handleAction(String action) {
        switch (action) {
                                case "Feed":
                                    feed();
                                    break;
                                case "Play":
                                    play();
                                    break;
                                case "Sleep":
                                    sleep();
                                    break;
                                case "Teach":
                                    teach();
                                    break;
                                case "Evolve":
                                    evolve();
                                    break;
        }
        updateStatus();
        checkEvolution();
    }

    private void feed() {
        hunger += 20;
        energy += 5;
        happiness += 5;

        if (hunger > 100) {
                                hunger = 100;
                                statusLabel.setText(name + " is full!");
        } else {
                                statusLabel.setText("You fed " + name + "!");
        }

        // Chance for personality change if overfed
        if (hunger > 90 && random.nextInt(10) == 0) {
                                personality = "Grumpy";
                                personalityLabel.setText("Personality: " + personality);
        }
    }

    private void play() {
        happiness += 20;
        energy -= 15;
        hunger -= 10;

        if (happiness > 100) {
                                happiness = 100;
        }

        if (energy < 0) {
                                energy = 0;
                                statusLabel.setText(name + " is too tired to play anymore!");
        } else {
                                statusLabel.setText("You played with " + name + "!");
        }

        // Play can change personality
        if (happiness > 80 && random.nextInt(5) == 0) {
                                personality = "Energetic";
                                personalityLabel.setText("Personality: " + personality);
        }
    }

    private void sleep() {
        energy += 30;
        hunger -= 5;

        if (energy > 100) {
                                energy = 100;
        }

        statusLabel.setText(name + " had a good nap!");

        // Sleep can affect personality
        if (energy > 90 && random.nextInt(5) == 0) {
                                personality = "Friendly";
                                personalityLabel.setText("Personality: " + personality);
        }
    }

    private void teach() {
        intelligence += 10;
        energy -= 10;
        hunger -= 5;

        if (intelligence > 100) {
                                intelligence = 100;
        }

        if (energy < 0) {
                                energy = 0;
        }

        statusLabel.setText("You taught " + name + " something new!");

        // Teaching affects personality
        if (intelligence > 70 && random.nextInt(3) == 0) {
                                personality = "Curious";
                                personalityLabel.setText("Personality: " + personality);
        }
    }

    private void evolve() {
        evolutionStage++;

        // Determine evolution path based on highest stat
        String newType;
        if (intelligence > happiness && intelligence > energy && intelligence > hunger) {
                                newType = EVOLUTION_PATHS[1]; // Scholar
        } else if (happiness > intelligence && happiness > energy && happiness > hunger) {
                                newType = EVOLUTION_PATHS[3]; // Jester
        } else if (energy > intelligence && energy > happiness && energy > hunger) {
                                newType = EVOLUTION_PATHS[0]; // Warrior
        } else {
                                newType = EVOLUTION_PATHS[2]; // Mystic
        }

        petType = newType;
        typeLabel.setText("Type: " + petType);
        stageLabel.setText("Stage: " + evolutionStage);

        // Update appearance
        if (evolutionStage <= PET_APPEARANCES.length) {
                                petDisplay.setText(PET_APPEARANCES[evolutionStage - 1]);
                                mainPanel.setBackground(BG_COLORS[evolutionStage - 1]);
                                petDisplay.setBackground(BG_COLORS[evolutionStage - 1]);
        }

        // Boost stats slightly
        hunger += 20;
        happiness += 20;
        energy += 20;

        // Cap stats at 100
        hunger = Math.min(hunger, 100);
        happiness = Math.min(happiness, 100);
        energy = Math.min(energy, 100);

        statusLabel.setText(name + " evolved into a " + petType + "!");
        evolveButton.setEnabled(false);
    }

    private void decreaseStats() {
        // Decrease stats over time
        hunger -= 5;
        happiness -= 3;
        energy -= 2;

        // Ensure stats don't go below 0
        hunger = Math.max(hunger, 0);
        happiness = Math.max(happiness, 0);
        energy = Math.max(energy, 0);

        // Increment age
        age++;
        ageLabel.setText("Age: " + age);

        // Random personality changes
        if (random.nextInt(20) == 0) {
                                personality = PERSONALITIES[random.nextInt(PERSONALITIES.length)];
                                personalityLabel.setText("Personality: " + personality);
        }

        updateStatus();
        checkEvolution();
    }

    private void updateStatus() {
        // Update progress bars
        hungerBar.setValue(hunger);
        happinessBar.setValue(happiness);
        energyBar.setValue(energy);
        intelligenceBar.setValue(intelligence);

        // Update status message based on lowest stat
        if (hunger < 20) {
                                statusLabel.setText(name + " is hungry!");
        } else if (happiness < 20) {
                                statusLabel.setText(name + " is sad!");
        } else if (energy < 20) {
                                statusLabel.setText(name + " is tired!");
        }

        // Visually update bars
        if (hunger < 30) hungerBar.setForeground(Color.RED);
        else hungerBar.setForeground(Color.ORANGE);

        if (happiness < 30) happinessBar.setForeground(Color.RED);
        else happinessBar.setForeground(Color.PINK);

        if (energy < 30) energyBar.setForeground(Color.RED);
        else energyBar.setForeground(Color.BLUE);
    }

    private void checkEvolution() {
        // Check if pet can evolve
        boolean canEvolve = false;

        if (evolutionStage == 1 && age >= 10) {
                                canEvolve = true;
        } else if (evolutionStage == 2 && age >= 25) {
                                canEvolve = true;
        } else if (evolutionStage == 3 && age >= 50) {
                                canEvolve = true;
        }

        evolveButton.setEnabled(canEvolve && evolutionStage < 4);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
                                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
                                e.printStackTrace();
        }

        // Create the pet
        SwingUtilities.invokeLater(() -> new DigitalPetEvolution());
    }
}