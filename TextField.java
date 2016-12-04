import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

/**
 * Class <code>TextField</code> is a Greenfoot component that allows the user
 * to enter a single line of text.
 *
 * When users are finished entering text, they can signal they are finished by
 * pressing the Enter key. Pressing the Enter key will fire the ActionListener
 * interface.
 *
 * The <code>ActionListener</code> interface is like the
 * <code>ActionListener</code> interface in Java Swing. Scenarios using the
 * <code>ActionListener</code> callback must include the
 * <code>ActionListener</code> interface, and the class listening for button
 * clicks must implement that interface. In addition, the implementing class
 * must add itself to the callback list by calling the
 * <code>addActionListener()</code> method. Then when the Enter key is pressed,
 * the <code>actionPerformed()</code> method in the listening class will be
 * called.
 *
 * The developer can read the text the user entered by calling the
 * <code>getText()</code> method.
 *
 * @author Ed Parrish
 * @version 1.0  12/28/2010
 * @see http://java.sun.com/developer/onlineTraining/Media/2DText/index.html
 */
public class TextField extends GUIComponent {
    private static final Color CARET_COLOR = Color.RED;
    private static final Color HIGHLIGHT_COLOR = Color.PINK;
    private static final long KEYSTOKE_DELAY = 110;
    private static final long BLINK_DELAY = 400;
    // Padding to leave small horizontal gap between box edge and cursor
    private static final int X_PAD = 1;
    // Input text buffer
    private StringBuilder input;
    // Width of the input field
    private int numCols;
    // TextLayout is used for drawing and measuring. Since it is expensive to
    // create, cache it and invalidate it when text is modified.
    private TextLayout layout = null;
    private boolean validTextLayout = false;
    // Timer variable used to prevent keystrokes from going too fast
    private long lastKeystrokeTime;
    // The main insertion index.
    private int index1 = 0;
    // The second insertion index used for mouse selection
    private int index2;
    // The x-coordinate of the origin relative to the left side of the component
    private float originX;
    // The y-coordinate of the origin relative to the top of the component
    private float originY;
    // Component with the cursor showing
    private GreenfootImage cursorOn;
    // Component without the cursor showing
    private GreenfootImage cursorOff;
    // Timer variable to control blinking cursor
    private long lastBlinkTime;
    // Variable to control blink state
    private boolean blinkOn;


    /**
     * Constructs a text field with not text and with a width of one column.
     */
    public TextField() {
        this ("", 1);
    }

    /**
     * Constructs a text field with the initial text displayed in the default
     * text color and font and with a width of one column.
     *
     * @param text The initial text to display on the text field.
     */
    public TextField(String text) {
        this (text, 1);
    }

    /**
     * Constructs a text field with the initial text displayed in the default
     * text color and font.
     *
     * @param columns Number of columns to calculate the preferred width.
     */
    public TextField(int columns) {
        this ("", columns);
    }

    /**
     * Constructs a text field with the initial text displayed in the default
     * text color and font.
     *
     * @param text The initial text to display on the text field.
     * @param columns Number of columns to calculate the preferred width.
     */
    public TextField(String text, int columns) {
        this (text, columns, DEFAULT_FONT, DEFAULT_FG, Color.WHITE);
    }

    /**
     * Constructs a text field with the initial text displayed in the default
     * text color and font.
     *
     * @param text The initial text to display on the text field.
     * @param columns Number of columns to calculate the preferred width.
     * @param font The font used to write on this component.
     */
    public TextField(String text, int columns, Font font) {
        this (text, columns, font, DEFAULT_FG, Color.WHITE);
    }

    /**
     * Constructs a text field with the initial text displayed in the specified
     * font and text color on the specified background color.
     *
     * @param text The initial text to display on the text field.
     * @param columns Number of columns to calculate the preferred width.
     * @param font The font used to write on this component.
     * @param fg The desired foreground color.
     * @param bg The desired background color.
     */
    public TextField(String text, int columns, Font font, Color fg, Color bg) {
        super(text, font, fg, bg, null);
        numCols = columns;
        input = new StringBuilder(text);
        super.repaint();
        originX = getInsets().left + X_PAD;
    }

    /**
     * Handle keystrokes and mouse actions for this component.
     */
    public void act() {
        super.act(); // check for focus request
        if (!isEnabled()) return;

        // Move caret using mouse pointer
        if (Greenfoot.mousePressed(this)) {
            MouseInfo e = Greenfoot.getMouseInfo();
            if (e != null && layout != null) {
                index1 = getHitPosition(e.getX(), e.getY());
                index2 = index1;
            }
            // Repaint the component so the new caret will be displayed.
            super.repaint();
        }

        // Highlight text with mouse pointer
        if (Greenfoot.mouseDragged(this)) {
            MouseInfo e = Greenfoot.getMouseInfo();
            if (e != null && layout != null) {
                index2 = getHitPosition(e.getX(), e.getY());
            }
            // Repaint the component so the highlight will be displayed.
            super.repaint();
        }

        // Ignore keystrokes if not the focus owner
        if (!isFocusOwner()) return;

        // Blink the cursor
        if (System.currentTimeMillis() - lastBlinkTime > BLINK_DELAY) {
            lastBlinkTime = System.currentTimeMillis();
            blinkOn = !blinkOn;
            if (blinkOn) {
                setImage(cursorOn);
            } else {
                setImage(cursorOff);
            }
        }

        // Slow down keystrokes on fast systems
        if (System.currentTimeMillis() - lastKeystrokeTime < KEYSTOKE_DELAY) {
            return;
        }
        lastKeystrokeTime = System.currentTimeMillis();

        // Process keystrokes
        TextHitInfo newPosition = null;
        String key = Greenfoot.getKey();
        if (key != null && key.length() == 1) {
            // Single character keystrokes like: 'a'...'z', '0'...'9'
            deleteHighlighedChars();
            input.insert(index1, key);
            index1++;
            index2 = index1;
            repaint();
        } else if (Greenfoot.isKeyDown("space")) {
            deleteHighlighedChars();
            input.insert(index1, " ");
            index1++;
            index2 = index1;
            repaint();
        } else if (Greenfoot.isKeyDown("backspace")) {
            if (index1 != index2) {
                deleteHighlighedChars();
                repaint();
            } else if (input.length() > 0 && index1 > 0) {
                --index1;
                input.deleteCharAt(index1);
                index2 = index1;
                repaint();
            }
        } else if (Greenfoot.isKeyDown("enter")) {
            fireActionEvent();
        } else if (Greenfoot.isKeyDown("right") && layout != null) {
            newPosition = layout.getNextRightHit(index2);
        } else if (Greenfoot.isKeyDown("left") && layout != null) {
            newPosition = layout.getNextLeftHit(index1);
        }
        if (newPosition != null) {
            index1 = newPosition.getInsertionIndex();
            index2 = index1;
            super.repaint();
        }
    }

    /**
     * Set the text to be displayed.
     *
     * @param newText The new text to be displayed on the button.
     */
    public void setText(String newText) {
        if (newText == null) {
            input.replace(0, input.length(), "");
        } else {
            input.replace(0, input.length(), newText);
        }
        index1 = 0;
        index2 = index1;
        repaint();
    }

    /**
     * Returns the text for this component.
     *
     * @return The text displayed on this component.
     */
    public String getText() {
        return input.toString();
    }

    /**
     * Sets the number of columns in this <code>TextField</code>.
     *
     * @param columns the number of columns >= 0
     * @exception IllegalArgumentException if <code>columns</code>
     *      is less than 0
     */
    public void setColumns(int columns) {
        int oldVal = numCols;
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero.");
        }
        if (numCols != oldVal) {
            numCols = columns;
            repaint();
        }
    }

    /**
     * Returns the number of columns in this <code>TextField</code>.
     *
     * @return the number of columns >= 0.
     */
    public int getColumns() {
        return numCols;
    }

    /**
     * Paints the component image, including the background, border and text.
     *
     * This method recreates the text layout. To repaint without recreating
     * the text layout, call <code>super.repaint()</code>.
     */
    public void repaint() {
        validTextLayout = false;
        super.repaint();
    }

    /**
     * Paints the text field's background image.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    public void paintComponent(Graphics g) {
        GreenfootImage img = null;
        if (isFixedSize()) {
            img = new GreenfootImage(getWidth(), getHeight());
        } else {
            // Calculate image size based on text height & numCols
            FontMetrics fm = g.getFontMetrics();
            String text = getText();
            if (text == null) text = "";
            int width = fm.charWidth('m') * numCols;
            if (width <= 0) width = 1;
            int height = fm.getHeight();
            Insets insets = getInsets();
            if (insets != null) {
                width += insets.left + insets.right;
                height += insets.top + insets.bottom;
            }
            img = new GreenfootImage(width, height);
        }
        if (getBackground().getTransparency() > 0) {
            img.setColor(getBackground());
            img.fill();
        }
        // Paint box around the text field if no border supercedes it
        if (getBorder() == null) {
            img.setColor(Color.BLACK);
            img.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
            // Draw thicker line if this component has the focus
            if (isFocusOwner()) {
                img.drawRect(1, 1, img.getWidth() - 3, img.getHeight() - 3);
            }
        }
        setImage(img);
    }

    /**
     * Paints the text onto the labels background image.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    public void paintText(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // Draw the text that the user has entered
        TextLayout tl = getTextLayout(g2);
        if (tl != null) {
            scrollToVisible();
            AffineTransform at
                = AffineTransform.getTranslateInstance(originX, originY);

            if (isFocusOwner()) {
                // Draw the highlight
                Shape hilight = tl.getLogicalHighlightShape(index1, index2);
                hilight = at.createTransformedShape(hilight);
                g2.setColor(HIGHLIGHT_COLOR);
                g2.fill(hilight);
            }
            if (getForeground() != null) g2.setColor(getForeground());
            tl.draw(g2, originX, originY);
            cursorOff = new GreenfootImage(getImage());
            if (isFocusOwner()) {
                // Draw the caret at the insertion index
                Shape[] carets = tl.getCaretShapes(index2);
                g2.setColor(CARET_COLOR);
                Shape caret = at.createTransformedShape(carets[0]);
                g2.draw(caret);
            }
            cursorOn = getImage();
        } else if (isFocusOwner()) {
            // Draw a caret for the empty box
            cursorOff = new GreenfootImage(getImage());
            g2.setColor(CARET_COLOR);
            Insets i = getInsets();
            g2.drawLine(i.left + X_PAD, i.top, i.right + X_PAD,
                getHeight() - i.bottom);
            cursorOn = getImage();
        }
    }

    /**
     * Scrolls the field left or right until it is visible.
     */
    private void scrollToVisible() {
        if (layout == null) return;
        Insets i = getInsets();
        int xMin = i.left + X_PAD;
        int xMax = getWidth() - i.right - X_PAD;
        int cursorX = layout.getCaretShapes(index2)[0].getBounds().x;
        float adjustX = layout.getAdvance() - xMax + originX;
        if (originX < xMin && adjustX < 0) {
            // adjust right margin when characters deleted
            originX = originX - adjustX;
            if (originX > xMin) originX = xMin;
        }
        if (cursorX < xMin - originX) {
            // Scroll to the left
            originX = xMin - cursorX;
        } else if (cursorX > xMax - originX) {
            // Scroll to the right
            originX = xMax - cursorX;
        }
    }

    /**
     * Returns a text layout for the text that the user has entered. The text
     * layout is cached until <code>invalidateTextLayout</code> is called.
     *
     * @param g  the <code>Graphics</code> context in which to paint.
     * @return a <code>TextLayout</code> for the text that the user has
     *         entered, or <code>null</code> if no text has been entered.
     */
    private TextLayout getTextLayout(Graphics g) {
        if (!validTextLayout) {
            layout = null;
            if (input.length() > 0) {
                Graphics2D g2 = (Graphics2D) g;
                FontRenderContext frc = g2.getFontRenderContext();
                layout = new TextLayout(getText(), getFont(), frc);
                originY = (getHeight() + layout.getAscent()
                           - layout.getDescent()) / 2;
            }
        }
        validTextLayout = true;
        return layout;
    }

    /**
     * Computes the character position of the mouse cursor.
     *
     * @param mouseX The x-coordinate of the mouse pointer.
     * @param mouseY The y-coordinate of the mouse pointer.
     * @return The character position of the mouse cursor.
     */
    private int getHitPosition(int mouseX, int mouseY) {
        // Compute mouse click location relative to text layout's origin
        float x = mouseX - getX() + getWidth() / 2 - originX;
        float y = mouseY - getY() + getHeight() / 2;
        // Get the character position of the mouse click.
        TextHitInfo hit = layout.hitTestChar(x, y);
        return hit.getInsertionIndex();
    }

    /**
     * Deletes the characters within the highlighted area.
     */
    private void deleteHighlighedChars() {
        if (index2 > index1) {
            input.delete(index1, index2);
            index2 = index1;
        } else if (index1 > index2) {
            input.delete(index2, index1);
            index1 = index2;
        }
    }
}
