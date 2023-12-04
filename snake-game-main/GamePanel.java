import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 20;
    static final int NUMBER_OF_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    final int x[] = new int[NUMBER_OF_UNITS];
    final int y[] = new int[NUMBER_OF_UNITS];
    int length = 5;
    int foodEaten;
    int foodX;
    int foodY;
    char direction = 'D';
    boolean running = false;
    Random random;
    Timer timer;
    JButton tryAgainButton;
    JButton exitButton;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        play();

        // Initialize buttons with styling
        tryAgainButton = createStyledButton("Try Again");
        exitButton = createStyledButton("Exit");

        this.setLayout(null); // Use null layout for absolute positioning
        this.add(tryAgainButton);
        this.add(exitButton);

        // Hide buttons initially
        tryAgainButton.setVisible(false);
        exitButton.setVisible(false);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Ink Free", Font.BOLD, 18)); // Increased font size
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 150, 0)); // Green background
        button.setFocusable(false);
        button.addActionListener(this);
        button.setBorder(BorderFactory.createRaisedBevelBorder()); // Raised bevel border for a 3D effect
        return button;
    }

    public void play() {
        addFood();
        running = true;
        timer = new Timer(75, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void move() {
        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            length++;
            foodEaten++;
            addFood();
        }
    }

    public void draw(Graphics graphics) {
        if (running) {
            graphics.setColor(Color.RED);
            graphics.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    graphics.setColor(Color.GREEN);
                    graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    graphics.setColor(new Color(45, 180, 0));
                    graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(graphics.getFont());
            graphics.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, graphics.getFont().getSize());
        } else {
            gameOver(graphics);
        }
    }

    public void addFood() {
        foodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void checkHit() {
        for (int i = length; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics graphics) {
        // Game Over text
        graphics.setColor(Color.red);
        graphics.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (WIDTH - metrics1.stringWidth("Game Over")) / 2, HEIGHT / 2 - 50);

        // Score
        graphics.setColor(Color.red);
        graphics.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " + foodEaten, (WIDTH - metrics2.stringWidth("Score: " + foodEaten)) / 2, HEIGHT / 2);

        // Position and show buttons
        positionAndShowButtons();
    }

    private void positionAndShowButtons() {
        int buttonWidth = 150; // Increased width
        int buttonHeight = 50; // Increased height
        int spacing = 60; // Adjusted spacing

        tryAgainButton.setBounds((WIDTH - buttonWidth) / 2, HEIGHT / 2 + spacing, buttonWidth, buttonHeight);
        exitButton.setBounds((WIDTH - buttonWidth) / 2, HEIGHT / 2 + spacing + buttonHeight + 20, buttonWidth, buttonHeight);

        tryAgainButton.setVisible(true);
        exitButton.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkHit();
        } else {
            if (e.getSource() == tryAgainButton) {
                // Reset the game
                resetGame();
            } else if (e.getSource() == exitButton) {
                // Exit the game
                System.exit(0);
            }
        }
        repaint();
    }

    private void resetGame() {
        length = 5;
        foodEaten = 0;
        direction = 'D';

        // Reset the position of the snake
        for (int i = 0; i < length; i++) {
            x[i] = 0 - i * UNIT_SIZE;
            y[i] = 0;
        }

        addFood();
        running = true;

        // Restart the timer
        timer.start();

        // Hide buttons
        tryAgainButton.setVisible(false);
        exitButton.setVisible(false);
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
