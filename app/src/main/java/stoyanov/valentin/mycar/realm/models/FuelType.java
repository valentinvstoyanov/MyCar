package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FuelType extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String unit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
