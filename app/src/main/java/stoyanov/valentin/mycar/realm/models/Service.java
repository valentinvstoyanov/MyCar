package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Service extends RealmObject {
    @PrimaryKey
    private String id;
    private ServiceType type;
    private Note note;
    //private Location location;
    private Action action;
    //maybe new realmobject that is like mid-table between many-to-many
    //relationship which consists of RealmList<Action> and maybe the two ids
    //ServiceId and ServiceTypeId


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
