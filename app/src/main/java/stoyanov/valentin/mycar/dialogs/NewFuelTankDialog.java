package stoyanov.valentin.mycar.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.BaseActivity;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.FuelType;

public class NewFuelTankDialog extends DialogFragment {

    private Spinner spnFTfuelType;
    private TextInputLayout tilFTCapacity;
    private TextInputLayout tilFTConsumption;
    private Button btnAdd;
    private OnAddFuelTankListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_fuel_tank, null);
        initComponents(view);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    FuelTank fuelTank = new FuelTank();
                    FuelType fuelType = new FuelType();
                    fuelType.setName(spnFTfuelType.getSelectedItem().toString());
                    fuelTank.setFuelType(fuelType);
                    fuelTank.setCapacity(Integer.parseInt(tilFTCapacity.getEditText()
                            .getText().toString()));
                    fuelTank.setConsumption(Integer.parseInt(tilFTConsumption.getEditText()
                            .getText().toString()));
                    listener.onAddFuelTank(fuelTank);
                }
            }
        });
        builder.setCancelable(true);
        builder.setView(view);
        return builder.create();
    }

    private void initComponents(View view) {
        spnFTfuelType = (Spinner) view.findViewById(R.id.spn_new_ft_fuel_type);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
                .createFromResource(getContext(),
                        R.array.fuel_types, R.layout.textview_spinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFTfuelType.setAdapter(spinnerAdapter);
        tilFTCapacity = (TextInputLayout) view.findViewById(R.id.til_new_ft_capacity);
        tilFTConsumption = (TextInputLayout) view.findViewById(R.id.til_new_ft_consumption);
        btnAdd = (Button) view.findViewById(R.id.btn_dialog_new_ft_add);
    }

    private boolean isInputValid() {
        boolean valid = true;
        if (tilFTCapacity.getEditText().getText().toString().length() < 1) {
            valid = false;
            tilFTCapacity.setError("Incorrect capacity");
        }
        if (tilFTConsumption.getEditText().getText().toString().length() < 1) {
            valid = false;
            tilFTConsumption.setError("Incorrect consumption");
        }
        return valid;
    }

    public void setListener(OnAddFuelTankListener listener) {
        this.listener = listener;
    }

    public interface OnAddFuelTankListener{
        void onAddFuelTank(FuelTank fuelTank);
    }
}
