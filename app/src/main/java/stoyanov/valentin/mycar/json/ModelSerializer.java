package stoyanov.valentin.mycar.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class ModelSerializer implements JsonSerializer<Model> {

    @Override
    public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RealmTable.NAME, src.getName());
        return jsonObject;
    }
}
