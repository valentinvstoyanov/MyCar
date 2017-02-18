package stoyanov.valentin.mycar.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Color extends RealmObject {

    @PrimaryKey
    private String id;
    private int color;
    private int relevantDarkColor;
    private int textIconsColor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getRelevantDarkColor() {
        return relevantDarkColor;
    }

    public void setRelevantDarkColor(int relevantDarkColor) {
        this.relevantDarkColor = relevantDarkColor;
    }

    public int getTextIconsColor() {
        return textIconsColor;
    }

    public void setTextIconsColor(int textIconsColor) {
        this.textIconsColor = textIconsColor;
    }
}
