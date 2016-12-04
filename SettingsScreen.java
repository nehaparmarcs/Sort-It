import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.*;

/**
 * Displays the game settings and allows the user to adjust them.
 *
 * @author Ed Parrish
 * @version 1.0  1/12/2011
 */
public class SettingsScreen extends Actor implements ActionListener {
    private static final Color BACKGROUND = new Color(0xC6, 0xD6, 0xE0, 236);
    private static final Color FOREGROUND = new Color(0x30, 0x58, 0x7E);
    private static final int WIDTH = 340;
    private static final int HEIGHT = 200;
    private static final String DEFAULT_FILE = "basics.txt";

    private GameManager world;
    private static GreenfootImage img;
    private Button timerButton;
    private Button choicesButton;
    private Button continueButton;
    private TextField fileTF;
    private String file = DEFAULT_FILE;

    /**
     * SettingsScreen constructor.
     */
    public SettingsScreen() { }

    /**
     * Sets the default filename that can be changed by the user.
     *
     * @param filename The default file name.
     */
    public void setFilename(String filename) {
        file = filename;
        if (fileTF != null) fileTF.setText(filename);
    }

    /**
     * Returns the name of the file entered by the user.
     *
     * @return The file name entered by the user.
     */
    public String getFilename() {
        return fileTF.getText();
    }

    /**
     * Clears the display area and removes this object from the world.
     */
    public void clear() {
        world.removeObject(timerButton);
        world.removeObject(choicesButton);
        world.removeObject(continueButton);
        //world.removeObject(userNameTF);
        //world.removeObject(gameIDTF);
        world.removeObject(this);
    }

    /**
     * Respond to a actions like a button being pressed.
     *
     * @param c The component object that caused the action.
     */
    public void actionPerformed(GUIComponent c) {
        if (c == timerButton) {
            if (world.getProvideTimer()) {
                timerButton.setText("Off");
                world.setProvideTimer(false);
            } else {
                timerButton.setText("On");
                world.setProvideTimer(true);
            }
        } else if (c == choicesButton) {
            if (world.getProvideChoices()) {
                choicesButton.setText("Off");
                world.setProvideChoices(false);
            } else {
                choicesButton.setText("On");
                world.setProvideChoices(true);
            }
        } else if (c == continueButton || c == fileTF) {
            world.startRound();
        }
    }

    /**
     * Save the world and draw it.
     *
     * @param w the world.
     */
    public void addedToWorld(World w) {
        world = (GameManager) w;
        draw();
    }

    /**
     * Draw the screen.
     */
    private void draw() {
        final int BORDER = 30;
        final int INSET = 3;
        final int THICKNESS = 3;
        final int BUTTON_WIDTH = 42;
        final int BUTTON_HEIGHT = 30;
        final int X_OFFSET = 119;

        if (img == null) {
            img = new GreenfootImage(WIDTH, HEIGHT);
        }
        img.setColor(BACKGROUND);
        img.fill();
        img.setColor(FOREGROUND);
        // Draw border
        for (int i = 0; i < THICKNESS; i++)  {
            img.drawRect(i + INSET, i + INSET,
                img.getWidth() - i - i - 1 - INSET - INSET,
                img.getHeight() - i - i - 1 - INSET - INSET);
        }
        String text = "Game Settings";
        int y = printCentered(text, GameManager.MED_FONT, 0, -5);
        text = "Use Timer";
        y = printCentered(text, GameManager.SMALL_FONT, -39, y);
        text = "Display multiple choices";
        y = printCentered(text, GameManager.SMALL_FONT, 20, y + 8);
        img.setFont(GameManager.SMALL_FONT);
        img.drawString("Question file:", BORDER, y + 35);

        setImage(img);

        timerButton = new Button("On", GameManager.SMALL_FONT, FOREGROUND);
        timerButton.setSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        timerButton.addActionListener(this);
        world.addObject(timerButton, getX() - X_OFFSET, 217);

        choicesButton = new Button("On", GameManager.SMALL_FONT, FOREGROUND);
        choicesButton.setSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        choicesButton.addActionListener(this);
        world.addObject(choicesButton, getX() - X_OFFSET, 258);


        fileTF = new TextField(file, 10, GameManager.SMALL_FONT);
        fileTF.requestFocus();
        fileTF.addActionListener(this);
        world.addObject(fileTF, getX() + 68, getY() + 29);

        continueButton = new Button("Continue", GameManager.SMALL_FONT,
            FOREGROUND);
        continueButton.addActionListener(this);
        world.addObject(continueButton, getX(), getY() + 70);
    }

    /**
     * Method printCentered displays a single centered line.
     *
     * @param line The line of text to display.
     * @param font The font to use.
     * @param x The x-coordinate offset.
     * @param y The y-coordinate offset.
     * @return The y location for use in the next print operation.
     */
    private int printCentered(String line, Font font, int x, int y) {
        final float LINE_HEIGHT_MULT = 1.2f;
        img.setFont(font);
        Graphics g = img.getAwtImage().getGraphics();
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        x += (img.getWidth() - fm.stringWidth(line)) / 2;
        y += Math.round(fm.getHeight() * LINE_HEIGHT_MULT);
        img.drawString(line, x, y);
        return y;
    }
}
