package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Refueling extends RealmObject {
    @PrimaryKey
    private int id;
    private Action action;
    private long fuelPrice;
}
