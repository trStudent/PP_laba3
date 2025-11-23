package factory;

import java.util.Date;

public class Moped extends AbstractVehicle {
    public Moped(long id,
                 String model,
                 double enginePower,
                 int maxSpeed,
                 Date releaseDate,
                 double price) {
        super(id, VehicleType.MOPED, model, enginePower, maxSpeed, releaseDate, price);
    }
}