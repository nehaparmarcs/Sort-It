import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * A label with a transparent background for writing text.
 *
 * @author Ed Parrish
 * @version 1.1  12/25/2010
 */
public class Label extends GUIComponent {
    // FontMetrics makes line height too high so reduce it
    private static final double ASCENT_MULT = .90;
    // Label backgrounds are transparent by default
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    // Flag to test if background image was set by user
    private boolean hasBGImage;

    /**
     * Constructs a label with the specified background image. The image may
     * be written upon by calling <code>setText()</code>.
     *
     * @param img The background image to display.
     */
    public Label(GreenfootImage img) {
        hasBGImage = true;
        setImage(img);
        setFocusable(false);
    }

    /**
     * Constructs a label with the specified text displayed in the default text
     * color and font on a transparent background without a border.
     *
     * @param text The text to display on the label
     */
    public Label(String text) {
        this (text, DEFAULT_FONT, DEFAULT_FG);
    }

    /**
     * Constructs a label with the specified text and font using the default
     * text color on a transparent background without a border.
     *
     * @param text The text to display on the label
     * @param font The font with which to write the text.
     */
    public Label(String text, Font font) {
        this (text, font, DEFAULT_FG);
    }

    /**
     * Constructs a label with text of the specified color using the default
     * font on a transparent background without a border.
     *
     * @param text The text to display on the label
     * @param textColor The color to use for displaying text.
     */
    public Label(String text, Color textColor) {
        this (text, DEFAULT_FONT, textColor);
    }

    /**
     * Constructs a label with text of the specified color and font on a
     * transparent background without a border.
     *
     * @param text The text to display on the label
     * @param font The font with which to display the text.
     * @param textColor The color to use for displaying text.
     */
    public Label(String text, Font font, Color textColor) {
        super(text, font, textColor, TRANSPARENT);
        setFocusable(false);
        setBorder(null); // calls repaint
    }

    /**
     * Constructs a label with text of the specified color on a background
     * of the specified color with a border.
     *
     * @param text The text to display on the label
     * @param font The font with which to display the text.
     * @param textColor The color to use for displaying text.
     * @param bgColor The desired background color.
     */
    public Label(String text, Font font, Color textColor, Color bgColor) {
        super(text, font, textColor, bgColor);
        setFocusable(false);
        repaint();
    }

    /**
     * Prepares the labels background image.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    public void paintComponent(Graphics g) {
        if (hasBGImage) return;
        GreenfootImage img = null;
        if (isFixedSize()) {
            img = new GreenfootImage(getWidth(), getHeight());
        } else {
            // Create image sized for text width and height
            Dimension d = getTextDimension(getText(), g);
            img = new GreenfootImage(d.width, d.height);
        }
        if (getBackground() != TRANSPARENT) {
            img.setColor(getBackground());
            img.fill();
        }
        setImage(img);
    }

    /**
     * Paints the text onto the labels background image.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    public void paintText(Graphics g) {
        String labelText = getText();
        if (labelText == null || labelText.length() == 0) return;
        String[] lines = splitLines(labelText, g);
        GreenfootImage img = getImage();
        if (getFont() != null) img.setFont(getFont());
        if (getForeground() != null) img.setColor(getForeground());

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = (int) (fm.getHeight() * ASCENT_MULT);
        int y = lineHeight + (getHeight() - (lineHeight * lines.length)
            - fm.getDescent()) / 2;
        for (int i = 0; i < lines.length; i++) {
            int x = getWidth() / 2 - fm.stringWidth(lines[i]) / 2;
            img.drawString(lines[i], x, y);
            y += lineHeight;
        }
    }
}
