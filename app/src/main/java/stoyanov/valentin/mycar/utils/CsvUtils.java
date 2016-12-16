package stoyanov.valentin.mycar.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class CsvUtils {
    public static String[] getParsedCsv(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        Scanner scanner = new Scanner(inputStreamReader).useDelimiter(",");
        ArrayList<String> values = new ArrayList<>();
        while (scanner.hasNext()) {
            String value = scanner.next();
            values.add(value);
        }
        scanner.close();
        return values.toArray(new String[0]);
    }
}
