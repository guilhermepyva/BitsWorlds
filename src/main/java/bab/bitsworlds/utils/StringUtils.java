package bab.bitsworlds.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    /*Can be useful some day...
    public static String replace(String origin, String from, String to) {
        if(!origin.contains(" ")) return origin.replace(from, to);

        StringBuilder result = new StringBuilder();
        String[] x = origin.split(" ");
        for (String x1 : x) {
            if (x1.equals(from)) result.append(" " + x1.replace(from, to));
            else result.append(x1);
        }

        return result.toString();
    }*/

    public static List<String> getDescriptionFromMessage(String message, String prefixColors, String prefix) {
        List<String> list = new ArrayList<>();

        boolean putPrefix = true;
        for (String string : message.split("\n")) {
            if (putPrefix) {
                putPrefix = false;
                list.add(prefix + prefixColors + string);
                continue;
            }

            list.add(prefixColors + string);
        }

        return list;
    }
}
