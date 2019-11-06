package bab.bitsworlds.extensions;

public enum BWPermission {
    MAINCMD("maincmd"),
    MAINCMD_CONFIG("maincmd.config"),
    MAINCMD_HELP("maincmd.help"),
    MAINCMD_WORLD_LIST("maincmd.world.list"),
    MAINCMD_WORLD_CREATE("maincmd.world.create"),
    MAINCMD_WORLD_INTERACT("maincmd.world.interact"),
    MAINCMD_BACKUP_LIST("maincmd.backup.list"),

    LOGS_SEE("logs.see"),

    LOGS_NOTE_ADD("logs.note.add"),
    LOGS_NOTE_MODIFY("logs.note.modify"),


    UNLOAD("interaction.unload.save"),
    UNLOAD_WITHOUT_SAVE("interaction.unload.nosave"),
    LOAD("interaction.load"),
    SEE_GAMERULES("interaction.gamerule.see"),
    SET_GAMERULE("interaction.gamerule.set"),
    SAVE("interaction.save"),
    DUPLICATE("interaction.duplicate"),
    BACKUP("interaction.backup"),
    TELEPORT("interaction.teleport.self"),
    TELEPORT_OTHER_PLAYER("interaction.teleport.other"),
    TELEPORT_ALL_PLAYERS("interaction.teleport.allplayers"),
    SEE_TIME("interaction.time.see"),
    SET_TIME("interaction.time.set"),
    SEE_DIFFICULTY("interaction.difficulty.see"),
    SET_DIFFICULTY("interaction.difficulty.set"),
    DELETE_WORLD("interaction.deleteworld"),
    RENAME_WORLD("interaction.renameworld"),

    RECOVER_BACKUP("backup.recover"),
    DELETE_BACKUP("backup.delete")


    ;

    BWPermission(String string) {
        this.string = "bitsworlds." +  string;
    }

    public String string;
}