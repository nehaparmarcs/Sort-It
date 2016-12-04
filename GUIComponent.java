import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A <em>component</em> is an object having a graphical representation
 * that can be displayed on the screen and that can interact with the
 * user.
 *
 * The <code>GUIComponent</code> class is the abstract superclass of components
 * such as Button, Label and TextField. Class <code>Component</code> can be
 * extended directly to create other components.
 *
 * @author Ed Parrish
 * @version 1.1  7/26/2011
 */
public abstract class GUIComponent extends Actor {
    public static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Color DEFAULT_FG = Color.BLACK;
    public static final Color DEFAULT_BG = new Color(204, 204, 204);
    // Space between text & edge can be superceded by Border
    private static final Insets DEFAULT_INSETS = new Insets(1, 3, 2, 3);

    // An identifying number for this component.
    private int id;
    // Initially enabled
    private boolean enabled = true;
    // Component made visible when added to the world
    private boolean visible = false;
    private String text = "";
    private Color foreground = DEFAULT_FG;
    private Color background = DEFAULT_BG;
    private Dimension size;
    private Font font = DEFAULT_FONT;
    // The default border for a component
    private Border border = new LineBorder();
    private List<ActionListener> listeners = new ArrayList<ActionListener>();

    /**
     * The component that will receive all keystrokes.
     */
    private static GUIComponent focusOwner;

    /**
     * Indicates whether this component can be focused.
     */
    private boolean focusable = true;

    /**
     * Constructs a new component.
     */
    public GUIComponent() { }

    /**
     * Constructs a new component with the specified text.
     *
     * The component will not display until <code>repaint()</code> is called
     * either directly or indirectly through calling a method like
     * <code>setFont()</code>.
     *
     * @param str The text to display on the component.
     */
    public GUIComponent(String str) {
        text = str;
        if (text == null) text = "";
    }

    /**
     * Constructs a new component setting the specified parameters.
     *
     * The component will not display until <code>repaint()</code> is called
     * either directly or indirectly through calling a method like
     * <code>setFont()</code>.
     *
     * @param str The text to display on the component.
     * @param f The font to use when displaying the text.
     * @param fg The foreground color to use when displaying text.
     * @param bg The background color of the component.
     */
    public GUIComponent(String str, Font f, Color fg, Color bg) {
        text = str;
        if (text == null) text = "";
        font = f;
        foreground = fg;
        background = bg;
    }

    /**
     * Constructs a new component setting the specified parameters.
     *
     * The component will not display until <code>repaint()</code> is called
     * either directly or indirectly through calling a method like
     * <code>setFont()</code>.
     *
     * @param str The text to display on the component.
     * @param f The font to use when displaying the text.
     * @param fg The foreground color to use when displaying text.
     * @param bg The background color of this component.
     * @param b The border to be rendered for this component.
     */
    public GUIComponent(String str, Font f, Color fg, Color bg, Border b) {
        text = str;
        if (text == null) text = "";
        font = f;
        foreground = fg;
        background = bg;
        border = b;
    }

    /**
     * Default behavior for a component that implements the focus system.
     *
     * Override in subclasses for more specific behavior. However, call
     * <code>super.act()</code> to allow focus changes.
     */
    public void act() {
        if (Greenfoot.mousePressed(this) && !isFocusOwner()) {
            requestFocus();
        }
    }

    /**
     * Set an ID number for this component.
     *
     * @param idNumber An identifier number.
     */
    public void setID(int idNumber) {
        id = idNumber;
    }

    /**
     * Return the ID number of this component.
     *
     * @return The ID number for this component.
     */
    public int getID() {
        return id;
    }

    /**
     * Enables or disables the component, depending on the value of the
     * parameter <code>b</code>.
     *
     * An enabled component can respond to user input and generate events.
     * Components are enabled initially by default.
     *
     * @param b Set true to enable the component, otherwise false.
     */
    public void setEnabled(boolean b) {
        enabled = b;
        if (enabled) {
            repaint();
        } else if (!enabled) {
            makeDisabledImage(getImage());
        }
    }

    /**
     * Converts an image to a "disabled" version.
     *
     * @param img The image to "disable".
     */
    private static void makeDisabledImage(GreenfootImage img) {
        if (img == null) return;
        final float RED_LUMINANCE = 0.229f;
        final float GREEN_LUMINANCE = 0.587f;
        final float BLUE_LUMINANCE = 0.114f;
        final int RGB_MIN = 148;
        final int RGBA_MAX = 255;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = img.getColorAt(x, y);
                if (c.getAlpha() >= RGBA_MAX) {
                    float red = c.getRed() * RED_LUMINANCE;
                    float green = c.getGreen() * GREEN_LUMINANCE;
                    float blue = c.getBlue() * BLUE_LUMINANCE;
                    int luminance = (int) (red + green + blue);
                    if (luminance < RGB_MIN) luminance = RGB_MIN;
                    Color color = new Color(luminance, luminance, luminance);
                    img.setColorAt(x, y, color);
                }
            }
        }
    }

    /**
     * Determines whether this component is enabled.
     *
     * An enabled component can respond to user input and generate events.
     * Components are enabled initially by default. A component may be enabled
     * or disabled by calling its <code>setEnabled()</code> method.
     *
     * @return Returns true if the component is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the text to be displayed.
     *
     * @param newText The new text to be displayed on this component.
     */
    public void setText(String newText) {
        String oldText = text;
        text = newText;
        if (text == null) text = "";
        if (text != oldText) repaint();
    }

    /**
     * Returns the text for this component.
     *
     * @return The text displayed on this component.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the font to use for writing the component text.
     *
     * @param newFont The font used to write on this component.
     */
    public void setFont(Font newFont) {
        Font oldFont = font;
        font = newFont;
        if (newFont != null) getImage().setFont(newFont);
        if (font != oldFont) repaint();
    }

    /**
     * Returns the current font used by this component, or null if
     * no font has been set.
     *
     * @return The current font or null if not font has been set.
     */
    public Font getFont() {
        if (font != null) {
            return font;
        }
        return getImage().getFont();
    }

    /**
     * Resizes this component so that it has width <code>d.width</code>
     * and height <code>d.height</code>.
     *
     * @param d The dimension specifying the new size.
     */
    public void setSize(Dimension d) {
        Dimension oldSize = size;
        size = new Dimension(d); // defensive copy
        if (size != oldSize) repaint();
    }

    /**
     * Returns the current height of this component.
     *
     * @return The height of this component.
     */
    public int getHeight() {
        if (size != null) {
            return size.height;
        } else {
            return getImage().getHeight();
        }
    }

    /**
     * Returns the current width of this component.
     *
     * @return The weight of this component.
     */
    public int getWidth() {
        if (size != null) {
            return size.width;
        } else {
            return getImage().getWidth();
        }
    }

    /**
     * Determines whether this component is set to a fixed size.
     *
     * By default a component will determine its own size based on properties
     * such as the text to display. However, the component can be set to a
     * fixed size using the <code>setSize()</code> method.
     *
     * @return Returns <code>true</code> if the component is a fixed size;
     *         <code>false</code> otherwise.
     */
    public boolean isFixedSize() {
        return size != null;
    }

    /**
     * Set the foreground color of this component, such as its text.
     *
     * @param fg The desired foreground color.
     */
    public void setForeground(Color fg) {
        Color oldColor = foreground;
        foreground = fg;
        if (foreground != oldColor) repaint();
    }

    /**
     * Gets the foreground color of this component.
     *
     * @return The foreground color of this component.
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * Set the background color of this component.
     *
     * @param bg The desired background color.
     */
    public void setBackground(Color bg) {
        Color oldColor = background;
        background = bg;
        if (background != oldColor) repaint();
    }

    /**
     * Gets the background color of this component.
     *
     * @return The background color of this component.
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Sets the border of this component. The <code>Border</code> object is
     * responsible for defining the insets for the component and for
     * rendering any border decorations.
     *
     * @param newBorder The border to be rendered for this component
     */
    public void setBorder(Border newBorder) {
        Border oldBorder = border;
        border = newBorder;
        if (border != oldBorder) repaint();
    }

    /**
     * Returns the border of this component or <code>null</code> if no
     * border is currently set.
     *
     * @return the border object for this component
     * @see #setBorder
     */
    public Border getBorder() {
        return border;
    }

    /**
     * Gets the insets of this component, which indicate the size of this
     * component's border.
     *
     * @return The insets of this component.
     */
    public Insets getInsets() {
        if (border != null) {
            return border.getBorderInsets();
        }
        return DEFAULT_INSETS;
    }

    /**
     * Returns a string representation of this component and its values.
     *
     * @return A string representation of this component.
     */
    public String toString() {
        return getClass().getName() + "(id=" + id + ", text=\"" + text
            + "\", width=" + size.width + ", height=" + size.height + ")";
    }

    // Focus management methods

    /**
     * Sets the focusable state of this <code>GUIComponent</code> to the
     * specified value. This value overrides the component's default
     * focusability.
     *
     * @param canFocus Determines whether this component is focusable.
     */
    public void setFocusable(boolean canFocus) {
        focusable = canFocus;
    }

    /**
     * Returns whether this <code>GUIComponent</code> can receive focus.
     *
     * @return <code>true</code> if this <code>GUIComponent</code> is
     *         focusable; <code>false</code> otherwise.
     */
    public boolean isFocusable() {
        return focusable;
    }

    /**
     * Requests that this component get the input focus.
     *
     * To receive focus, this component must be focusable and enabled.
     * Developers must never assume that this component is the focus owner
     * and must check the return value to be certain. If this method returns
     * true then focus was received. If <code>false</code> is returned,
     * the request failed.
     *
     * @return <code>true</code> if the focus succeeds and <code>false</code>
     *         if the focus fails.
     */
    public boolean requestFocus() {
        if (isFocusable() && isEnabled()) {
            if (focusOwner != null) {
                GUIComponent oldFocusOwner = focusOwner;
                focusOwner = null;
                oldFocusOwner.repaint(); // to remove focus decoration
            }
            // Clear KB buffer disabled due to null pointer on start up
            // java.lang.NullPointerException
            //  at greenfoot.Greenfoot.getKey(Greenfoot.java:86)
            // Evidently, Greenfoot is not always available when World is
            // Greenfoot.getKey(); // clear keyboard buffer
            focusOwner = this;
            focusOwner.repaint(); // add focus decoration
            return true;
        }
        return false;
    }

    /**
     * Returns <code>true</code> if this component is the focus owner.
     *
     * @return <code>true</code> if this component is the focus owner;
     *         <code>false</code> otherwise
     * @since 1.4
     */
    public boolean isFocusOwner() {
        return focusOwner == this;
    }

    // ActionListener callback management methods

    /**
     * Adds a class implementing the <code>ActionListener</code> interface to
     * the notification list.
     *
     * @param al The <code>ActionListener</code> to add to the notification
     *        list.
     */
    public void addActionListener(ActionListener al) {
        listeners.add(al);
    }

    /**
     * Notifies all listeners that have registered using
     * <code>addActionListener()</code> for notification.
     */
    protected void fireActionEvent() {
        for (int i = 0; i < listeners.size(); i++) {
            ActionListener al = listeners.get(i);
            al.actionPerformed(this);
        }
    }

    /**
     * Removes an <code>ActionListener</code> from the component.
     *
     * @param al The <code>ActionListener</code> to remove.
     */
    public void removeActionListener(ActionListener al) {
        listeners.remove(al);
    }

    // Paint management methods

    /**
     * Make visible when added to world.
     *
     * @param w the world.
     */
    public void addedToWorld(World w) {
        visible = true;
        repaint();
        if (!enabled) makeDisabledImage(getImage());
    }

    /**
     * Paints the component image, including the background, border and text.
     */
    public void repaint() {
        if (visible && enabled) {
            Graphics g = getGraphics();
            paintComponent(g);
            // Refresh the graphics context in case the image was changed
            g = getGraphics();
            paintBorder(g);
            paintText(g);  // paintChildren() in Swing
        }
    }

    /**
     * Gets a graphics context for this component from this Actor's image.
     *
     * @return a graphics context for this component.
     */
    public Graphics getGraphics() {
        Graphics g = getImage().getAwtImage().getGraphics();
        if (getFont() != null) g.setFont(getFont());
        if (getForeground() != null) g.setColor(getForeground());
        return g;
    }

    /**
     * Prepares the components background image.
     *
     * @param g  the <code>Graphics</code> context in which to paint.
     */
    public void paintComponent(Graphics g) { /* override in subclass */ }

    /**
     * Paints the component's border, if it has one.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    protected void paintBorder(Graphics g) {
        Border b = getBorder();
        if (b != null) {
            b.paintBorder(this, g);
        }
    }

    /**
     * Paints the text onto the components background image.
     *
     * @param g The <code>Graphics</code> context in which to paint.
     */
    public void paintText(Graphics g) { /* override in subclass */ }

    /**
     * Wraps a single string into an array of strings for some maximum
     * number of characters. Also will split lines on newlines or the
     * characters "\n" allowing simple manually placed newlines.
     *
     * @param str The text to word wrap.
     * @param max The maximum number of characters per line.
     * @return The word-wrapped lines of text.
     *
     * @author http://joust.kano.net/weblog/archives/000060.html
     * @author Ed Parrish (changed regex, added max, added newline split,
     * removed extra blank line at end)
     */
    public static String[] wordWrap(String str, int max) {
        if (max <= 0) return new String[0];
        Pattern wrapRE =
            Pattern.compile(".{0," + (max - 1) + "}(?:\\S(?:-| |\n|$)|\n|$)");
        List<String> list = new LinkedList<String>();
        Matcher m = wrapRE.matcher(str);
        while (m.find()) list.add(m.group());
        if (list.get(list.size() - 1).equals("")) list.remove(list.size() - 1);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Computes the dimension needed to display text in the specified
     * <code>Graphics</code> context.
     *
     * @param str The text to display on this component.
     * @param g The <code>Graphics</code> context in which to paint.
     * @return the dimension (width and height) needed.
     */
    public Dimension getTextDimension(String str, Graphics g) {
        // Calculate image size based on text width & height
        if (str == null) str = "";
        FontMetrics fm = g.getFontMetrics();
        // Allow multiple lines separated by newline characters ('\n')
        String[] lines = str.split("\n");
        int width = 1;
        int height = 0;
        Rectangle2D[] bounds = new Rectangle2D[lines.length];
        for (int i = 0; i < lines.length; i++) {
            bounds[i] = g.getFontMetrics().getStringBounds(lines[i], g);
            width = Math.max(width, (int) Math.ceil(bounds[i].getWidth()));
            height += Math.ceil(bounds[i].getHeight());
        }
        Insets insets = getInsets();
        if (insets != null) {
            width += insets.left + insets.right;
            height += insets.top + insets.bottom;
        }
        if (height <= 0) height = 1;
        return new Dimension(width, height);
    }

    /**
     * Splits the text into multiple lines in the specified
     * <code>Graphics</code> context.
     *
     * If the component size is fixed, then the text is word wrapped to fit.
     * Otherwise, text is split on '\n' characters.
     *
     * @param str The text to display on this component.
     * @param g The <code>Graphics</code> context in which to paint.
     * @return the split lines of text.
     */
    public String[] splitLines(String str, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        if (str == null) str = "";
        String[] lines = null;
        if (isFixedSize()) {
            int pixelsWide = fm.stringWidth(str);
            Insets insets = getInsets();
            if (insets != null) {
                pixelsWide += (insets.left + insets.right);
            }
            int maxChars = Math.round(getWidth()
                / ((float) pixelsWide / str.length()));
            lines = wordWrap(str, maxChars);
        } else {
            lines = str.split("\n");
        }
        return lines;
    }
}

/**
 * This interface defines the listener method: <code>actionPerformed()</code>.
 *
 * This listener interface can be used for callbacks like the Java Swing
 * <code>ActionListener</code> interface. The class processing the callback
 * must:
 * <ul>
 * <li>Implement this interface</li>
 * <li>Register to receive callbacks by calling the
 * <code>addActionListener()</code> method of <code>GUIComponent</code></li>
 * </ul>
 * See the <code>GUIWorld</code> class for examples of how to use this
 * interface.
 *
 * This interface is included in the <code>GUIComponent</code> class to make it
 * easier to use the components by reducing the number of files to copy into a
 * project.
 *
 * @author Edward Parrish
 * @version 1.2  12/27/2010
 */
interface ActionListener {
    /**
     * A GUIComponent in the world invoked a command.
     *
     * @param c The GUIComponent making the command
     */
    public void actionPerformed(GUIComponent c);
}

/**
 * Interface describing an object capable of rendering a border
 * around the edges of a <code>GUIComponent</code>.
 *
 * See the <code>GUIWorld</code> class for examples of how to use this
 * interface.
 *
 * You can write your own border class by implementing this interface.
 *
 * This interface is included in the <code>GUIComponent</code> class to make it
 * easier to use the components by reducing the number of files to copy into a
 * project.
 *
 * @author Ed Parrish
 * @version 1.0  01/05/2011
 * @see LineBorder
 */
interface Border {
    /**
     * Paints the border for the specified component.
     *
     * @param c The component for which this border is being painted.
     * @param g The graphics context in which to paint.
     */
    void paintBorder(GUIComponent c, Graphics g);

    /**
     * Returns the insets of the border.
     *
     * @return A new <code>Insets</code> object with the required values
     *         needed for drawing the border.
     */
    Insets getBorderInsets();
}

/**
 * A class which implements a simple line border in the color black.
 *
 * This interface is included in the <code>GUIComponent</code> class to make it
 * easier to use the components by reducing the number of files to copy into a
 * project.
 *
 * See the <code>GUIWorld</code> class for examples of how to use this
 * interface.
 *
 * @author Ed Parrish
 * @version 1.0  01/05/2011
 */
class LineBorder implements Border {
    // Padding to leave small horizontal gap between box edge and cursor
    private static final int X_PAD = 3;
    private Color color;
    private int thickness;

    /**
     * Creates a simple line border in the color black and a thickness of one.
     */
    public LineBorder() {
        this(Color.BLACK, 1);
    }

    /**
     * Creates a simple line border with the specified color and thickness.
     *
     * @param lineColor The color of the border.
     * @param lineThickness The thickness of the border.
     */
    public LineBorder(Color lineColor, int lineThickness) {
        color = lineColor;
        thickness = lineThickness;
    }

    /**
     * Paints the border for the specified component.
     *
     * @param c The component for which this border is being painted.
     * @param g The graphics context in which to paint.
     */
    public void paintBorder(GUIComponent c, Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(color);
        for (int i = 0; i < thickness; i++)  {
            g.drawRect(i, i, c.getWidth() - i - i - 1,
                c.getHeight() - i - i - 1);
        }
        g.setColor(oldColor);
    }

    /**
     * Returns a new <code>Insets</code> object where the <code>top</code>,
     * <code>left</code>, <code>bottom</code>, and <code>right</code>
     * fields return the values needed for drawing the border.
     *
     * @return A new <code>Insets</code> object with the required values
     *         needed for drawing the border.
     */
    public Insets getBorderInsets()       {
        return new Insets(thickness, thickness + X_PAD, thickness,
            thickness + X_PAD);
    }
}
