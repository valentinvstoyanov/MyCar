package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;

public class RealmSettings extends RealmObject {

    private int distanceInAdvance;
    private String lengthUnit;
    private String currencyUnit;

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
