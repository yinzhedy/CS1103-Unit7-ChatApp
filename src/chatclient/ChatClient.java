package chatclient;

import utils.ConsoleUtils;
import utils.NetworkUtils;
import utils.SocketUtils;
import java.io.*;
import java.net.*;

public class ChatClient {
    private String hostname;
    private int port;
    private Socket socket;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Connected to the chat server");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
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
            // Temporary variables to hold resources until they are fully initialized
            InputStream tempInput = null;
            OutputStream tempOutput = null;
            try {
                tempInput = socket.getInputStream();
                tempOutput = socket.getOutputStream();
                reader = new BufferedReader(new InputStreamReader(tempInput));
                writer = new PrintWriter(new OutputStreamWriter(tempOutput), true);
    
                // Prevent the finally block from closing these streams if initialization is successful
                tempInput = null;
                tempOutput = null;
            } catch (IOException ex) {
                System.out.println("Error getting input stream: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                // Ensure resources are closed if they were not assigned successfully
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
                    if (serverMessage == null) {  // Check if the server has closed the connection
                        break;
                    }
                    if ("HEARTBEAT".equals(serverMessage)) {
                        heartbeatHandler.handleHeartbeat();
                        continue;  // Skip printing the heartbeat message
                    }
                    System.out.println("\n" + serverMessage);
                } catch (IOException ex) {
                    System.out.println("Error reading from server: " + ex.getMessage());
                    break;  // Exit the loop on error
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
            String userName = ConsoleUtils.readLine("\nEnter your name: ");
            NetworkUtils.sendMessage(socket, "NEW_USER " + userName);

            String text;
            do {
                text = ConsoleUtils.readLine("[" + userName + "]: ");
                NetworkUtils.sendMessage(socket, "MESSAGE " + text);
            } while (!text.equals("bye"));

            SocketUtils.closeSocket(socket);
        }
    }
}
