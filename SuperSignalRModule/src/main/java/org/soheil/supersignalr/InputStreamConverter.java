package org.soheil.supersignalr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamConverter {

    private static char RETURN_SYMBOL = '\n';

    public static String convert(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
            total.append(RETURN_SYMBOL);
        }

        return total.toString();
    }
}