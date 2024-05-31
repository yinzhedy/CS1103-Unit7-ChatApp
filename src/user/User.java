package user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private String name;
    private String userID;
    private static int userCount = 0;
    private boolean isActive;
    private static final Set<User> activeUsers = new HashSet<>();

    // Constructor
    public User(String name) {
        this.name = name;
        this.userID = UUID.randomUUID().toString();  // Generate unique ID
        this.isActive = true;  // Set the user as active upon creation
        incrementUserCount();
        addUser(this);  // Add to active users set
    }

    // Synchronized method to increment user count
    private synchronized void incrementUserCount() {
        userCount++;
    }

    // Synchronized method to decrement user count
    private synchronized void decrementUserCount() {
        userCount--;
    }

    // Synchronized method to add user to active users set
    private synchronized void addUser(User user) {
        activeUsers.add(user);
    }

    // Synchronized method to remove user from active users set
    private synchronized void removeUser(User user) {
        activeUsers.remove(user);
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for userID
    public String getUserID() {
        return userID;
    }

    // Getter for isActive
    public boolean isActive() {
        return isActive;
    }

    // Setter for isActive
    public void setActive(boolean active) {
        if (!active) {
            decrementUserCount();
            removeUser(this);
        }
        this.isActive = active;
    }

    // Getter for userCount
    public static synchronized int getUserCount() {
        return userCount;
    }

    // Getter for activeUsers
    public static synchronized Set<User> getActiveUsers() {
        return new HashSet<>(activeUsers);
    }

    // Destructor
    @Override
    protected void finalize() throws Throwable {
        try {
            if (isActive) {
                decrementUserCount();
                removeUser(this);
            }
        } finally {
            super.finalize();
        }
    }
}
