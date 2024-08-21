package com.mycompany.testingprojectnb15;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author ericw
 * 
 */
public class RunProject {
    private static Scanner scan;
    private static final List<String> option = Arrays.asList("Run Weather", "Exit");
    
    public static void main(String[] args) {
        int input = 0;
        while (true) {
            genMenu();
            scan = new Scanner(System.in);
            boolean validInput = false;
            while (!validInput) {
                System.out.print(">> ");
                try {
                    input = scan.nextInt();
                    scan.nextLine(); // Consume the newline character
                    if (input >= 1 && input <= option.size()) {
                        validInput = true;
                    } else {
                        System.out.println();
                        System.out.println("Invalid choice. Please enter a number between 1 and " + option.size() + ".");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scan.next(); // Consume the invalid input
                }
            }
            String menuChosen = option.get(input - 1);

            if ("Exit".equals(menuChosen)) {
                System.out.println("You choose: " + menuChosen);
                System.out.println("Goodbye!!!");
                break; // Exit the loop and end the program
            }

            System.out.println("You choose: " + menuChosen);
            menuChosen = convertMenu(menuChosen);
            
            try {
                Class<?> clazz = Class.forName("com.mycompany.testingprojectnb15." + menuChosen);
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method method = clazz.getMethod("exec");
                method.invoke(instance);
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + menuChosen);
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                System.out.println("Method 'execute' not found in class: " + menuChosen);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            input = 0;
        }
        scan.close();
        System.exit(0);
    }
    
    private static void genMenu() {
        String title = "TEST PROJECT MENU";
        int width = 30;
        int height = 5;
        int titleStart = (width - title.length()) / 2;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == height / 2 && j == titleStart) {
                    System.out.print(title);
                    j += title.length() - 1;
                } else {
                    System.out.print("*");
                }
            }
            System.out.println("");
        }

        System.out.println("");

        for (int i = 0; i < option.size(); i++) {
            System.out.println(i + 1 + ". " + option.get(i));
        }
    }
    
    private static String convertMenu(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(result.length() == 0 ? Character.toLowerCase(c) : c);
            }
        }

        return result.toString();
    }
}
