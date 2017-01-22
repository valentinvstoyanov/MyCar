package stoyanov.valentin.mycar.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.res.ResourcesCompat;

import stoyanov.valentin.mycar.R;

public class ColorUtils {

    public static int pickColorByBackground(Context context, int bgColor) {
        TypedArray primaryColors = context.getResources()
                .obtainTypedArray(R.array.vehicles_primary_colors);
        int textIconColor = ResourcesCompat
                .getColor(context.getResources(), R.color.colorTextIcons, null);
        for (int i = 0; i < primaryColors.length(); i++) {
            int color = primaryColors.getColor(i, -1);
            if (color == bgColor) {
                if (i > 5) {
                    textIconColor = ResourcesCompat
                            .getColor(context.getResources(), R.color.colorPrimaryText, null);
                }
                break;
            }
        }
        primaryColors.recycle();
        return textIconColor;
    }

    public static int getDarkColor(Context context, int color) {
        TypedArray primaryColors = context.getResources()
                .obtainTypedArray(R.array.vehicles_primary_colors);
        TypedArray darkColors = context.getResources()
                .obtainTypedArray(R.array.vehicles_dark_colors);
        int darkColor = -1;
        for (int i = 0; i < primaryColors.length(); i++) {
            int currentColor = primaryColors.getColor(i, -1);
            if (currentColor == color) {
                darkColor = darkColors.getColor(i, -1);
                break;
            }
        }
        primaryColors.recycle();
        darkColors.recycle();
        return darkColor;
    }
}
