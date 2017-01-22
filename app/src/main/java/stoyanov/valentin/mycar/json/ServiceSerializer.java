package stoyanov.valentin.mycar.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class ServiceSerializer implements JsonSerializer<Service> {

    @Override
    public JsonElement serialize(Service src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RealmTable.ID, src.getId());
        return null;
    }
}
