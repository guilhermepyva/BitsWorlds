package bab.bitsworlds.logger;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.UUID;

public class Log {
    public int id;
    public LogAction action;
    public Object data;
    public LogRecorder recorder;
    public String note;
    public LogRecorder noteRecorder;
    public Timestamp time;

    public UUID world;
    public String worldName;

    public Log(int id, LogAction action, Object data, LogRecorder recorder, String note, LogRecorder noteRecorder, Timestamp time, @Nullable UUID world, String worldName) {
        this.id = id;
        this.action = action;
        this.data = data;
        this.recorder = recorder;
        this.note = note;
        this.noteRecorder = noteRecorder;
        this.time = time;
        this.world = world;
        this.worldName = worldName;
    }

    public boolean isGlobal() {
        return worldName == null;
    }
}