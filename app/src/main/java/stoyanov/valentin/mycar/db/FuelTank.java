package stoyanov.valentin.mycar.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FuelTank extends RealmObject {
    @PrimaryKey
    private int id;
    private int capacity;
    private double consumption;
}
