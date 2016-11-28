package stoyanov.valentin.mycar.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Brand extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
}
