package stoyanov.valentin.mycar.utils;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.widget.AutoCompleteTextView;

public class TextUtils {

    public static void setTextToTil(TextInputLayout til, String text) {
        TextInputEditText tiEt = (TextInputEditText) til.getEditText();
        if (tiEt != null) {
            tiEt.setText(text);
        }
    }

    public static void setTextToAutoComplete(TextInputLayout til, String text) {
        AutoCompleteTextView acTv = (AutoCompleteTextView) til.getEditText();
        if (acTv != null) {
            acTv.setText(text);
        }
    }

    public static String getTextFromAutoComplete(TextInputLayout til) {
        AutoCompleteTextView acTv = (AutoCompleteTextView) til.getEditText();
        return acTv != null ? acTv.getText().toString() : "";
    }

    public static String getTextFromTil(TextInputLayout til) {
        TextInputEditText tiEt = (TextInputEditText) til.getEditText();
        return tiEt != null ? tiEt.getText().toString() : "";
    }
}
