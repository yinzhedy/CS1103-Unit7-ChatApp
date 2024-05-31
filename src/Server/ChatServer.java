package Server;

import user.User;  // Import User class
import utils.NetworkUtils;
import utils.SocketUtils;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private int port;
    private ExecutorService executorService;
    private Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public ChatServer(int port) {
        this.port = port;
        this.executorService = Executors.newCachedThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                ClientHandler newUser = new ClientHandler(socket, this);
                clientHandlers.add(newUser);
                executorService.submit(newUser);
            }
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
        } finally {
            stopServer();
        }
    }

    private void stopServer() {
        System.out.println("Shutting down the server...");
        try {
            clientHandlers.forEach(ClientHandler::closeConnection);
        } finally {
            executorService.shutdownNow();
        }
    }

    void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler aUser : clientHandlers) {
            if (aUser != excludeUser) {
                NetworkUtils.sendMessage(aUser.socket, message);
            }
        }
    }

    void removeUser(ClientHandler user) {
        user.getUser().setActive(false);
        clientHandlers.remove(user);
        System.out.println("The user disconnected");
        broadcast("A user has left the chat.", null);
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ChatServer server;
        private BufferedReader reader;
        private PrintWriter writer;
        private User user;  // Add User field
        private HeartbeatManager heartbeatManager;

        public ClientHandler(Socket socket, ChatServer server) {
            this.socket = socket;
            this.server = server;
            BufferedReader tempReader = null;
            PrintWriter tempWriter = null;
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                tempReader = new BufferedReader(new InputStreamReader(input));
                tempWriter = new PrintWriter(new OutputStreamWriter(output), true);

                reader = tempReader;
                writer = tempWriter;
                tempReader = null;
                tempWriter = null;

                heartbeatManager = new HeartbeatManager(writer);
                String userName = reader.readLine();
                user = new User(userName);
                broadcast("User " + user.getName() + " has joined the chat!", this);
            } catch (IOException ex) {
                System.out.println("Error setting up streams: " + ex.getMessage());
                closeConnection();
            } finally {
                if (tempReader != null) {
                    try { tempReader.close(); } catch (IOException e) { e.printStackTrace(); }
                }
                if (tempWriter != null) {
                    tempWriter.close();
                }
            }
        }

        public void run() {
            try {
                heartbeatManager.start();
                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    if ("HEARTBEAT_RESPONSE".equals(clientMessage)) {
                        continue;  // Ignore the heartbeat response messages
                    }
                    // Broadcast user message in a cleaner format
                    server.broadcast(user.getName() + ": " + clientMessage, this);
                }
            } catch (IOException ex) {
                System.out.println("Error reading from client: " + ex.getMessage());
            } finally {
                closeConnection();
            }
        }

        public User getUser() {
            return user;
        }

        private void closeConnection() {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
            server.removeUser(this);
        }
    }
}