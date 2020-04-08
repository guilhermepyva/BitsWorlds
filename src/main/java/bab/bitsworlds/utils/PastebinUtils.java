package bab.bitsworlds.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class PastebinUtils {
    public static URL getURL(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }

        if (!url.getHost().startsWith("pastebin.com") && !url.getHost().startsWith("www.pastebin.com"))
            return null;

        return url;
    }

    public static String getStringFromPaste(URL pastebinUrl) {
        String[] paths = pastebinUrl.getPath().split("/");

        if (paths.length < 2 || paths.length > 3)
            return null;

        if (paths.length == 3) {
            if (!paths[1].equals("raw"))
                return null;

            try {
                return readStringFromScanner(new Scanner(pastebinUrl.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        else
            try {
                return readStringFromScanner(new Scanner(new URL("https://pastebin.com/raw/" + paths[1]).openStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
    }

    private static String readStringFromScanner(Scanner scanner) {
        StringBuilder sb = new StringBuilder();

        while (scanner.hasNext())
            sb.append(scanner.nextLine());

        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            System.out.println(getStringFromPaste(new URL("https://pastebin.com/XDp3LLiZ")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}