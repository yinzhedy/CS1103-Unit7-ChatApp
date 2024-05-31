package utils;

import java.io.Console;

public class ConsoleUtils {
    private static Console console = System.console();

    public static String readLine(String prompt) {
        return console.readLine(prompt);
    }
}
