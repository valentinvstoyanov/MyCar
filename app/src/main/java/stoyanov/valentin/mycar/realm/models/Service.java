package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Service extends RealmObject {
    @PrimaryKey
    private int id;
    private ServiceType type;
    private Note note;
    private Location location;
    private Action action;
    //maybe new realmobject that is like mid-table between many-to-many
    //relationship which consists of RealmList<Action> and maybe the two ids
    //ServiceId and ServiceTypeId
}
