package stoyanov.valentin.mycar.utils;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtils {

    public static boolean isInputValid(String input) {
        return input != null && !input.isEmpty() && StringUtils.isAlphanumericSpace(input);
    }

    public static boolean isNumeric(String input) {
        return StringUtils.isNumeric(input);
    }
}
