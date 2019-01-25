package bab.bitsworlds.logger;

import java.util.UUID;

public class LogRecorder {
    public RecorderType type;
    public UUID uuid;

    public LogRecorder(RecorderType type) {
        this.type = type;
    }

    public LogRecorder(UUID uuid) {
        if (uuid == null)
            this.type = RecorderType.CONSOLE;
        else
            this.type = RecorderType.PLAYER;

        this.uuid = uuid;
    }

    public LogRecorder(RecorderType type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    public enum RecorderType {
        PLAYER,
        CONSOLE,
        SYSTEM
    }
}