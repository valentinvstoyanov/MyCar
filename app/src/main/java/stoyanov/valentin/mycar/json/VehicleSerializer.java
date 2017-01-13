package stoyanov.valentin.mycar.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;

public class VehicleSerializer implements JsonSerializer<Vehicle>{

    @Override
    public JsonElement serialize(Vehicle src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        //should be 18
        jsonObject.addProperty(RealmTable.NAME, src.getName());
        jsonObject.addProperty(RealmTable.HORSE_POWER, src.getHorsePower());
        jsonObject.addProperty(RealmTable.CUBIC_CENTIMETERS, src.getCubicCentimeter());
        //jsonObject.addProperty(RealmTable.COLOR, src.getColor());
        jsonObject.addProperty(RealmTable.ODOMETER, src.getOdometer());
        jsonObject.addProperty(RealmTable.MANUFACTURE_DATE,
                DateUtils.manufactureDateToString(src.getManufactureDate()));
        jsonObject.addProperty(RealmTable.REGISTRATION_PLATE, src.getRegistrationPlate());
        jsonObject.addProperty(RealmTable.VIN_PLATE, src.getVinPlate());
        jsonObject.add(RealmTable.BRAND, context.serialize(src.getBrand()));
        jsonObject.add(RealmTable.MODEL, context.serialize(src.getModel()));
        jsonObject.addProperty(RealmTable.NAME, src.getName());
        jsonObject.addProperty(RealmTable.NAME, src.getName());

        return null;
    }
}
