package user;

import java.util.UUID;

public class User {
    private String name;
    private String userID;
    private static int userCount = 0;
    private boolean isActive;

    // Constructor
    public User(String name) {
        this.name = name;
        this.userID = UUID.randomUUID().toString();  // Generate unique ID
        this.isActive = true;  // Set the user as active upon creation
        incrementUserCount();
    }

    // Synchronized method to increment user count
    private synchronized void incrementUserCount() {
        userCount++;
    }

    // Synchronized method to decrement user count
    private synchronized void decrementUserCount() {
        userCount--;
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
        }
        this.isActive = active;
    }

    // Getter for userCount
    public static synchronized int getUserCount() {
        return userCount;
    }

    // Destructor
    @Override
    protected void finalize() throws Throwable {
        try {
            if (isActive) {
                decrementUserCount();
            }
        } finally {
            super.finalize();
        }
    }
}