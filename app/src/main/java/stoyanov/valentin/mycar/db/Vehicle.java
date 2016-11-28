package stoyanov.valentin.mycar.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Vehicle extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private int horsePower;
    private int cubicCentimeter;
    private String registrationNumber;
    private String vinNumber;
    private Date manifactureDate;
    private String type;
    private int color;
}
