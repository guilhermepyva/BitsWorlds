package bab.bitsworlds.extensions;

public enum BWPermission {
    MAINCMD("bitsworlds.maincmd"),
    MAINCMD_CONFIG("bitsworlds.maincmd.config"),
    MAINCMD_HELP("bitsworlds.maincmd.help"),


    LOGS_SEE_ALL("bitsworlds.logs.see.all"),
    LOGS_SEE_GLOBAL("bitsworlds.logs.see.global"),
    LOGS_SEE_WORLD("bitsworlds.logs.see.world"),

    LOGS_DESCRIPTION_ADD("bitsworlds.logs.description.add"),
    LOGS_DESCRIPTION_MODIFY("bitsworlds.logs.description.modify")

    ;

    BWPermission(String string) {
        this.string = string;
    }

    public String string;
}