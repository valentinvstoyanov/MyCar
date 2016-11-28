package stoyanov.valentin.mycar.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Price extends RealmObject {
    @PrimaryKey
    private int id;
    private long value;
}
