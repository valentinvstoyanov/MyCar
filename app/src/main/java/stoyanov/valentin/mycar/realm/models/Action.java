package stoyanov.valentin.mycar.realm.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Action extends RealmObject {
    @PrimaryKey
    private long id;
    private Date date;
    private long price;
    private long odometer;
}
