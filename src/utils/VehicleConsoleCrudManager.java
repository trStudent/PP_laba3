package utils;

import collections.MyList;
import collections.MyMap;
import factory.AbstractVehicle;
import factory.VehicleFactory;
import factory.VehicleType;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class VehicleConsoleCrudManager {

    private final VehicleDataProcessor dataProcessor = new VehicleDataProcessor();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Scanner scanner = new Scanner(System.in);

    private final String dataFile;
    private final String logFile;

    private final MyList<AbstractVehicle> vehicles;
    private final MyMap<Long, AbstractVehicle> vehicleIndex = new MyMap<>();

    public VehicleConsoleCrudManager(String dataFile, String logFile) throws IOException {
        this.dataFile = dataFile;
        this.logFile = logFile;
        this.vehicles = dataProcessor.readVehicles(dataFile, logFile);
        rebuildIndex();
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    addVehicle();
                    break;
                case "2":
                    updateVehicle();
                    break;
                case "3":
                    deleteVehicle();
                    break;
                case "4":
                    listVehicles();
                    break;
                case "0":
                    exit = true;
                    break;
                default:
                    System.out.println("Неизвестная команда. Повторите ввод.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n=== VEHICLE CUD MENU ===");
        System.out.println("1) Добавить транспорт");
        System.out.println("2) Обновить транспорт");
        System.out.println("3) Удалить транспорт");
        System.out.println("4) Показать все (через итераторы)");
        System.out.println("0) Выход");
        System.out.print("Ваш выбор: ");
    }

    private void addVehicle() {
        AbstractVehicle vehicle = promptVehicle(null);
        vehicles.add(vehicle);
        vehicleIndex.put(vehicle.getId(), vehicle);
        persist();
        System.out.println("Транспорт добавлен.");
    }

    private void updateVehicle() {
        long id = promptLong("ID для обновления: ");
        if (!vehicleIndex.containsKey(id)) {
            System.out.println("Транспорт с таким ID не найден.");
            return;
        }
        AbstractVehicle updated = promptVehicle(vehicleIndex.get(id));
        replaceVehicle(id, updated);
        System.out.println("Транспорт обновлён.");
    }

    private void deleteVehicle() {
        long id = promptLong("ID для удаления: ");
        if (!vehicleIndex.containsKey(id)) {
            System.out.println("Транспорт с таким ID не найден.");
            return;
        }
        AbstractVehicle target = vehicleIndex.get(id);
        // убрать из списка
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId() == id) {
                vehicles.remove(i);
                break;
            }
        }
        vehicleIndex.remove(id);
        persist();
        System.out.println("Транспорт удалён.");
    }

    private void listVehicles() {
        System.out.println("\n--- Содержимое MyList (iterator) ---");
        var listIterator = vehicles.iterator();
        while (listIterator.hasNext()) {
            AbstractVehicle v = listIterator.next();
            System.out.println(describe(v));
        }

        System.out.println("\n--- Содержимое MyMap (iterator по значениям) ---");
        var valueIterator = vehicleIndex.values().iterator();
        while (valueIterator.hasNext()) {
            AbstractVehicle v = valueIterator.next();
            System.out.println(describe(v));
        }
    }

    private AbstractVehicle promptVehicle(AbstractVehicle existing) {
        VehicleType type = promptType(existing);
        long id = existing == null ? promptLong("ID: ") : existing.getId();
        String model = promptString("Модель", existing == null ? "" : existing.getModel());
        double enginePower = promptDouble("Мощность двигателя", existing == null ? 0.0 : existing.getEnginePower());
        int maxSpeed = (int) promptLong("Максимальная скорость", existing == null ? 0 : existing.getMaxSpeed());
        Date releaseDate = promptDate("Дата выпуска (yyyy-MM-dd)", existing == null ? null : existing.getReleaseDate());
        double price = promptDouble("Цена", existing == null ? 0.0 : existing.getPrice());

        switch (type) {
            case MOTORCYCLE:
                return VehicleFactory.createMotorcycle(id, model, enginePower, maxSpeed, releaseDate, price);
            case QUADRICYCLE:
                return VehicleFactory.createQuadricycle(id, model, enginePower, maxSpeed, releaseDate, price);
            case MOPED:
                return VehicleFactory.createMoped(id, model, enginePower, maxSpeed, releaseDate, price);
            case SCOOTER:
                return VehicleFactory.createScooter(id, model, enginePower, maxSpeed, releaseDate, price);
            default:
                throw new IllegalStateException("Неизвестный тип " + type);
        }
    }

    private void replaceVehicle(long id, AbstractVehicle updated) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId() == id) {
                vehicles.set(i, updated);
                break;
            }
        }
        vehicleIndex.put(id, updated);
        persist();
    }

    private void rebuildIndex() {
        vehicleIndex.clear();
        for (int i = 0; i < vehicles.size(); i++) {
            AbstractVehicle v = vehicles.get(i);
            vehicleIndex.put(v.getId(), v);
        }
    }

    private void persist() {
        try {
            FileTextUtils.writeLines(serializeVehicles(), dataFile);
            // при необходимости можно заново прогнать парсер, чтобы обновить лог
            dataProcessor.readVehicles(dataFile, logFile);
        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    private MyList<String> serializeVehicles() {
        MyList<String> lines = new MyList<>(vehicles.size());
        for (int i = 0; i < vehicles.size(); i++) {
            lines.add(serializeVehicle(vehicles.get(i)));
        }
        return lines;
    }

    private String serializeVehicle(AbstractVehicle v) {
        String date = v.getReleaseDate() == null ? "" : dateFormat.format(v.getReleaseDate());
        return String.join(",",
                v.getType().name(),
                String.valueOf(v.getId()),
                v.getModel(),
                String.valueOf(v.getEnginePower()),
                String.valueOf(v.getMaxSpeed()),
                date,
                String.valueOf(v.getPrice())
        );
    }

    private VehicleType promptType(AbstractVehicle existing) {
        while (true) {
            System.out.print("Тип (MOTORCYCLE|QUADRICYCLE|MOPED|SCOOTER)"
                    + (existing == null ? "" : " [" + existing.getType() + "]")
                    + ": ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty() && existing != null) return existing.getType();
            try {
                return VehicleType.valueOf(raw.toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println("Неверный тип. Повторите.");
            }
        }
    }

    private String promptString(String label, String defaultValue) {
        System.out.print(label + (defaultValue.isEmpty() ? "" : " [" + defaultValue + "]") + ": ");
        String raw = scanner.nextLine();
        return raw.trim().isEmpty() ? defaultValue : raw.trim();
    }

    private long promptLong(String label) {
        return promptLong(label, 0L);
    }

    private long promptLong(String label, long defaultValue) {
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

    private double promptDouble(String label, double defaultValue) {
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

    private Date promptDate(String label, Date defaultValue) {
        while (true) {
            String def = defaultValue == null ? "" : dateFormat.format(defaultValue);
            System.out.print(label + (def.isEmpty() ? "" : " [" + def + "]") + ": ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) return defaultValue;
            try {
                return dateFormat.parse(raw);
            } catch (ParseException ex) {
                System.out.println("Формат даты yyyy-MM-dd. Попробуйте снова.");
            }
        }
    }

    private String describe(AbstractVehicle v) {
        return v.getType() + " #" + v.getId() + " " + v.getModel()
                + ", power=" + v.getEnginePower()
                + ", maxSpeed=" + v.getMaxSpeed()
                + ", date=" + (v.getReleaseDate() == null ? "null" : dateFormat.format(v.getReleaseDate()))
                + ", price=" + v.getPrice();
    }
}