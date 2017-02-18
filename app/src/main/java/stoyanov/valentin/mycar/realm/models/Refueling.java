package stoyanov.valentin.mycar.realm.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Refueling extends RealmObject {
    @PrimaryKey
    private String id;
    private FuelTank fuelTank;
    private long fuelPrice;
    private int quantity;
    private Date date;
    private long odometer;
    private long price;
    private String note;
    //private String fuelTankId;
    //private Action action;
    // private Note note;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FuelTank getFuelTank() {
        return fuelTank;
    }

    public void setFuelTank(FuelTank fuelTank) {
        this.fuelTank = fuelTank;
    }

    public long getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(long fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getOdometer() {
        return odometer;
    }

    public void setOdometer(long odometer) {
        this.odometer = odometer;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
