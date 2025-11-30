package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ConsoleMenu {
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat;
    private final Map<String, MenuItem> items = new LinkedHashMap<>();
    private final String prompt;

    public ConsoleMenu() {
        this(new Scanner(System.in), new SimpleDateFormat("yyyy-MM-dd"), "Ваш выбор: ");
    }

    public ConsoleMenu(Scanner scanner, SimpleDateFormat dateFormat, String prompt) {
        this.scanner = scanner;
        this.dateFormat = dateFormat;
        this.prompt = prompt;
    }

    public void addItem(String key, String text, Runnable action) {
        items.put(key, new MenuItem(key, text, action));
    }

    public void addItem(String key, String text, SupplierAction action) {
        items.put(key, new MenuItem(key, text, action));
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();
            MenuItem item = items.get(choice);
            if (item == null) {
                System.out.println("Неизвестная команда. Повторите ввод.");
                continue;
            }
            try {
                Object result = item.execute();
                if (item.isExit()) exit = true;
                if (result instanceof MenuResult) {
                    MenuResult mr = (MenuResult) result;
                    if (mr.isExit()) {
                        exit = true;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Ошибка при выполнении команды: " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }

    private void printMenu() {
        System.out.println();
        for (MenuItem it : items.values()) {
            System.out.printf("%s) %s%n", it.key, it.text);
        }
        System.out.print(prompt);
    }

    public String promptString(String label, String defaultValue) {
        System.out.print(label + (defaultValue == null || defaultValue.isEmpty() ? "" : " [" + defaultValue + "]") + ": ");
        String raw = scanner.nextLine();
        if (raw == null) return defaultValue;
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? defaultValue : trimmed;
    }

    public long promptLong(String label, long defaultValue) {
        while (true) {
            System.out.print(label + (defaultValue == 0 ? "" : " [" + defaultValue + "]") + ": ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty() && defaultValue != 0) return defaultValue;
            try {
                return Long.parseLong(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Введите целое число.");
            }
        }
    }

    public double promptDouble(String label, double defaultValue) {
        while (true) {
            System.out.print(label + " [" + defaultValue + "]: ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) return defaultValue;
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Введите число.");
            }
        }
    }

    public Date promptDate(String label, Date defaultValue) {
        while (true) {
            String def = defaultValue == null ? "" : dateFormat.format(defaultValue);
            System.out.print(label + (def.isEmpty() ? "" : " [" + def + "]") + ": ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) return defaultValue;
            try {
                return dateFormat.parse(raw);
            } catch (ParseException ex) {
                System.out.println("Формат даты " + dateFormat.toPattern() + ". Попробуйте снова.");
            }
        }
    }

    private static class MenuItem {
        final String key;
        final String text;
        final Runnable runnable;
        final SupplierAction supplierAction;
        final boolean exitFlag;

        MenuItem(String key, String text, Runnable action) {
            this.key = key;
            this.text = text;
            this.runnable = action;
            this.supplierAction = null;
            this.exitFlag = "exit".equalsIgnoreCase(key) || "0".equals(key);
        }

        MenuItem(String key, String text, SupplierAction action) {
            this.key = key;
            this.text = text;
            this.runnable = null;
            this.supplierAction = action;
            this.exitFlag = "exit".equalsIgnoreCase(key) || "0".equals(key);
        }

        Object execute() {
            if (runnable != null) {
                runnable.run();
                return null;
            } else if (supplierAction != null) {
                return supplierAction.get();
            }
            return null;
        }

        boolean isExit() {
            return exitFlag;
        }
    }

    @FunctionalInterface
    public interface SupplierAction {
        Object get();
    }

    public static class MenuResult {
        private final boolean exit;

        public MenuResult(boolean exit) {
            this.exit = exit;
        }

        public boolean isExit() {
            return exit;
        }
    }
}