package Client;

import user.User;  // Import User class
import utils.ConsoleUtils;
import utils.NetworkUtils;
import utils.SocketUtils;
import java.io.*;
import java.net.*;

public class ChatClient {
    private String hostname;
    private int port;
    private Socket socket;
    private User user;  // Add User field

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Connected to the chat server");

            // Collect username and create User object
            String userName = ConsoleUtils.readLine("\nEnter your name: ");
            user = new User(userName);
            System.out.println("Your userID: " + user.getUserID());
            System.out.println("Type /exit in the chat at any time to disconnect.");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public User getUser() {
        return user;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: java ChatClient <host> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }

    class ReadThread extends Thread {
        private BufferedReader reader;
        private PrintWriter writer;
        private ChatClient client;

        public ReadThread(Socket socket, ChatClient client) {
            this.client = client;
            InputStream tempInput = null;
            OutputStream tempOutput = null;
            try {
                tempInput = socket.getInputStream();
                tempOutput = socket.getOutputStream();
                reader = new BufferedReader(new InputStreamReader(tempInput));
                writer = new PrintWriter(new OutputStreamWriter(tempOutput), true);
                tempInput = null;
                tempOutput = null;
            } catch (IOException ex) {
                System.out.println("Error getting input stream: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                if (tempInput != null) {
                    try { tempInput.close(); } catch (IOException e) { e.printStackTrace(); }
                }
                if (tempOutput != null) {
                    try { tempOutput.close(); } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }

        public void run() {
            HeartbeatHandler heartbeatHandler = new HeartbeatHandler(writer);
            while (true) {
                try {
                    String serverMessage = reader.readLine();
                    if (serverMessage == null) {
                        break;
                    }
                    if ("HEARTBEAT".equals(serverMessage)) {
                        heartbeatHandler.handleHeartbeat();
                        continue;  // Skip printing the heartbeat message
                    }
                    System.out.println("\n" + serverMessage);
                } catch (IOException ex) {
                    System.out.println("Error reading from server: " + ex.getMessage());
                    break;
                }
            }
        }
    }

    class WriteThread extends Thread {
        private PrintWriter writer;
        private Socket socket;
        private ChatClient client;

        public WriteThread(Socket socket, ChatClient client) {
            this.socket = socket;
            this.client = client;

            try {
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
            } catch (IOException ex) {
                System.out.println("Error getting output stream: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        public void run() {
            String text;
            do {
                text = ConsoleUtils.readLine("[" + client.getUser().getName() + "]: ");
                NetworkUtils.sendMessage(socket, text);  // Send message directly
            } while (!text.equals("/exit"));

            client.getUser().setActive(false);
            SocketUtils.closeSocket(socket);
        }
    }
}
