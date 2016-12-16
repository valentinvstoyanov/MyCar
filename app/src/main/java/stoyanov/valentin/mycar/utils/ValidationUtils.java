package stoyanov.valentin.mycar.utils;

public class ValidationUtils {

    public static final String REGEX = "\\w+";

    public static boolean isInputValid(String input) {
        if (input.matches(REGEX)) {
            return true;
        }
        return false;
    }
}
