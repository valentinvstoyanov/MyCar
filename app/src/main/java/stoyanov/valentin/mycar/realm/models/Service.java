package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Service extends RealmObject {
    @PrimaryKey
    private String id;
    private ServiceType type;
    private Note note;
    private Action action;
    private RealmNotification notification;
    private long targetOdometer;

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

    public RealmNotification getNotification() {
        return notification;
    }

    public void setNotification(RealmNotification notification) {
        this.notification = notification;
    }

    public long getTargetOdometer() {
        return targetOdometer;
    }

    public void setTargetOdometer(long targetOdometer) {
        this.targetOdometer = targetOdometer;
    }
}
