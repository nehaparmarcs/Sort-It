import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.*;


public class GameManager extends World implements ActionListener {
    public static final Font LARGE_FONT = new Font("SansSerif", Font.BOLD, 30);
    public static final Font MED_FONT = new Font("SansSerif", Font.PLAIN, 24);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 20);
    public static final int ROWS = 5;
    public static final int COLS = 6;
    public static final int BORDER = 20;

    private static final Font CAT_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font SCORE_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final int MAX_CATEGORIES = 7;
    private static final int MAX_QUESTS = 31;
    private static final int DEFAULT_TIMER = 15;
    private static final int FILL_PAUSE = 90;

    private String[] categories;
    private String[] methodCategories;
    private List<Label> catLabels;
    private Question[] questions;
    private int score;
    private Label scoreAmount;
    
    private Button[][] grid = new Button[COLS][ROWS];
    private Question currentQuestion;
    private Response response;
    private SplashScreen splash = new SplashScreen();
    private SettingsScreen settings = new SettingsScreen();
    //Neha s
    private UserEntryScreen userEntry = new UserEntryScreen();
    //Neha e
    private BetScreen betScreen;
    private CountDownTimer timer = new CountDownTimer(DEFAULT_TIMER);
    private int questionCount;
    private int numQuestions;
    private int questionsAdded;
    private long lastAddTime;
    private boolean provideTimer = true;
    private boolean provideChoices = true;
    private Label errorMsg;
    private OverScreen over;
    private boolean playedFinal;
//    RestClient rs  = null;

    /**
     * Initializing GameManager.
     */
    
    public GameManager() {
        super(600, 454, 1);
        Label label = new Label("Score:", SCORE_FONT, Color.CYAN);
        addObject(label, 500, 444);
        scoreAmount = new Label("" + score, SCORE_FONT, Color.CYAN);
        addObject(scoreAmount, 530 + scoreAmount.getWidth() / 2, 444);
        response = new Response(null, Response.Type.WRONG);
        errorMsg = new Label("", LARGE_FONT, Color.RED);
        errorMsg.setSize(new Dimension(getWidth() - 50, 100));
        addObject(errorMsg, getWidth() / 2, getHeight() - 50);
        
        //Added Neha starts 
        methodCategories = new String[5]; //We have 5 sorting games
        
        //Added Neha ends
        
        //rs = new RestClient();
        System.out.println("Pushing player data to Cloud ==== > Game ID: "+ userEntry.gameId +"\n User Name:"+ userEntry.userName);
        //rs.postData(userEntry.gameId, userEntry.userName);
        
        startGame();
//        Greenfoot.start();
    }

    // Accessor and setter methods

    /**
     * Set whether to provide choices for questions.
     *
     * @param b Set <code>true</code> to provide choices; otherwise set to
     *        <code>false</code>.
     */
    public void setProvideChoices(boolean b) {
        provideChoices = b;
    }

    /**
     * Return whether choices are provided or not.
     *
     * @return <code>true</code> if choices are provided; otherwise
     *         <code>false</code>.
     */
    public boolean getProvideChoices() {
        return provideChoices;
    }

    /**
     * Set whether to provide a timer or not.
     *
     * @param b Set <code>true</code> to provide a timer; otherwise set to
     *        <code>false</code>.
     */
    public void setProvideTimer(boolean b) {
        provideTimer = b;
    }

    /**
     * Return whether a timer is provided or not.
     *
     * @return <code>true</code> if a timer is provided; otherwise
     *         <code>false</code>.
     */
    public boolean getProvideTimer() {
        return provideTimer;
    }

    /**
     * Returns the current score.
     *
     * @return The current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the current question.
     *
     * @return The current question.
     */
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    // Public helper methods

    /**
     * Print an error message.
     *
     * @param msg The error message.
     */
    public void printErrorMessage(String msg) {
        // Ensure error message is on top
        removeObject(errorMsg);
        errorMsg.setText(msg);
        addObject(errorMsg, getWidth() / 2, getHeight() - 50);
    }

    /**
     * Clear the error message.
     */
    public void clearErrorMessage() {
        errorMsg.setText("");
    }

    /**
     * Start the timer.
     */
    public void startTimer() {
        if (provideTimer) {
            timer.start();
            addObject(timer, 565, 35);
        }
    }

    /**
     * Stop the timer.
     */
    public void stopTimer() {
        timer.stop();
        removeObject(timer);
    }

    /**
     * Used for board fill
     */
    public void act() {
        if (questionsAdded < numQuestions
                && System.currentTimeMillis() - lastAddTime > FILL_PAUSE) {
            lastAddTime = System.currentTimeMillis();
            addQuestionToBoard();
        }
    }

    /**
     * Add a single question to the game board
     */
    private void addQuestionToBoard() {
        int id = Greenfoot.getRandomNumber(30);
        int row = id % 5;
        int col = id / 5;
        while (questions[id] == null || grid[col][row] != null) {
            id++;
            row = id % 5;
            col = id / 5;
            if (id >= 30) {
                id = 0;
                row = id % 5;
                col = id / 5;
            }
        }
        // Now have a unique and valid question
        Question q = questions[id];
        int amount = q.getValue();
        String text = "" + amount;
        if (text.length() < 4) text = "  " + text;
        Button b = new Button(text, MED_FONT, Color.WHITE,
            new Color(0, 0, 0, 0));
        b.setBorder(null);
        b.setFocusable(false);
        b.setSize(new Dimension(85, 61));
        b.setBackgroundPressed(new Color(0, 0xCC, 0xFF));
        b.setBackgroundHover(new Color(0, 0x99, 0xFF));
        b.setID(id);
        b.addActionListener(this);
        grid[col][row] = b;
        addObject(b, 66 + 94 * col, 128 + row * 68);
        questionsAdded++;
    }

    /**
     * Make the game board.
     */
    public void makeBoard() {
        catLabels = new LinkedList<Label>();
        for (int i = 0; i < categories.length - 1; i++) {
            Label cat = new Label(categories[i], CAT_FONT, Color.WHITE);
            cat.setSize(new Dimension(86, 44));
            addObject(cat, 68 + 94 * i, 44);
            catLabels.add(cat);
        }
        // Ensure all grid elements set to null
        for (int col = 0; col < grid.length; col++) {
            for (int row = 0; row < grid[col].length; row++) {
                grid[col][row] = null;
            }
        }
        questionsAdded = 0; // Allows act() to start filling board
    }

    // Game flow methods

    
    public void startGame() {
        if (over != null) {
            over.clear();
            removeObject(over);
            over = null;
        }
        playedFinal = false;
        score = 0;
        scoreAmount.setText("" + score);
        scoreAmount.setLocation(530 + scoreAmount.getWidth() / 2,
            scoreAmount.getY());
        addObject(splash, 0, 0);
        //userEntry
        addObject(userEntry, getWidth() / 2, getHeight() / 2 + 37);
    }

    
    public void startRound() {
        questionCount = 0;
        boolean valid = false;
        while (!valid) {
            try {
                //System.out.println(userEntry.getUserName());
                loadRound("basics.txt");
                valid = true;
            } catch (IOException ioe) {
                String msg = ioe.getMessage();
                printErrorMessage(msg);
                return;
            }
        }
        clearErrorMessage();
        if (getObjects(SettingsScreen.class).size() != 0) settings.clear();
        if (getObjects(UserEntryScreen.class).size() != 0) userEntry.clear();
        if (getObjects(SplashScreen.class).size() != 0) splash.clear();
        makeBoard();
        Greenfoot.playSound("boardfill.wav");
        System.gc();
    }

    /**
     * Respond to a button being clicked by choosing a question to display.
     *
     * @param c The button object that was clicked.
     */
    public void actionPerformed(GUIComponent c) {
        int buttonID = c.getID();
        if (buttonID < 0 || buttonID >= questions.length) {
            System.out.println("Error: unknown button: " + c);
            return;
        }
        int row = buttonID % 5;
        int col = buttonID / 5;
        grid[col][row].setText("");
        grid[col][row].setEnabled(false);
        showQuestion(buttonID);
    }

    /**
     * Show the specified question.
     *
     * @param questionNum The question number to show.
     */
    private void showQuestion(int questionNum) {
        if (questionNum < questions.length) {
            Greenfoot.playSound("tone.wav");
            currentQuestion = questions[questionNum];
            if (currentQuestion != null) {
                currentQuestion.setDisplayAnswers(provideChoices);
                addObject(currentQuestion, getWidth() / 2, getHeight() / 2);
            }
        }
    }

    /**
     * Records an answer and advances.
     *
     * @param correct Set <code>true</code> if the answer was right; other set
     * <code>false</code>.
     */
    public void answerResponse(boolean correct) {
        stopTimer();
        if (correct) {
            score += currentQuestion.getValue();
            Greenfoot.playSound("ansright.wav");
            response.setType(Response.Type.RIGHT);
        } else {
            score -= currentQuestion.getValue();
            Greenfoot.playSound("answrong.wav");
            response.setType(Response.Type.WRONG);
        }
        scoreAmount.setText("" + score);
        scoreAmount.setLocation(530 + scoreAmount.getWidth() / 2,
            scoreAmount.getY());
        response.setQuestion(currentQuestion);
        addObject(response, getWidth() / 2, getHeight() / 2);
    }

    /**
     * Advances to the response screen when user is deciding if the answer
     * is right or wrong.
     */
    public void selfResponse() {
        stopTimer();
        response.setType(Response.Type.SELF);
        response.setQuestion(currentQuestion);
        addObject(response, getWidth() / 2, getHeight() / 2);
    }

    /**
     * Records the user evaluation of an answer and advances.
     *
     * @param correct Set <code>true</code> if the answer was right; other set
     * <code>false</code>.
     */
    public void answerSelf(boolean correct) {
        stopTimer();
        if (correct) {
            score += currentQuestion.getValue();
            Greenfoot.playSound("ansright.wav");
        } else {
            score -= currentQuestion.getValue();
            Greenfoot.playSound("answrong.wav");
        }
        scoreAmount.setText("" + score);
        scoreAmount.setLocation(530 + scoreAmount.getWidth() / 2,
            scoreAmount.getY());
        endQuestion();
    }

    /**
     * Responds to a timeout and advances.
     */
    public void timeout() {
        Greenfoot.playSound("timeout.wav");
        stopTimer();
        endQuestion();
    }

    /**
     * End the question and decide the next step.
     */
    public void endQuestion() {
        if (getObjects(Response.class).size() != 0) response.clear();
        if (getObjects(Question.class).size() != 0) currentQuestion.clear();
        questionCount++;
        if (questionCount == numQuestions && questions[30] != null) {
            startFinalRound();
        } else if (questionCount >= numQuestions || playedFinal) {
            gameOver();
        }
    }

    /**
     * Method startFinalRound
     */
    public void startFinalRound() {
        Greenfoot.playSound("applause.wav");
        if (score > 0 && questions[30] != null) {
            playedFinal = true;
            betScreen = new BetScreen(categories[6]);
            addObject(betScreen, getWidth() / 2, getHeight() / 2);
        } else {
            playedFinal = false;
            gameOver();
        }
    }

    /**
     * Start the final round.
     *
     * @param bet The amount of the bet.
     */
    public void finalRound(int bet) {
        questionCount++;
        questions[30].setValue(bet);
        showQuestion(30);
    }

    /**
     * Display the game over.
     */
    public void gameOver() {
        Greenfoot.playSound("applause.wav");
        settings.setFilename("basics2.txt");
        for (int col = 0; col < grid.length; col++) {
            for (int row = 0; row < grid[col].length; row++) {
                if (grid[col][row] != null) removeObject(grid[col][row]);
            }
        }
        removeObjects(catLabels);
        if (getObjects(BetScreen.class).size() != 0) betScreen.clear();
        addObject(splash, 0, 0);
        over = new OverScreen(score, playedFinal);
        addObject(over, 0, 0);
    }

    /**
     * Wraps a single string into an array of strings for some maximum
     * number of characters. Also will split lines on newlines or the
     * characters "\n" allowing simple manually-placed newlines.
     *
     * Changed the regex, dded max, added newline split, removed extra
     * blank line at end.
     *
     * @see http://joust.kano.net/weblog/archives/000060.html
     */
    public static String[] wordWrap(String str, int max) {
        if (max <= 0) return new String[0];
        Pattern newline = Pattern.compile("\\\\n");
        Matcher nl = newline.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (nl.find()) {
            nl.appendReplacement(sb, "\n");
        }
        nl.appendTail(sb);
        str = new String(sb);
        Pattern wrapRE =
            Pattern.compile(".{0,"+(max-1)+"}(?:\\S(?:-| |\n|$)|\n|$)");
        List<String> list = new LinkedList<String>();
        Matcher m = wrapRE.matcher(str);
        while (m.find()) list.add(m.group());
        if (list.get(list.size() - 1).equals("")) list.remove(list.size() - 1);
        return (String[]) list.toArray(new String[list.size()]);
    }

    private void loadRound(String filename) throws IOException {
        categories = new String[MAX_CATEGORIES];
        questions = new Question[MAX_QUESTS];
        ArrayList<String> lines = loadFile(filename);
        convertUnicode(lines);
        readCategories(lines);
        readQuestions(lines);
        readAnswers(lines);
        readAnswerOrder(lines);
        readExplanations(lines);
    }

    private ArrayList<String> loadFile(String filename) throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        // Allow loading from JAR files
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(filename);
        if (is == null) {
            throw new IOException("No such file: " + filename);
        }
        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is));
        String line = reader.readLine();
        while (line != null) {
            if (!line.equals("") && !line.startsWith("#")) {
                lines.add(line);
            }
            line = reader.readLine();
        }
        reader.close();
        return lines;
    }

    private void convertUnicode(ArrayList<String> lines) {
        Pattern unicode = Pattern.compile("\\\\u([0-9A-Fa-f]+)");
        for (int i = 0; i < lines.size(); i++) {
            Matcher m = unicode.matcher(lines.get(i));
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                int code = Integer.valueOf(m.group(1), 16);
                String uc = "" + Character.valueOf((char)code);
                m.appendReplacement(sb, uc);
            }
            m.appendTail(sb);
            lines.set(i, new String(sb));
        }
    }

    private void createMethodCategories(){
        methodCategories[0] = "Bubble Sort";
        methodCategories[1] = "Insertion Sort";
        methodCategories[2] = "Quick Sort";
        methodCategories[3] = "Selection Sort";
        methodCategories[4] = "Merge Sort";
        methodCategories[5] = "General Maths";
        
    }
    
    
    private void readCategories(final ArrayList<String> lines) {
        Pattern cat = Pattern.compile("(?i):CAT(\\d+):(.+)");
        Matcher m = cat.matcher("");
        for (int i = 0; i < lines.size(); i++) {
            m.reset(lines.get(i));
            if (m.matches()) {
                int index = Integer.valueOf(m.group(1)) - 1;
                if (index >= MAX_CATEGORIES) {
                    System.out.println("WARNING: Category number "
                    + (index + 1) + " exceeds maximum allowed ("
                    + MAX_CATEGORIES + ")");
                } else if (categories[index] != null) {
                    System.out.println("WARNING: repeating category "
                        + (index + 1));
                } else {
                    categories[index] = m.group(2);
                }
            }
        }
    }

    private void readQuestions(final ArrayList<String> lines) {
        Pattern qst = Pattern.compile("(?i):QUEST(\\d+):(\\d+):(.+)");
        Matcher m = qst.matcher("");
        numQuestions = 0;
        for (int i = 0; i < lines.size(); i++) {
            m.reset(lines.get(i));
            if (m.matches()) {
                int index = Integer.valueOf(m.group(1)) - 1;
                if (index >= MAX_QUESTS) {
                    System.out.println("ERROR: QUESTION number "
                    + (index + 1) + " exceeds maximum allowed ("
                    + MAX_QUESTS + ")");
                } else {
                    int value = Integer.valueOf(m.group(2));
                    Question question = new Question(m.group(3), value);
                    if (questions[index] != null) {
                        System.out.println("WARNING: repeating question "
                            + (index + 1));
                    } else {
                        numQuestions++;
                    }
                    questions[index] = question;
                }
            }
        }
        // Adjust count for final round question
        if (questions[30] != null) numQuestions--;
    }

    private void readAnswers(final ArrayList<String> lines) {
        Pattern ans = Pattern.compile("(?i):ANSWER(\\d+):(\\w):(.+)");
        Matcher m = ans.matcher("");
        for (int i = 0; i < lines.size(); i++) {
            m.reset(lines.get(i));
            if (m.matches()) {
                int index = Integer.valueOf(m.group(1)) - 1;
                if (index >= MAX_QUESTS) {
                    System.out.println("ERROR: ANSWER for question number "
                    + (index + 1) + " exceeds maximum allowed question ("
                    + MAX_QUESTS + ")");
                } else if (questions[index] == null) {
                        System.out.println("ERROR: ANSWER number "
                            + (index + 1) + "has no question.");
                } else {
                    String correct = m.group(2);
                    Question question = questions[index];
                    if (correct.equalsIgnoreCase("T")) {
                        question.addAnswer(m.group(3), true);
                    } else {
                        question.addAnswer(m.group(3), false);
                    }
                }
            }
        }
    }

    private void readAnswerOrder(final ArrayList<String> lines) {
        Pattern order = Pattern.compile("(?i):ANSWERORDER(\\d+):(.+)");
        Matcher m = order.matcher("");
        for (int i = 0; i < lines.size(); i++) {
            m.reset(lines.get(i));
            if (m.matches()) {
                int index = Integer.valueOf(m.group(1)) - 1;
                if (index >= MAX_QUESTS) {
                    System.out.println("ERROR: ANSWERORDER for question number "
                    + (index + 1) + " exceeds maximum allowed question ("
                    + MAX_QUESTS + ")");
                } else if (questions[index] == null) {
                        System.out.println("ERROR: ANSWERORDER number "
                            + (index + 1) + "has no question.");
                } else {
                    String text = m.group(2);
                    Question question = questions[index];
                    if (text.equalsIgnoreCase("randomized")) {
                        question.shuffleAnswers();
                    }
                }
            }
        }
    }

    private void readExplanations(final ArrayList<String> lines) {
        Pattern exp = Pattern.compile("(?i):EXPLANATION(\\d+):(.+)");
        Matcher m = exp.matcher("");
        for (int i = 0; i < lines.size(); i++) {
            m.reset(lines.get(i));
            if (m.matches()) {
                int index = Integer.valueOf(m.group(1)) - 1;
                if (index >= MAX_QUESTS) {
                    System.out.println("ERROR: EXPLANATION for question number "
                    + (index + 1) + " exceeds maximum allowed question ("
                    + MAX_QUESTS + ")");
                } else if (questions[index] == null) {
                        System.out.println("ERROR: EXPLANATION number "
                            + (index + 1) + "has no question.");
                } else {
                    Question question = questions[index];
                    String explanation = question.getExplanation();
                    if (explanation != null && !explanation.equals("")) {
                        System.out.println(
                            "WARNING: overwritting explanation for question "
                            + (index + 1));
                    }
                    question.setExplanation(m.group(2));
                }
            }
        }
    }
}
