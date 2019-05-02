package bab.bitsworlds.extensions;

public enum BWPermission {
    MAINCMD("maincmd"),
    MAINCMD_CONFIG("maincmd.config"),
    MAINCMD_HELP("maincmd.help"),


    LOGS_SEE("logs.see"),

    LOGS_DESCRIPTION_ADD("logs.description.add"),
    LOGS_DESCRIPTION_MODIFY("logs.description.modify")

    ;

    BWPermission(String string) {
        this.string = "bitsworlds." +  string;
    }

    public String string;
}