import chatserver.ChatServer;
import chatclient.ChatClient;

public class App {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8989;

        // Start the server in a new thread
        Thread serverThread = new Thread(() -> {
            ChatServer server = new ChatServer(port);
            server.start();
        });
        serverThread.start();

        // Wait a moment to ensure the server starts
        try {
            System.err.println("Waiting for server to start..");
            Thread.sleep(5000);  // Increase wait time if needed
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Failed to wait for the server to start");
        }

        // Start the client
        ChatClient client = new ChatClient(hostname, port);
        client.execute();

        try {
            serverThread.join(); // Keep the main thread running until the server thread terminates
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}