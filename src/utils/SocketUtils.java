package utils;

import java.io.IOException;
import java.net.Socket;

public class SocketUtils {
    public static void closeSocket(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            ErrorHandler.printError("Error closing socket", e);
        }
    }
}
