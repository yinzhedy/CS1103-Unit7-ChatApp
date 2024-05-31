package chatserver;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatManager {
    private PrintWriter writer;
    private static final long HEARTBEAT_INTERVAL = 5000; // 5 seconds

    public HeartbeatManager(PrintWriter writer) {
        this.writer = writer;
    }

    public void start() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        }, 0, HEARTBEAT_INTERVAL);
    }

    private void sendHeartbeat() {
        if (writer != null) {
            writer.println("HEARTBEAT");
            writer.flush();
        }
    }
}
