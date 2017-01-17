package stoyanov.valentin.mycar.realm.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Reminder extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private Date date;
    private Date notifDate;
    private Note note;
    private long odometer;
}
