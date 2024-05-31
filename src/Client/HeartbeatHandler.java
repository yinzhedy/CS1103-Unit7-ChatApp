package Client;

import java.io.PrintWriter;

public class HeartbeatHandler {
    private PrintWriter writer;

    public HeartbeatHandler(PrintWriter writer) {
        this.writer = writer;
    }

    public void handleHeartbeat() {
        if (writer != null) {
            writer.println("HEARTBEAT_RESPONSE");
            writer.flush();
        }
    }
}
