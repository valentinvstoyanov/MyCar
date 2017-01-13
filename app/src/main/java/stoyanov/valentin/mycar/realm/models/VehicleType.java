package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class VehicleType extends RealmObject{
    @PrimaryKey
    private String id;
    private String name;
    private String drawableName;

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

    public String getDrawableName() {
        return drawableName;
    }

    public void setDrawableName(String drawableName) {
        this.drawableName = drawableName;
    }
}
