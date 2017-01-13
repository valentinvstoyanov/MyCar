package stoyanov.valentin.mycar.utils;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtils {

    public static final String REGEX = "[\\w\\s]+";
    public static final String NUMBERS_REGEX = "\\d+";

    public static boolean isInputValid(String input) {
        return StringUtils.isAlphanumericSpace(input);
    }

    public static boolean isNumeric(String input) {
        return StringUtils.isNumeric(input);
    }
}
