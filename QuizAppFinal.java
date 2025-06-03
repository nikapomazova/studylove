import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.net.*;
import java.util.*;
import org.json.*;

public class QuizAppFinal {
    // Configuration
    private static final String TRIVIA_API = "https://opentdb.com/api.php?amount=1&type=multiple";
    private static final String USER_FILE = "login.txt";
    
    // Color Scheme
    private static final Color PRIMARY_COLOR = new Color(48, 63, 159);
    private static final Color SECONDARY_COLOR = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(255, 213, 79);
    private static final Color CORRECT_COLOR = new Color(67, 160, 71);
    private static final Color WRONG_COLOR = new Color(229, 57, 53);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BUTTON_BG = new Color(240, 240, 240);
    
    // App State
    private static Map<String, User> users = new HashMap<>();
    private static User currentUser;
    private static int currentDifficulty = 1;
    private static int correctAnswerIndex = -1;
    
    // UI Components
    private static JFrame frame;
    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    private static JTextField authUsernameField;
    private static JPasswordField authPasswordField;
    private static JLabel coinsLabel;
    private static JLabel difficultyLabel;
    private static JTextArea questionArea;
    private static JButton[] optionButtons = new JButton[4];
    private static JTextField topicField;
    private static JLabel welcomeLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            loadUsers();
            createGUI();
        });
    }

    private static void createGUI() {
        setupLookAndFeel();
        
        // Main Window
        frame = new JFrame("Quiz Master Pro");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setMinimumSize(new Dimension(900, 700));
        frame.setLocationRelativeTo(null);
        
        // Card Layout for Screens
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(SECONDARY_COLOR);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create All Screens
        createAuthScreen();
        createMainScreen();
        createQuizScreen();
        
        frame.add(cardPanel);
        frame.setVisible(true);
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAuthScreen() {
        JPanel authPanel = new JPanel(new GridBagLayout());
        authPanel.setBackground(SECONDARY_COLOR);
        authPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        // Title
        JLabel titleLabel = new JLabel("QUIZ MASTER PRO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(PRIMARY_COLOR);
        authPanel.add(titleLabel, gbc);
        
        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Test your knowledge and win coins!");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_COLOR);
        authPanel.add(subtitleLabel, gbc);
        
        // Username Field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        authPanel.add(userLabel, gbc);
        
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        authUsernameField = new JTextField(20);
        styleTextField(authUsernameField);
        authPanel.add(authUsernameField, gbc);
        
        // Password Field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        authPanel.add(passLabel, gbc);
        
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        authPasswordField = new JPasswordField(20);
        styleTextField(authPasswordField);
        authPanel.add(authPasswordField, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        JButton loginButton = createPrimaryButton("Login");
        loginButton.addActionListener(e -> login());
        loginButton.setForeground(Color.BLACK);
        buttonPanel.add(loginButton);
        
        JButton registerButton = createSuccessButton("Register");
        registerButton.setForeground(Color.BLACK);
        registerButton.addActionListener(e -> register());
        buttonPanel.add(registerButton);
        
        authPanel.add(buttonPanel, gbc);
        
        cardPanel.add(authPanel, "auth");
    }

    private static void createMainScreen() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        coinsLabel = new JLabel("Coins: 0");
        coinsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        coinsLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(coinsLabel, BorderLayout.EAST);
        
        welcomeLabel = new JLabel("Welcome, Player!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30);
        
        // Instruction Label
        JLabel instructionLabel = new JLabel("Ready to test your knowledge?");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        instructionLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 0;
        centerPanel.add(instructionLabel, gbc);
        
        // Start Button
        JButton startButton = createPrimaryButton("START QUIZ");
        startButton.setForeground(Color.BLACK);
        startButton.setPreferredSize(new Dimension(250, 60));
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "quiz");
            resetQuestionDisplay();
        });
        gbc.gridy++;
        centerPanel.add(startButton, gbc);
        
        // Logout Button
        JButton logoutButton = createDangerButton("LOGOUT");
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setPreferredSize(new Dimension(250, 50));
        logoutButton.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(cardPanel, "auth");
        });
        gbc.gridy++;
        centerPanel.add(logoutButton, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        cardPanel.add(mainPanel, "main");
    }

    private static void createQuizScreen() {
        JPanel quizPanel = new JPanel(new BorderLayout());
        quizPanel.setBackground(SECONDARY_COLOR);
        quizPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        difficultyLabel = new JLabel("Difficulty: 1");
        difficultyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        difficultyLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(difficultyLabel, BorderLayout.EAST);
        
        JButton backButton = createSecondaryButton("← Back to Menu");
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "main");
            updateCoinsDisplay();
        });
        headerPanel.add(backButton, BorderLayout.WEST);
        
        quizPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel with Scroll
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Topic Selection
        JPanel topicPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topicPanel.setOpaque(false);
        
        JLabel topicLabel = new JLabel("Enter topic to study:");
        topicLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topicPanel.add(topicLabel);
        
        topicField = new JTextField(20);
        styleTextField(topicField);
        topicField.setMaximumSize(new Dimension(300, 35));
        topicPanel.add(topicField);
        
        centerPanel.add(topicPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Question Display (using JTextArea in ScrollPane)
        questionArea = new JTextArea();
        questionArea.setFont(new Font("Segoe UI", Font.BOLD, 18));
        questionArea.setForeground(TEXT_COLOR);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setEditable(false);
        questionArea.setOpaque(false);
        questionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        
        JScrollPane questionScroll = new JScrollPane(questionArea);
        questionScroll.setBorder(BorderFactory.createEmptyBorder());
        questionScroll.setMaximumSize(new Dimension(800, 150));
        centerPanel.add(questionScroll);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Answer Options
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        optionsPanel.setOpaque(false);
        optionsPanel.setMaximumSize(new Dimension(800, 400));
        
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createAnswerButton("Option " + (i+1));
            final int index = i;
            optionButtons[i].addActionListener(e -> checkAnswer(index));
            optionsPanel.add(optionButtons[i]);
        }
        centerPanel.add(optionsPanel);
        
        scrollPane.setViewportView(centerPanel);
        quizPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton generateButton = createSuccessButton("Generate Question");
        generateButton.setForeground(Color.BLACK);
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        generateButton.setPreferredSize(new Dimension(250, 50));
        generateButton.addActionListener(e -> generateQuestion());
        bottomPanel.add(generateButton);
        
        quizPanel.add(bottomPanel, BorderLayout.SOUTH);
        cardPanel.add(quizPanel, "quiz");
    }

    private static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR, Color.WHITE);
    }
    
    private static JButton createSecondaryButton(String text) {
        return createStyledButton(text, BUTTON_BG, TEXT_COLOR);
    }
    
    private static JButton createSuccessButton(String text) {
        return createStyledButton(text, CORRECT_COLOR, Color.WHITE);
    }
    
    private static JButton createDangerButton(String text) {
        return createStyledButton(text, WRONG_COLOR, Color.WHITE);
    }
    
    private static JButton createAnswerButton(String text) {
        JButton button = new JButton("<html><div style='width:300px;padding:10px;color:black;text-align:left'>" + text + "</div></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(BUTTON_BG);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEADING);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_BG.darker());
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BG);
            }
        });
        
        return button;
    }
    
    private static JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(250, 35));
    }

    private static void resetQuestionDisplay() {
        questionArea.setText("");
        for (JButton button : optionButtons) {
            button.setText("Option " + (Arrays.asList(optionButtons).indexOf(button) + 1));
            button.setBackground(BUTTON_BG);
            button.setVisible(false);
            button.setEnabled(true);
        }
    }

    private static void login() {
        String username = authUsernameField.getText().trim();
        String password = new String(authPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showErrorDialog("Username and password cannot be empty");
            return;
        }
        
        if (users.containsKey(username)) {
            User user = users.get(username);
            if (user.password.equals(password)) {
                currentUser = user;
                currentDifficulty = 1;
                updateCoinsDisplay();
                updateWelcomeMessage();
                cardLayout.show(cardPanel, "main");
                authUsernameField.setText("");
                authPasswordField.setText("");
                return;
            }
        }
        
        showErrorDialog("Invalid username or password");
    }

    private static void register() {
        String username = authUsernameField.getText().trim();
        String password = new String(authPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showErrorDialog("Username and password cannot be empty");
            return;
        }
        
        if (users.containsKey(username)) {
            showErrorDialog("Username already exists");
            return;
        }
        
        User newUser = new User(username, password);
        users.put(username, newUser);
        currentUser = newUser;
        currentDifficulty = 1;
        updateCoinsDisplay();
        updateWelcomeMessage();
        cardLayout.show(cardPanel, "main");
        authUsernameField.setText("");
        authPasswordField.setText("");
        
        saveUsers();
        showSuccessDialog("Registration successful!");
    }

    private static void generateQuestion() {
        String topic = topicField.getText().trim();
        if (topic.isEmpty()) {
            showErrorDialog("Please enter a topic to study");
            return;
        }
    
        new Thread(() -> {
            try {
                // Add delay between requests to prevent rate limiting
                Thread.sleep(1000); // 1 second delay
                
                // Build the API URL properly
                String apiUrl = TRIVIA_API + "&encode=url3986";
                if (!topic.equalsIgnoreCase("any")) {
                    try {
                        apiUrl += "&query=" + URLEncoder.encode(topic, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        SwingUtilities.invokeLater(() -> {
                            showErrorDialog("Error encoding topic");
                        });
                        return;
                    }
                }
    
                // Try the request up to 3 times with delays
                int retries = 0;
                int maxRetries = 3;
                JSONObject json = null;
                
                while (retries < maxRetries) {
                    try {
                        String response = callAPI(apiUrl);
                        json = new JSONObject(response);
                        
                        // Check if we got valid results
                        if (json.getInt("response_code") == 0 && json.getJSONArray("results").length() > 0) {
                            break;
                        }
                    } catch (IOException e) {
                        if (retries == maxRetries - 1) {
                            SwingUtilities.invokeLater(() -> {
                                showErrorDialog("Failed to get questions after multiple attempts. Please try again later.");
                            });
                            return;
                        }
                    }
                    
                    retries++;
                    if (retries < maxRetries) {
                        Thread.sleep(2000); // Wait 2 seconds before retrying
                    }
                }
    
                // If we still have no valid results, try with category
                if (json == null || json.getInt("response_code") != 0 || json.getJSONArray("results").length() == 0) {
                    int categoryId = getCategoryIdForTopic(topic);
                    if (categoryId > 0) {
                        apiUrl = TRIVIA_API + "&category=" + categoryId;
                        try {
                            String response = callAPI(apiUrl);
                            json = new JSONObject(response);
                        } catch (IOException e) {
                            SwingUtilities.invokeLater(() -> {
                                showErrorDialog("Error getting questions: " + e.getMessage());
                            });
                            return;
                        }
                    }
                }
    
                // If still no questions, show error
                if (json == null || json.getInt("response_code") != 0 || json.getJSONArray("results").length() == 0) {
                    SwingUtilities.invokeLater(() -> {
                        showErrorDialog("No questions found about " + topic + ". Try a different topic.");
                    });
                    return;
                }
    
                JSONObject questionData = json.getJSONArray("results").getJSONObject(0);
                
                final String question = decodeHtml(questionData.getString("question"));
                final String correctAnswer = decodeHtml(questionData.getString("correct_answer"));
                JSONArray incorrectAnswers = questionData.getJSONArray("incorrect_answers");
                
                List<String> allAnswers = new ArrayList<>();
                allAnswers.add(correctAnswer);
                for (int i = 0; i < incorrectAnswers.length(); i++) {
                    allAnswers.add(decodeHtml(incorrectAnswers.getString(i)));
                }
                Collections.shuffle(allAnswers);
                
                final int finalCorrectAnswerIndex = allAnswers.indexOf(correctAnswer);
                
                SwingUtilities.invokeLater(() -> {
                    questionArea.setText(question);
                    for (int i = 0; i < 4; i++) {
                        optionButtons[i].setText("<html><div style='width:300px;padding:10px;color:black;text-align:left'>" + 
                                               allAnswers.get(i) + "</div></html>");
                        optionButtons[i].setBackground(BUTTON_BG);
                        optionButtons[i].setVisible(true);
                        optionButtons[i].setEnabled(true);
                    }
                    correctAnswerIndex = finalCorrectAnswerIndex;
                    difficultyLabel.setText("Difficulty: " + currentDifficulty);
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showErrorDialog("Error getting question: " + e.getMessage());
                });
            }
        }).start();
    }

    private static int getCategoryIdForTopic(String topic) {
        // Map common topics to OpenTDB category IDs
        Map<String, Integer> topicCategories = new HashMap<>();
        topicCategories.put("history", 23);
        topicCategories.put("science", 17);
        topicCategories.put("math", 19);
        topicCategories.put("computer", 18);
        topicCategories.put("sports", 21);
        topicCategories.put("geography", 22);
        topicCategories.put("art", 25);
        topicCategories.put("movie", 11);
        topicCategories.put("music", 12);
        topicCategories.put("literature", 10);
        topicCategories.put("english", 10);
        topicCategories.put("biology", 17);
        topicCategories.put("chemistry", 17);
        topicCategories.put("physics", 17);
        topicCategories.put("programming", 18);
        topicCategories.put("books", 10);
        topicCategories.put("novel", 10);
        topicCategories.put("author", 10);
        
        String lowerTopic = topic.toLowerCase();
        for (Map.Entry<String, Integer> entry : topicCategories.entrySet()) {
            if (lowerTopic.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return -1;
    }

    private static void checkAnswer(int selectedIndex) {
        if (selectedIndex == correctAnswerIndex) {
            int coinsEarned = currentDifficulty * 10;
            currentUser.coins += coinsEarned;
            currentDifficulty = Math.min(5, currentDifficulty + 1);
            
            optionButtons[selectedIndex].setBackground(CORRECT_COLOR);
            optionButtons[selectedIndex].setForeground(Color.WHITE);
            showSuccessDialog("Correct! You earned " + coinsEarned + " coins");
        } else {
            currentDifficulty = Math.max(1, currentDifficulty - 1);
            
            optionButtons[selectedIndex].setBackground(WRONG_COLOR);
            optionButtons[selectedIndex].setForeground(Color.WHITE);
            optionButtons[correctAnswerIndex].setBackground(CORRECT_COLOR);
            optionButtons[correctAnswerIndex].setForeground(Color.WHITE);
            showErrorDialog("Incorrect! The right answer was option " + (correctAnswerIndex + 1));
        }
        
        for (JButton button : optionButtons) {
            button.setEnabled(false);
        }
        
        difficultyLabel.setText("Difficulty: " + currentDifficulty);
        updateCoinsDisplay();
        saveUsers();
    }

    private static void updateCoinsDisplay() {
        if (currentUser != null) {
            coinsLabel.setText("Coins: " + currentUser.coins);
        }
    }

    private static void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.username + "!");
        }
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(frame, 
            "<html><div style='width:300px;padding:10px'>" + message + "</div></html>", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    private static void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(frame, 
            "<html><div style='width:300px;padding:10px'>" + message + "</div></html>", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private static String callAPI(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private static String decodeHtml(String html) {
        try {
            String decoded = URLDecoder.decode(html, "UTF-8");
            return decoded.replace("&quot;", "\"")
                         .replace("&amp;", "&")
                         .replace("&lt;", "<")
                         .replace("&gt;", ">")
                         .replace("&#039;", "'")
                         .replace("&rsquo;", "'")
                         .replace("&ldquo;", "\"")
                         .replace("&rdquo;", "\"");
        } catch (UnsupportedEncodingException e) {
            return html;
        }
    }

    private static void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    int coins = Integer.parseInt(parts[2]);
                    users.put(username, new User(username, password, coins));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    private static void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : users.values()) {
                bw.write(user.username + "," + user.password + "," + user.coins);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    static class User {
        String username;
        String password;
        int coins;
        
        User(String username, String password) {
            this(username, password, 0);
        }
        
        User(String username, String password, int coins) {
            this.username = username;
            this.password = password;
            this.coins = coins;
        }
    }
}