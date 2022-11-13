package Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static String read(String fileName) {
        StringBuilder data = new StringBuilder();
        try (java.io.FileReader fileReader = new java.io.FileReader(fileName)) {
            int c;
            while ((c = fileReader.read()) != -1) data.append(c);
        } catch (FileNotFoundException e) {
            data.append("File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            data.append("Common error.");
            e.printStackTrace();
        }
        return data.toString();
    }

    public static void write(String fileName, String data) {
        try(FileOutputStream fos=new FileOutputStream(fileName)) {
            byte[] buffer = data.getBytes();
            fos.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void append(String fileName, String data) {
        StringBuilder builder = new StringBuilder();
        try (java.io.FileReader fileReader = new java.io.FileReader(fileName)) {
            int c;
            while ((c = fileReader.read()) != -1) builder.append(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(FileOutputStream fos=new FileOutputStream(fileName)) {
            byte[] buffer = builder.toString().getBytes();
            fos.write(buffer);
            buffer = data.getBytes();
            fos.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
