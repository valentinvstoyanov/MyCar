package stoyanov.valentin.mycar.utils;

public class ValidationUtils {

    public static final String REGEX = "[\\w\\s]+";
    public static final String NUMBERS_REGEX = "\\d+";

    public static boolean isInputValid(String input) {
        return input.matches(REGEX);
    }

    public static boolean isNumeric(String input) {
        return input.matches(NUMBERS_REGEX);
    }
}
