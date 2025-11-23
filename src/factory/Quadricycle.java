package factory;

import java.util.Date;

public class Quadricycle extends AbstractVehicle {
    public Quadricycle(long id,
                       String model,
                       double enginePower,
                       int maxSpeed,
                       Date releaseDate,
                       double price) {
        super(id, VehicleType.QUADRICYCLE, model, enginePower, maxSpeed, releaseDate, price);
    }
}