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
import android.widget.Spinner;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.FuelType;

public class NewFuelTankDialog extends DialogFragment {

    private Spinner spnFTfuelType;
    private TextInputLayout tilFTCapacity;
    private TextInputLayout tilFTConsumption;
    private OnAddFuelTankListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_fuel_tank, null);
        initComponents(view);
        builder.setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isValidInput()) {
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
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
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
    }

    private boolean isValidInput() {
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
