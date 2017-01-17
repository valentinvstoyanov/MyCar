package stoyanov.valentin.mycar.utils;

import android.util.Log;

import java.math.BigDecimal;

public class MoneyUtils {

    public static final BigDecimal LOWEST_UNIT = new BigDecimal(100);

    public static String longToString(BigDecimal bigDecimal) {
        return bigDecimal.divide(LOWEST_UNIT, 2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static float longToFloat(BigDecimal bigDecimal) {
        return bigDecimal.divide(LOWEST_UNIT, 2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static long stringToLong(String string) {
        return new BigDecimal(string).multiply(LOWEST_UNIT).longValue();
    }

    public static long calculateFuelPrice(String totalCost, String quantity) {
        BigDecimal dividend = new BigDecimal(totalCost);
        BigDecimal divisor = new BigDecimal(quantity);
        Log.d("Dividend : ", dividend.toPlainString());
        Log.d("Divisor : ", divisor.toPlainString());
        return dividend.divide(divisor, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(LOWEST_UNIT).longValue();
    }
}
