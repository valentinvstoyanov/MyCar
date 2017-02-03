package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OdometerNotification extends RealmObject {

    @PrimaryKey
    private String id;
    private boolean isTriggered;
    private long targetOdometer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void setTriggered(boolean triggered) {
        isTriggered = triggered;
    }

    public long getTargetOdometer() {
        return targetOdometer;
    }

    public void setTargetOdometer(long targetOdometer) {
        this.targetOdometer = targetOdometer;
    }
}
