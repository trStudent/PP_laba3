package utils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class FileTextUtils {

    public static List<String> readLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
    }

    public static void writeLines(List<String> lines, String path) throws IOException {
        Files.write(Paths.get(path), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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