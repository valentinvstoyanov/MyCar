package stoyanov.valentin.mycar.realm.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Expense extends RealmObject {
    @PrimaryKey
    private int id;
    private ExpenseType type;
    private Action action;
    private Location location;
    private Note note;
    private Date notificationDate;
}
