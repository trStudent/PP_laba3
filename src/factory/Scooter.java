package factory;

import java.util.Date;

public class Scooter extends AbstractVehicle {
    public Scooter(long id,
                   String model,
                   double enginePower,
                   int maxSpeed,
                   Date releaseDate,
                   double price) {
        super(id, VehicleType.SCOOTER, model, enginePower, maxSpeed, releaseDate, price);
    }
}