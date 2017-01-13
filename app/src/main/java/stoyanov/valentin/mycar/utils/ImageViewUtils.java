package stoyanov.valentin.mycar.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import stoyanov.valentin.mycar.R;

public class ImageViewUtils {

    public static String getDrawableNameByVehicleType(String type) {
        switch (type) {
            case "Bus":
                return "ic_bus_black";
            case "Motorcycle":
                return "ic_motorcycle_black";
            case "Truck":
                return "ic_truck_black_24dp";
            default:
                return "ic_car_black";
        }
    }

    public static int getDrawableResourceIdByDrawableName(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static Drawable getDrawableByVehicleType(String type, Context context, int color) {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                getDrawableResourceIdByDrawableName(context, getDrawableNameByVehicleType(type))
                , null);
        if (drawable != null) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        return drawable;
    }
}
