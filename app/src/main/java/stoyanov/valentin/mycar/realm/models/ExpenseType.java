package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ExpenseType extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
}
