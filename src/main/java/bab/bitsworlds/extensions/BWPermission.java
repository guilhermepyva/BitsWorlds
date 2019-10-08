package bab.bitsworlds.extensions;

public enum BWPermission {
    MAINCMD("maincmd"),
    MAINCMD_CONFIG("maincmd.config"),
    MAINCMD_HELP("maincmd.help"),
    MAINCMD_WORLD_LIST("maincmd.world.list"),
    MAINCMD_WORLD_CREATE("maincmd.world.create"),
    MAINCMD_WORLD_INTERACT("maincmd.world.interact"),

    LOGS_SEE("logs.see"),

    LOGS_NOTE_ADD("logs.note.add"),
    LOGS_NOTE_MODIFY("logs.note.modify"),


    UNLOAD("interaction.unload.save"),
    UNLOAD_WITHOUT_SAVE("interaction.unload.nosave"),
    LOAD("interaction.load"),
    GAMERULE("interaction.gamerule"),
    SAVE("interaction.save"),
    DUPLICATE("interaction.duplicate"),
    BACKUP("interaction.backup")


    ;

    BWPermission(String string) {
        this.string = "bitsworlds." +  string;
    }

    public String string;
}