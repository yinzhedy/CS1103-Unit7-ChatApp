# Chat Application

## Overview

This is a simple multi-user chat application implemented in Java. It allows multiple users to connect to a central server, send messages, and receive messages from other users in real time. The application includes a client-side component and a server-side component, with each component handling its respective responsibilities.

## Features

- Multi-user chat functionality
- Unique user ID generation using UUID
- User activity tracking
- Heartbeat mechanism to keep the connection alive
- Simple text-based user interface
- Clean and modular code structure with utility packages

## Getting Started

### Prerequisites

- Java Development Kit (JDK) installed on your machine

### Starting the Application

1. Open a terminal and navigate to the project directory.
2. Run the App.java file - this will automatically run the server for the chat on host: localhost and port: 8989

## Class Descriptions

### Client Package
#### ChatClient.java
- Manages the client-side operations, including connecting to the server, handling incoming messages, and sending messages.

#### HeartbeatHandler.java
- Handles sending heartbeat responses to keep the connection alive.

## User Package
#### User.java
- Represents a user in the chat application, including their name, unique ID, active status, and user count tracking.

### Server Package
#### ChatServer.java
- Manages server-side operations such as accepting client connections, broadcasting messages, and handling disconnections.

#### HeartbeatManager.java
- Sends periodic heartbeat messages to clients to ensure they are still connected.

### Utils Package
#### ConsoleUtils.java
- Provides utilities for reading user input from the console.

#### ErrorHandler.java
- Provides a centralized way to handle and display errors.

#### NetworkUtils.java
- Provides utilities for sending messages over a network.

#### SocketUtils.java
- Provides utilities for closing sockets.

### App.java
- Main entry point for running both the server and client within the same application.

## Communication Protocol
- The communication between the client and server uses a simple text-based protocol. Each message sent by a client is prefixed by the user's name, allowing the server to broadcast it to all other connected clients. Heartbeat messages are used to maintain the connection and are handled internally without being displayed to the users.

## Known Issues
- Currently, there is no persistent storage of messages or user information.
- The application assumes a reliable network connection and does not handle network failures gracefully.

## Future Improvements
- Add persistent storage for chat messages and user information.
- Improve error handling for network failures.
- Implement a more sophisticated user authentication system.