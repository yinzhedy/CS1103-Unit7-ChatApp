package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkUtils {
    public static void sendMessage(Socket socket, String message) {
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
        } catch (IOException e) {
            ErrorHandler.printError("Error sending message", e);
        }
    }
}
