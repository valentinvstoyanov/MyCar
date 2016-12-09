package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Plate extends RealmObject {
    @PrimaryKey
    private int id;
    private String registration;
    private String vin;
}
