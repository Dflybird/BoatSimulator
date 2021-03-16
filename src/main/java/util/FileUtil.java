package util;

import java.io.*;

public class FileUtil {


    private FileUtil() {}

    public static byte[] readFileBytes(File file) throws IOException {
        if (file.exists()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            int c;

            while ((c = inputStream.read()) != -1) {
                outputStream.write(c);
            }
            inputStream.close();
            return outputStream.toByteArray();
        } else {
            return new byte[0];
        }
    }

    public static void writeFile(File file, byte[] bytes) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStream outputStream = new BufferedOutputStream(fileOutputStream);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        fileOutputStream.close();
    }

    public static String readFileCharacter (File file) throws IOException {
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            return builder.toString();
        }
        return null;
    }

    public static void writeFile(File file, String str) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(str);
        bufferedWriter.flush();
        bufferedWriter.close();
        fileOutputStream.close();
    }
}
