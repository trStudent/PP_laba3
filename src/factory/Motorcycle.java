package factory;

import java.util.Date;

public class Motorcycle extends AbstractVehicle {
    public Motorcycle(long id,
                      String model,
                      double enginePower,
                      int maxSpeed,
                      Date releaseDate,
                      double price) {
        super(id, VehicleType.MOTORCYCLE, model, enginePower, maxSpeed, releaseDate, price);
    }
}