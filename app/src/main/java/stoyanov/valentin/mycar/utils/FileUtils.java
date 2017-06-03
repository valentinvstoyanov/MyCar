package stoyanov.valentin.mycar.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtils {

    public static final String DIRNAME = "MyCarApp";

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static Uri getFileUri(File file) {
        return Uri.fromFile(file);
    }

    public static File createFile(Context context, File dir, String filename, String content) {
        File file = new File(dir, filename);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.write(content);
            writer.close();
            outputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Couldn't create file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static File createAppDir(Context context) {
        File appDir = new File(Environment.getExternalStorageDirectory(), DIRNAME);
        if (appDir.exists() || appDir.mkdir()) {
            return appDir;
        }
        Toast.makeText(context, "Couldn't create app directory", Toast.LENGTH_SHORT).show();
        return null;
    }

    public static String getContentFromInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public static String[] getParsedCsv(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter(",");
        ArrayList<String> values = new ArrayList<>();
        while (scanner.hasNext()) {
            String value = scanner.next();
            values.add(value);
        }
        scanner.close();
        return values.toArray(new String[0]);
    }
}
