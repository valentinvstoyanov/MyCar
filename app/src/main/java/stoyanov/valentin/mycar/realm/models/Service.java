package stoyanov.valentin.mycar.realm.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Service extends RealmObject {

    @PrimaryKey
    private String id;
    private ServiceType type;
    private Date date;
    private long odometer;
    private long price;
    private boolean shouldNotify;
    private DateNotification dateNotification;
    private long targetOdometer;
    private boolean isOdometerTriggered;
    private String note;

    //private Action action;
    //private Note note;
    //private OdometerNotification odometerNotification;


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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getOdometer() {
        return odometer;
    }

    public void setOdometer(long odometer) {
        this.odometer = odometer;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean shouldNotify() {
        return shouldNotify;
    }

    public void setShouldNotify(boolean shouldNotify) {
        this.shouldNotify = shouldNotify;
    }

    public DateNotification getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(DateNotification dateNotification) {
        this.dateNotification = dateNotification;
    }

    public long getTargetOdometer() {
        return targetOdometer;
    }

    public void setTargetOdometer(long targetOdometer) {
        this.targetOdometer = targetOdometer;
    }

    public boolean isOdometerTriggered() {
        return isOdometerTriggered;
    }

    public void setOdometerTriggered(boolean odometerTriggered) {
        isOdometerTriggered = odometerTriggered;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
