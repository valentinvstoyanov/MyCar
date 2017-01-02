package stoyanov.valentin.mycar.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.utils.ValidationUtils;

public class ChangeVehiclePropertyDialog extends DialogFragment {
    private String text;
    private TextView textView;
    private TextInputLayout textInputLayout;
    private Button btnSet;
    private OnChangeVehiclePropertyListener listener;
    private ValidationType validationType;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_vehicle_property, null);
        initComponents(view);
        String str = String.format(getString(R.string.change_placeholder), text);
        textView.setText(str);
        textInputLayout.setHint(text);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInput()) {
                    listener.onChange(textInputLayout.getEditText().getText().toString());
                }
            }
        });
        builder.setCancelable(true);
        builder.setView(view);
        return builder.create();
    }

    private void initComponents(View view) {
        textView = (TextView) view.findViewById(R.id.tv_dialog_change_vehicle_property);
        textInputLayout = (TextInputLayout) view.findViewById(R.id.til_dialog_change_vehicle_property);
        btnSet = (Button) view.findViewById(R.id.btn_dialog_change_vehicle_property_set);
    }

    private boolean isValidInput() {
        boolean valid = true;
        String tilText = textInputLayout.getEditText().getText().toString();
        switch (validationType) {
            case STRING:
                if (!ValidationUtils.isInputValid(tilText)) {
                    textInputLayout.setError("Incorrect input");
                    valid = false;
                }
                break;
            case NUMBER:
                if (!ValidationUtils.isNumeric(tilText)) {
                    textInputLayout.setError("Number expected");
                    valid = false;
                }
                break;
            case NOTE:
                if (tilText.length() < 1) {
                    textInputLayout.setError("No text entered");
                    valid = false;
                }
                break;
        }
        return valid;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setListener(OnChangeVehiclePropertyListener listener) {
        this.listener = listener;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public interface OnChangeVehiclePropertyListener{
        void onChange(String input);
    }

    public enum ValidationType {
        STRING, NUMBER, NOTE
    }
}
