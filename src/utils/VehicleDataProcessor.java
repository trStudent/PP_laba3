package utils;


import factory.AbstractVehicle;
import factory.VehicleFactory;
import factory.VehicleType;
import utils.FileTextUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class VehicleDataProcessor {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public VehicleDataProcessor() {
        dateFormat.setLenient(false);
    }

    public List<AbstractVehicle> readVehicles(String inputFile, String logFile) throws IOException {
        List<String> lines = FileTextUtils.readLines(inputFile);
        List<AbstractVehicle> vehicles = new ArrayList<>();
        List<String> log = new ArrayList<>();

        int lineNo = 0;
        for (String rawLine : lines) {
            lineNo++;
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",", -1);
            if (parts.length != 7) {
                log.add(formatLog(lineNo, "Expected 7 fields, got " + parts.length + " → SKIPPED: " + line));
                continue;
            }

            // 1) TYPE
            VehicleType type;
            try {
                type = VehicleType.valueOf(parts[0].trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                log.add(formatLog(lineNo, "Invalid type '" + parts[0] + "' → SKIPPED line"));
                continue;
            }

            // 2) ID
            long id = parseLong(parts[1], 0L, lineNo, "id", log);

            // 3) MODEL
            String model = parts[2].trim();
            if (model.isEmpty()) {
                log.add(formatLog(lineNo, "Empty model → set to empty string"));
            }

            // 4) ENGINE POWER
            double enginePower = parseDouble(parts[3], 0.0, lineNo, "enginePower", log);

            // 5) MAX SPEED
            int maxSpeed = (int) parseLong(parts[4], 0L, lineNo, "maxSpeed", log);

            // 6) RELEASE DATE
            Date releaseDate = null;
            String ds = parts[5].trim();
            if (!ds.isEmpty()) {
                try {
                    releaseDate = dateFormat.parse(ds);
                } catch (ParseException ex) {
                    log.add(formatLog(lineNo, "Invalid date '" + ds + "' → set to null"));
                }
            } else {
                log.add(formatLog(lineNo, "Empty date → set to null"));
            }

            double price = parseDouble(parts[6], 0.0, lineNo, "price", log);

            AbstractVehicle v;
            switch (type) {
                case MOTORCYCLE:
                    v = VehicleFactory.createMotorcycle(id, model, enginePower, maxSpeed, releaseDate, price);
                    break;
                case QUADRICYCLE:
                    v = VehicleFactory.createQuadricycle(id, model, enginePower, maxSpeed, releaseDate, price);
                    break;
                case MOPED:
                    v = VehicleFactory.createMoped(id, model, enginePower, maxSpeed, releaseDate, price);
                    break;
                case SCOOTER:
                    v = VehicleFactory.createScooter(id, model, enginePower, maxSpeed, releaseDate, price);
                    break;
                default:
                    continue;
            }
            vehicles.add(v);
        }

        FileTextUtils.writeLines(log, logFile);
        return vehicles;
    }

    private String formatLog(int lineNo, String msg) {
        return "Line " + lineNo + ": " + msg;
    }

    private long parseLong(String raw, long defaultValue, int lineNo, String field, List<String> log) {
        String cleaned = raw.trim().replaceAll("[^0-9\\-]", "");
        if (cleaned.isEmpty() || cleaned.equals("-")) {
            log.add(formatLog(lineNo, "Invalid " + field + " '" + raw + "' → set to " + defaultValue));
            return defaultValue;
        }
        try {
            return Long.parseLong(cleaned);
        } catch (NumberFormatException ex) {
            log.add(formatLog(lineNo, "Parse error " + field + " '" + raw + "' → set to " + defaultValue));
            return defaultValue;
        }
    }

    private double parseDouble(String raw, double defaultValue, int lineNo, String field, List<String> log) {
        String cleaned = raw.trim().replaceAll("[^0-9\\.\\-]", "");
        if (cleaned.isEmpty() || cleaned.equals("-") || cleaned.equals(".")) {
            log.add(formatLog(lineNo, "Invalid " + field + " '" + raw + "' → set to " + defaultValue));
            return defaultValue;
        }
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException ex) {
            log.add(formatLog(lineNo, "Parse error " + field + " '" + raw + "' → set to " + defaultValue));
            return defaultValue;
        }
    }
}