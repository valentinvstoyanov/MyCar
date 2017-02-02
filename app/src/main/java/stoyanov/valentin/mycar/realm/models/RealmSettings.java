package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmSettings extends RealmObject {

    @PrimaryKey
    private String id;
    private int distanceInAdvance;
    private String lengthUnit;
    private String currencyUnit;

    public RealmSettings() {}

    public RealmSettings(int distanceInAdvance, String lengthUnit, String currencyUnit) {
        this.distanceInAdvance = distanceInAdvance;
        this.lengthUnit = lengthUnit;
        this.currencyUnit = currencyUnit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDistanceInAdvance() {
        return distanceInAdvance;
    }

    public void setDistanceInAdvance(int distanceInAdvance) {
        this.distanceInAdvance = distanceInAdvance;
    }

    public String getLengthUnit() {
        return lengthUnit;
    }

    public void setLengthUnit(String lengthUnit) {
        this.lengthUnit = lengthUnit;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }
}
