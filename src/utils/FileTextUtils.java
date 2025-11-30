package utils;

import collections.MyList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileTextUtils {

    public static MyList<String> readLines(String path) throws IOException {
        java.util.List<String> javaLines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        MyList<String> lines = new MyList<>(javaLines.size() > 0 ? javaLines.size() : 10);
        for (String line : javaLines) {
            lines.add(line);
        }
        return lines;
    }

    public static void writeLines(MyList<String> lines, String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            sb.append(lines.get(i));
            if (i < lines.size() - 1) {
                sb.append(System.lineSeparator());
            }
        }
        Files.write(Paths.get(path),
                sb.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static String readAll(String path) throws IOException {
        byte[] all = Files.readAllBytes(Paths.get(path));
        return new String(all, StandardCharsets.UTF_8);
    }

    public static void writeAll(String content, String path) throws IOException {
        Files.write(Paths.get(path),
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}