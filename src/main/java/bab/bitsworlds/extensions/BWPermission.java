package bab.bitsworlds.extensions;

public enum BWPermission {
    MAINCMD("maincmd"),
    MAINCMD_CONFIG("maincmd.config"),
    MAINCMD_HELP("maincmd.help"),
    MAINCMD_WORLD_LIST("maincmd.world.list"),

    LOGS_SEE("logs.see"),

    LOGS_NOTE_ADD("logs.note.add"),
    LOGS_NOTE_MODIFY("logs.note.modify")

    ;

    BWPermission(String string) {
        this.string = "bitsworlds." +  string;
    }

    public String string;
}