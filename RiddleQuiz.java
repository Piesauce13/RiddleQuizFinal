    import java.util.*;
    import java.io.*;

    class RiddleQuiz {
        private static final Scanner scanner = new Scanner(System.in);
        private static final Map<String, String> users = new HashMap<>();
        private static final Map<String, Integer> leaderboard = new HashMap<>();
        private static final Map<String, Long> completionTimes = new HashMap<>();
        private static final String USERS_FILE = "users.txt";
        private static final String LEADERBOARD_FILE = "leaderboard.txt";
        private static final String QUESTIONS_FILE = "questions.txt";
        private static final List<Question> questions = new ArrayList<>();

        public static void main(String[] args) {
            loadUsers();
            loadLeaderboard();
            loadQuestions();
            showWelcomeScreen();
        }

        // Starting screen
        private static void showWelcomeScreen() {
            System.out.println("---------------------- Welcome to Riddle Quiz Game! ---------------------- ");
            System.out.println(" \"Test your wits with challenging riddles across different difficulty levels.\" ");

            while (true) {
                System.out.print("\nDo you have an account yet?\nEnter (Yes/No): ");
                String response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("yes") || response.equals("y")) {
                    String username = login();
                    if (username != null) {
                        showMainMenu(username);
                    }
//                    break;
                } else if (response.equals("no") || response.equals("n")) {
                    signUp();
//                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'Yes' or 'No'.");
                }
            }
        }

        // Main menu
        private static void showMainMenu(String username) {
            while (true) {
                System.out.println("\n\n                                Main Menu                                ");
                System.out.println("1. Play Quiz");
                System.out.println("2. View Leaderboard");
                System.out.println("3. Edit Profile");
                System.out.println("4. Logout");
                System.out.print("Choose an option: ");
    
                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine();
    
                    switch (choice) {
                        case 1:
                            playQuiz(username);
                            break;
                        case 2:
                            showLeaderboard();
                            break;
                        case 3:
                            editProfile(username);
                            break;
                        case 4:
                            System.out.println("Logging out... Goodbye, " + username + "!");
                            showWelcomeScreen();
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid number (1-4).");
                    scanner.nextLine();
                }
            }
        }
    
        // Edit profile menu
        private static void editProfile(String currentUsername) {
            System.out.println("\n\n                                Edit Profile                                ");
            System.out.println("1. Change Username");
            System.out.println("2. Change Password");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose an option: ");
    
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
    
                switch (choice) {
                    case 1:
                        changeUsername(currentUsername);
                        break;
                    case 2:
                        changePassword(currentUsername);
                        break;
                    case 3:
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number (1-3).");
                scanner.nextLine();
            }
        }

        // Edit profile method
        private static void changeUsername(String currentUsername) {
            System.out.print("\nEnter new username: ");
            String newUsername = scanner.nextLine();
    
            if (users.containsKey(newUsername)) {
                System.out.println("Username already exists. Please choose another one.");
                return;
            }
    
            // Update all records with the new username
            String password = users.get(currentUsername);
            int score = leaderboard.getOrDefault(currentUsername, 0);
            long time = completionTimes.getOrDefault(currentUsername, Long.MAX_VALUE);
    
            // Remove old entries
            users.remove(currentUsername);
            leaderboard.remove(currentUsername);
            completionTimes.remove(currentUsername);
    
            // Add new entries
            users.put(newUsername, password);
            leaderboard.put(newUsername, score);
            completionTimes.put(newUsername, time);
    
            saveUsers();
            saveLeaderboard();
    
            System.out.println("Username changed successfully to: " + newUsername);
            
            // Return to welcome screen with new username
            System.out.println("Please login again with your new username.");
            showWelcomeScreen();
        }
    
        // Change password method
        private static void changePassword(String username) {
            Console console = System.console();
            if (console == null) {
                System.out.println("No console available. Running in IDE?");
            }

            while (true) {
                char[] currentPasswordArray = console != null ? console.readPassword("\nEnter current password: ") : scanner.nextLine().toCharArray();
                String currentPassword = new String(currentPasswordArray);

                if (!users.get(username).equals(currentPassword)) {
                    System.out.println("Incorrect current password. Try again.");
                    continue;
                }

                while (true) {
                    char[] newPasswordArray = console != null ? console.readPassword("Enter new password: ") : scanner.nextLine().toCharArray();
                    String newPassword = new String(newPasswordArray);

                    if (newPassword.equals(currentPassword)) {
                        System.out.println("New password cannot be the same as the current password. Try again.");
                        continue;
                    }

                    char[] confirmPasswordArray = console != null ? console.readPassword("Confirm new password: ") : scanner.nextLine().toCharArray();
                    String confirmPassword = new String(confirmPasswordArray);

                    if (!newPassword.equals(confirmPassword)) {
                        System.out.println("Passwords do not match. Try again.");
                        continue;
                    }

                    users.put(username, newPassword);
                    saveUsers();
                    System.out.println("Password changed successfully!");
                    return;
                }
            }
        }

        // Signup method
        private static void signUp() {
            Console console = System.console();
            if (console == null) {
                System.out.println("No console available. Running in IDE?");
            }

            System.out.print("Enter a username: ");
            String username = scanner.nextLine();

            if (users.containsKey(username)) {
                System.out.println("Username already exists. Try another one.");
                return;
            }

            char[] passwordArray = console != null ? console.readPassword("Enter a password: ") : scanner.nextLine().toCharArray();
            String password = new String(passwordArray);

            users.put(username, password);
            leaderboard.put(username, 0);
            completionTimes.put(username, Long.MAX_VALUE);
            saveUsers();
            saveLeaderboard();
            System.out.println("Sign-up successful! You can now log in.");
        }

        // Login method
        private static String login() {
            Console console = System.console();
            if (console == null) {
                System.out.println("No console available. Running in IDE?");
            }

            while (true) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();

                if (!users.containsKey(username)) {
                    System.out.println("Username not found. Try again.");
                    continue;
                }

                char[] passwordArray = console != null ? console.readPassword("Enter password: ") : scanner.nextLine().toCharArray();
                String password = new String(passwordArray);

                if (users.get(username).equals(password)) {
                    System.out.println("Login successful! Welcome, " + username + "!");
                    return username;
                } else {
                    System.out.println("Invalid password. Try again.");
                }
            }
        }

        // Quiz method
        private static void playQuiz(String username) {
            if (questions.isEmpty()) {
                System.out.println("No questions available.");
                return;
            }
        
            // Group questions by difficulty
            Map<String, List<Question>> questionsByDifficulty = new HashMap<>();
            questionsByDifficulty.put("Easy", new ArrayList<>());
            questionsByDifficulty.put("Medium", new ArrayList<>());
            questionsByDifficulty.put("Hard", new ArrayList<>());
            
            for (Question q : questions) {
                questionsByDifficulty.get(q.getDifficulty()).add(q);
            }
        
            long startTime = System.currentTimeMillis();
            int totalScore = 0;
            int totalQuestions = 0;
        
            for (String difficulty : new String[]{"Easy", "Medium", "Hard"}) {
                List<Question> difficultyQuestions = questionsByDifficulty.get(difficulty);
                if (difficultyQuestions.isEmpty()) continue;
                
                System.out.println("\n\nStarting " + difficulty + " difficulty questions!");
                int difficultyScore = 0;
                
                // Randomly select 3 questions
                Collections.shuffle(difficultyQuestions);
                List<Question> selectedQuestions = difficultyQuestions.subList(0, Math.min(3, difficultyQuestions.size()));

                int questionIndex = 1;
                for (Question q : selectedQuestions) {
                    System.out.println("\n" + questionIndex++ + ". (" + q.getDifficulty() + ") " + q.getText());
                    char optionChar = 'A';
                    for (String option : q.getOptions()) {
                        System.out.println(optionChar + ". " + option);
                        optionChar++;
                    }
                    
                    char answer;
                    while (true) {
                        System.out.print("Your answer (A, B, C) or Q to quit: ");
                        String input = scanner.next().trim().toUpperCase();
                        scanner.nextLine();
                        
                        if (input.equals("Q")) {
                            System.out.println("Exiting the quiz... Your progress will be lost.");
                            return;
                        }
                        
                        if (input.length() == 1 && (input.equals("A") || input.equals("B") || input.equals("C"))) {
                            answer = input.charAt(0);
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter A, B, or C.");
                        }
                    }
                    
                    if (answer == q.getCorrectAnswer()) {
                        System.out.println("Correct!");
                        difficultyScore++;
                    } else {
                        System.out.println("Wrong! The correct answer was " + q.getCorrectAnswer());
                    }
                }
                
                totalScore += difficultyScore;
                totalQuestions += selectedQuestions.size();
                
                System.out.println("\nCongratulations on completing the " + difficulty + " difficulty!");
                System.out.println("Your score for " + difficulty + " questions: " + difficultyScore + "/" + selectedQuestions.size());
            }
        
            long endTime = System.currentTimeMillis();
            long completionTime = (endTime - startTime) / 1000;
            System.out.println("\n\nQuiz Over! Your total score: " + totalScore + "/" + totalQuestions);
            System.out.println("Total time taken: " + completionTime + " seconds");

            int previousBestScore = leaderboard.getOrDefault(username, 0);
            long previousBestTime = completionTimes.getOrDefault(username, Long.MAX_VALUE);

            if (totalScore > previousBestScore || (totalScore == previousBestScore && completionTime < previousBestTime)) {
                leaderboard.put(username, totalScore);
                completionTimes.put(username, completionTime);
                saveLeaderboard();
                System.out.println("Congratulations! You earned your new high score!");
            } else {
                System.out.println("You didn't break your record. Better luck next time!");
            }
        }
        

        private static void showLeaderboard() {
            while (true) {
                System.out.println("\n\n                                Leaderboard                              ");
                leaderboard.entrySet().stream()
                    .sorted((a, b) -> {
                        int scoreCompare = b.getValue().compareTo(a.getValue());
                        if (scoreCompare != 0) return scoreCompare;
                        return Long.compare(
                            completionTimes.getOrDefault(a.getKey(), Long.MAX_VALUE),
                            completionTimes.getOrDefault(b.getKey(), Long.MAX_VALUE)
                        );
                    })
                    .forEach(entry -> {
                        String username = entry.getKey();
                        int score = entry.getValue();
                        long time = completionTimes.getOrDefault(username, Long.MAX_VALUE);
                        System.out.println(username + " - " + score + " points - Fastest time: " + (time == Long.MAX_VALUE ? "N/A" : time + " sec"));
                    });
        
                System.out.println("\n1. Back to Main Menu");
                System.out.print("Choose an option: ");
                
                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice == 1) {
                        return;
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid number (1).");
                    scanner.nextLine();
                }
            }
        }

        private static void loadUsers() {
            try (Scanner fileScanner = new Scanner(new File(USERS_FILE))) {
                while (fileScanner.hasNextLine()) {
                    String[] data = fileScanner.nextLine().split(",");
                    if (data.length >= 2) {
                        users.put(data[0], data[1]);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("No previous user data found. Starting fresh.");
            }
        }

        private static void loadLeaderboard() {
            try (Scanner fileScanner = new Scanner(new File(LEADERBOARD_FILE))) {
                while (fileScanner.hasNextLine()) {
                    String[] data = fileScanner.nextLine().split(",");
                    if (data.length >= 2) {
                        try {
                            leaderboard.put(data[0], Integer.parseInt(data[1]));
                            if (data.length >= 3) {
                                try {
                                    completionTimes.put(data[0], Long.parseLong(data[2]));
                                } catch (NumberFormatException e) {
                                    completionTimes.put(data[0], Long.MAX_VALUE);
                                }
                            } else {
                                completionTimes.put(data[0], Long.MAX_VALUE);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format in leaderboard file.");
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("No previous leaderboard data found. Starting fresh.");
            }
        }

        private static void loadQuestions() {
            try (Scanner fileScanner = new Scanner(new File(QUESTIONS_FILE))) {
                int questionNumber = 1;
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    // Split on commas that are not inside quotes
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    
                    // Trim each part and remove surrounding quotes if present
                    for (int i = 0; i < parts.length; i++) {
                        parts[i] = parts[i].trim().replaceAll("^\"|\"$", "");
                    }
                    
                    if (parts.length >= 6) {  // Need at least difficulty, text, 3 options, and answer
                        String difficulty = parts[0];
                        String text = parts[1];
                        String[] options = Arrays.copyOfRange(parts, 2, parts.length - 1);
                        char correctAnswer = parts[parts.length - 1].trim().charAt(0);
                        questions.add(new Question(questionNumber++, difficulty, text, options, correctAnswer));
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("No questions found. Please add questions to " + QUESTIONS_FILE);
            }
        }

        private static void saveUsers() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
                users.forEach((user, pass) -> writer.println(user + "," + pass));
            } catch (IOException e) {
                System.out.println("Error saving users.");
            }
        }

        private static void saveLeaderboard() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
                leaderboard.forEach((user, score) -> {
                    Long time = completionTimes.get(user);
                    writer.println(user + "," + score + "," + (time == Long.MAX_VALUE ? "" : time));
                });
            } catch (IOException e) {
                System.out.println("Error saving leaderboard.");
            }
        }
    }

    class Question {
        private final int number;
        private final String difficulty;
        private final String text;
        private final String[] options;
        private final char correctAnswer;

        public Question(int number, String difficulty, String text, String[] options, char correctAnswer) {
            this.number = number;
            this.difficulty = difficulty;
            this.text = text;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public int getNumber() { return number; }
        public String getDifficulty() { return difficulty; }
        public String getText() { return text; }
        public String[] getOptions() { return options; }
        public char getCorrectAnswer() { return correctAnswer; }
    }

