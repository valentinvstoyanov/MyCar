package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Refueling extends RealmObject {
    @PrimaryKey
    private String id;
    private String fuelTankId;
    private Action action;
    private long fuelPrice;
    private int quantity;
    private Note note;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFuelTankId() {
        return fuelTankId;
    }

    public void setFuelTankId(String fuelTankId) {
        this.fuelTankId = fuelTankId;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
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

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
