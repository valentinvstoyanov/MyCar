package stoyanov.valentin.mycar.utils;

import java.math.BigDecimal;

public class MoneyUtils {

    public static final BigDecimal LOWEST_UNIT = new BigDecimal(100);

    public static String longToString(BigDecimal bigDecimal) {
        return bigDecimal.divide(LOWEST_UNIT).setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    public static long stringToLong(String string) {
        return new BigDecimal(string).longValue();
    }
}
