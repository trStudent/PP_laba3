package factory;

import java.util.Date;
import java.text.SimpleDateFormat;

public abstract class AbstractVehicle {
    private long id;
    private VehicleType type;
    private String model;
    private double enginePower;
    private int maxSpeed;
    private Date releaseDate;
    private double price;

    public AbstractVehicle(long id,
                           VehicleType type,
                           String model,
                           double enginePower,
                           int maxSpeed,
                           Date releaseDate,
                           double price) {
        this.id = id;
        this.type = type;
        this.model = model;
        this.enginePower = enginePower;
        this.maxSpeed = maxSpeed;
        this.releaseDate = releaseDate;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(double enginePower) {
        this.enginePower = enginePower;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    protected String formatDate(Date d) {
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
               "id=" + id +
               ", type=" + type +
               ", model='" + model + '\'' +
               ", enginePower=" + enginePower +
               ", maxSpeed=" + maxSpeed +
               ", releaseDate=" + formatDate(releaseDate) +
               ", price=" + price +
               '}';
    }
}