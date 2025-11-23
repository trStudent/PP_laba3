package factory;

import java.util.Date;

public class VehicleFactory {

    public static Motorcycle createMotorcycle(long id,
                                              String model,
                                              double enginePower,
                                              int maxSpeed,
                                              Date releaseDate,
                                              double price) {
        return new Motorcycle(id, model, enginePower, maxSpeed, releaseDate, price);
    }

    public static Quadricycle createQuadricycle(long id,
                                                String model,
                                                double enginePower,
                                                int maxSpeed,
                                                Date releaseDate,
                                                double price) {
        return new Quadricycle(id, model, enginePower, maxSpeed, releaseDate, price);
    }

    public static Moped createMoped(long id,
                                    String model,
                                    double enginePower,
                                    int maxSpeed,
                                    Date releaseDate,
                                    double price) {
        return new Moped(id, model, enginePower, maxSpeed, releaseDate, price);
    }

    public static Scooter createScooter(long id,
                                        String model,
                                        double enginePower,
                                        int maxSpeed,
                                        Date releaseDate,
                                        double price) {
        return new Scooter(id, model, enginePower, maxSpeed, releaseDate, price);
    }
}