package utils;

public class ErrorHandler {
    public static void printError(String message, Exception e) {
        System.out.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}
