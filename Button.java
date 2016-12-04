import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * An implementation of a generic button for a game or application. The button
 *  is displayed using either supplied images or images generated automatically
 * from text.
 * <p>
 * <code>Button</code> objects have two ways to respond to button clicks:
 * <ol>
 * <li>Poll the state of a button using <code>isPressed()</code>.</li>
 * <li>Using the <code>ActionListener</code> interface.</li>
 * </ol>
 *
 * The <code>ActionListener</code> interface is like the
 * <code>ActionListener</code> interface in Java Swing. Scenarios using the
 * <code>ActionListener</code> callback must include the
 * <code>ActionListener</code> interface, and the class listening for button
 * clicks must implement that interface. In addition, the implementing class
 * must add itself to the callback list by calling the
 * <code>addActionListener()</code> method. Then when a button of this class
 * is clicked, the <code>actionPerformed()</code> method in the listening class
 * will be called.
 * <p>
 * Note that you can make buttons with transparent backgrounds by using
 * transparent background images or by setting the background to a transparent
 * color. For example:
 * <pre>button.setBackground(new Color(0, 0, 0, 0));</pre>
 *
 * @author Edward Parrish
 * @version 1.1  12/25/2010
 */
public class Button extends GUIComponent {
    // Line spacing adjustment
    private static final double ASCENT_MULT = .85;
    // Default color for the mouseover (hover) state
    private static final Color DEFAULT_HOVER = new Color(238, 238, 238);

    // Button image in normal (up) state
    private GreenfootImage up;
    // Button image in pressed (down) state
    private GreenfootImage down;
    // Button image in mouseover (hover) state
    private GreenfootImage hover;
    // Button image supplied by user for normal (up) state
    private GreenfootImage bgUp;
    // Button image supplied by user for pressed (down) state
    private GreenfootImage bgDown;
    // Button image supplied by user for mouseover (hover) state
    private GreenfootImage bgHover;
    // Color for pressed (down) state
    private Color bgColorDown = Color.LIGHT_GRAY;
    // Color for mouseover (hover) state
    private Color bgColorHover = DEFAULT_HOVER;
    // Tracks the state of the button during painting
    private boolean hoverState;

    /**
     *  Creates a button with no set text or image.
     */
    public Button() {
        this("          ", 0);
    }

    /**
     * Creates a button with the specified text.
     *
     * @param labelText The text displayed on the button.
     */
    public Button(String labelText) {
        this(labelText, 0);
    }

    /**
     * Creates a button with the specified text and identifier number.
     *
     * @param text The text displayed on the button.
     * @param idNumber An identifier number.
     */
    public Button(String text, int idNumber) {
        super(text);
        setID(idNumber);
        repaint();
    }

    /**
     * Creates a button with the specified text and identifier number.
     *
     * @param text The text displayed on the button.
     * @param font The font used to write on this component.
     * @param textColor The color to use for displaying text.
     */
    public Button(String text, Font font, Color textColor) {
        super(text, font, textColor, DEFAULT_BG);
        repaint();
    }

    /**
     * Creates a button with the specified text and identifier number.
     *
     * @param text The text displayed on the button.
     * @param font The font used to write on this component.
     * @param textColor The color to use for displaying text.
     * @param bgColor The desired background color.
     */
    public Button(String text, Font font, Color textColor, Color bgColor) {
        super(text, font, textColor, bgColor);
        repaint();
    }

    /**
     * Creates a button using the specified images. The images may be
     * written on with <code>setText()</code>.
     *
     * @param normalBG The image displayed when the button is up (normal).
     * @param pressedBG The image displayed when the button is down (pressed).
     * @param hoverBG The image displayed when the mouse is hovering over the
     * button.
     * @throws IllegalArgumentException when an image is null.
     */
    public Button(GreenfootImage normalBG, GreenfootImage pressedBG,
            GreenfootImage hoverBG) {
        if (normalBG == null) {
            throw new IllegalArgumentException("normalBG is null.");
        }
        if (pressedBG == null) {
            throw new IllegalArgumentException("pressedBG is null.");
        }
        if (normalBG == null) {
            throw new IllegalArgumentException("hoverBG is null.");
        }
        // Make defensive copies
        bgUp = new GreenfootImage(normalBG);
        up = new GreenfootImage(normalBG);
        bgDown = new GreenfootImage(pressedBG);
        down = new GreenfootImage(pressedBG);
        bgHover = new GreenfootImage(hoverBG);
        hover = new GreenfootImage(hoverBG);

        setImage(normalBG);
        setBorder(null);
    }

    /**
     * React to the mouse including rollovers and button clicks.
     */
    public void act() {
        if (isEnabled()) {
            super.act(); // check for focus request
            if (Greenfoot.mouseMoved(this)) {
                setImage(hover);
            } else if (Greenfoot.mouseMoved(null)) {
                setImage(up);
            }
            if (Greenfoot.mousePressed(this)) {
                setImage(down);
            }
            if (Greenfoot.mouseClicked(null)
                    || Greenfoot.mouseDragEnded(null)) {
                setImage(up);
            }
            if (Greenfoot.mouseClicked(this)) {
                setImage(hover);
                fireActionEvent();
            }
        }
    }

    /**
     * Returns true if this button is currently down (pressed), otherwise
     * returns false.
     *
     * @return <code>true</code> if this button is currently pressed, otherwise
     * <code>false</code>.
     */
    public boolean isPressed() {
        return down == getImage();
    }

    /**
     * Resizes this button so that it has width <code>d.width</code>
     * and height <code>d.height</code>.
     *
     * @param d The dimension specifying the new size.
     */
    public void setSize(Dimension d) {
        // Adjust the images if they were used
        if (bgUp != null) {
            up.scale(d.width, d.height);
            bgUp.scale(d.width, d.height);
        }
        if (bgDown != null) {
            down.scale(d.width, d.height);
            bgDown.scale(d.width, d.height);
        }
        if (bgHover != null) {
            hover.scale(d.width, d.height);
            bgHover.scale(d.width, d.height);
        }
        // Let super handle the rest
        super.setSize(d);
    }

    /**
     * Set the background color of this button when down (pressed).
     *
     * @param bgDownColor The desired background color when pressed.
     */
    public void setBackgroundPressed(Color bgDownColor) {
        bgColorDown = bgDownColor;
        repaint();
    }

    /**
     * Set the background color of this button on hover (mouseover).
     *
     * @param bgHoverColor The desired background color on hover.
     */
    public void setBackgroundHover(Color bgHoverColor) {
        bgColorHover = bgHoverColor;
        repaint();
    }

    /**
     * Prepares the button's background images.
     *
     * If the images are supplied by the user, then copies those images onto
     * the background. Otherwise, fills the background images with their
     * respective colors.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    public void paintComponent(Graphics g) {
        hoverState = (hover == getImage());
        // Calculate sizes
        int width = getWidth();
        int height = getHeight();
        if (!isFixedSize()) {
            // Calculate width and height needed for text
            Dimension d = getTextDimension(getText(), g);
            width = d.width;
            height = d.height;
        }

        // Prepare up image
        if (bgUp == null) {
            up = prepareImage(up, getBackground(), width, height);
        } else {
            up.drawImage(bgUp, 0, 0);
        }
        // Prepare down image
        if (bgDown == null) {
            down = prepareImage(down, bgColorDown, width, height);
        } else {
            down.drawImage(bgDown, 0, 0);
        }
        // Prepare hover image
        if (bgHover == null) {
            hover = prepareImage(hover, bgColorHover, width, height);
        } else {
            hover.drawImage(bgHover, 0, 0);
        }
        // Set image so border is drawn correctly
        setImage(up);
    }

    /**
     * Helper method to prepare images for writing text.
     *
     * @param img The image to prepare.
     * @param bgColor The background color with which to fill the image.
     * @param imgWidth The width of the image to create or rework.
     * @param imgHeight The height of the image to create or rework.
     * @return The prepared image.
     */
    private GreenfootImage prepareImage(GreenfootImage img, Color bgColor,
            int imgWidth, int imgHeight) {
        // Create new image when needed, otherwise reuse it
        if (img == null || img.getWidth() != imgWidth
                || img.getHeight() != imgHeight || bgColor.getAlpha() < 255) {
            img = new GreenfootImage(imgWidth, imgHeight);
        }
        img.setColor(bgColor);
        img.fill();
        return img;
    }

    /**
     * Paints this component's border, if it has one.
     *
     * @param g  the <code>Graphics</code> context in which to paint.
     */
    protected void paintBorder(Graphics g) {
        super.paintBorder(up.getAwtImage().getGraphics());
        super.paintBorder(down.getAwtImage().getGraphics());
        super.paintBorder(hover.getAwtImage().getGraphics());
    }

    /**
     * Paints the text onto the buttons background image.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    public void paintText(Graphics g) {
        String btnText = getText();
        if (btnText == null || btnText.length() == 0) return;
        String[] lines = splitLines(btnText, g);

        // Add up (normal) text
        if (getFont() != null) up.setFont(getFont());
        if (getForeground() != null) up.setColor(getForeground());
        printText(up, lines, 0);

        // Add down (pressed) text
        if (getFont() != null) down.setFont(getFont());
        if (getForeground() != null) down.setColor(getForeground());
        printText(down, lines, 1);

        // Add hover (mouseover) text
        if (getFont() != null) hover.setFont(getFont());
        if (getForeground() != null) hover.setColor(getForeground());
        printText(hover, lines, 0);

        // Set initial image to display
        if (hoverState) {
            setImage(hover);
        } else {
            setImage(up);
        }
    }

    /**
     * Helper method to print text onto the background image.
     *
     * @param img The image to write onto.
     * @param lines The lines of text to write.
     * @param offset The offset to the baseline (used for pressed effect).
     */
    private void printText(GreenfootImage img, String[] lines, int offset) {
        Graphics g = img.getAwtImage().getGraphics();
        if (getFont() != null) g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = (int) (fm.getHeight() * ASCENT_MULT);
        int height = lineHeight * lines.length;
        int width = 0;
        int y = lineHeight + (img.getHeight() - height - fm.getDescent()) / 2;
        for (int i = 0; i < lines.length; i++) {
            int lineWidth = fm.stringWidth(lines[i]);
            width = Math.max(width, lineWidth);
            int x = (img.getWidth() - lineWidth) / 2;
            img.drawString(lines[i], x + offset, y + offset);
            y += lineHeight;
        }
        if (isFocusOwner()) {
            img.setColor(Color.GRAY);
            int x = (img.getWidth() - width) / 2 - 1;
            y = (img.getHeight() - height) / 2;
            img.drawRect(x + offset, y + offset, width + 1, height);
        }
    }
}
