package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FuelTank extends RealmObject {
    @PrimaryKey
    private int id;
    private int capacity;
    private double consumption;
    private boolean isMain;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(boolean main) {
        isMain = main;
    }
}
