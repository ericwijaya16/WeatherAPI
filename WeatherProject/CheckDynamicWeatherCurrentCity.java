/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.testingprojectnb15.WeatherProject;

import com.mycompany.testingprojectnb15.WeatherProject.utils.WeatherUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ericw
 */
public class CheckDynamicWeatherCurrentCity implements MenuInterface{
    private static HashMap<String, String> param = new HashMap<>();
    private static List<String> VALID_VARIABLES = new ArrayList<>();
    
    @Override
    public void execute() {
        try {
            VALID_VARIABLES = WeatherUtils.loadValidVariables();
            List<String> variables = readUserInput();
            param.put("hourly", String.join(",", variables));
            WeatherUtils weather = new WeatherUtils("dynamic", param);
            weather.fetchWeatherData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private static List<String> readUserInput() throws Exception {
        List<String> variables = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        System.out.println("Enter hourly weather variables (type 'Done' to finish, but at least one valid variable is required)");
        System.out.println("Please type 'Help' to list all valid hourly weather variables");
        System.out.print(">> ");
        // Ensure the user enters at least one valid variable
        boolean first = true;
        while (true) {
            if (!first) {
                System.out.print(">> ");
            }
            input = reader.readLine();

            if (first) {
                first = false;
            }

            input = normalizeInput(input);

            if (input.equalsIgnoreCase("done")) {
                if (variables.isEmpty()) {
                    System.out.println("Please enter at least one valid weather variable before typing 'Done'.");
                } else {
                    break;
                }
            } else if (input.equalsIgnoreCase("help")) {
                System.out.println("Here's all available weather variables: ");
                printValidVariables();
            } else if (VALID_VARIABLES.contains(input)) {
                variables.add(input);
            } else if (input.equals("")) {
                System.out.println("Enter Detected. Please type 'Done' to finish the input or");
                System.out.println("'Help' for listing all available hourly weather variables");
            } else {
                String suggestion = suggestSimilarVariable(input);
                System.out.println("Invalid variable. Did you mean: '" + suggestion + "'?");
            }
        }

        return variables;
    }

    private static String normalizeInput(String input) {
        return input.trim().toLowerCase().replace(" ", "_");
    }

    private static void printValidVariables() {
        for (String variable : VALID_VARIABLES) {
            System.out.println(capitalize(variable.replace("_", " ")));
        }
    }

    private static String capitalize(String input) {
        String[] words = input.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
        }
        return capitalized.toString().trim();
    }

    private static String suggestSimilarVariable(String input) {
        String normalizedInput = input.replace("_", " ").toLowerCase();
        String suggestion = "";
        int minDistance = Integer.MAX_VALUE;

        for (String variable : VALID_VARIABLES) {
            String normalizedVariable = variable.replace("_", " ").toLowerCase();
            int distance = levenshteinDistance(normalizedInput, normalizedVariable);
            if (distance < minDistance) {
                minDistance = distance;
                suggestion = variable;
            }
        }

        // Additional prefix match for better suggestions
        for (String variable : VALID_VARIABLES) {
            String normalizedVariable = variable.replace("_", " ").toLowerCase();
            if (normalizedVariable.startsWith(normalizedInput) || normalizedVariable.contains(normalizedInput)) {
                suggestion = variable;
                break;
            }
        }

        return capitalize(suggestion.replace("_", " "));
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }

        return dp[a.length()][b.length()];
    }
    
}
