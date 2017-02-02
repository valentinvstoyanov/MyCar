package stoyanov.valentin.mycar.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.interfaces.INewBaseActivity;
import stoyanov.valentin.mycar.realm.models.RealmSettings;

public class SettingsDialog extends DialogFragment implements INewBaseActivity{

    private View view;
    private Spinner spnLength, spnCurrency;
    private TextInputLayout tilDistanceInAdvance;
    private Button btnSave;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        initComponents();
        setComponentListeners();
        setContent();
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void initComponents() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_settings, null);
        spnLength = (Spinner) view.findViewById(R.id.spn_settings_length);
        spnCurrency = (Spinner) view.findViewById(R.id.spn_settings_currency);
        tilDistanceInAdvance = (TextInputLayout) view.findViewById(R.id.til_settings_distance_advance);
        btnSave = (Button) view.findViewById(R.id.btn_settings_save);
    }

    @Override
    public void setComponentListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    saveToRealm();
                    dismiss();
                }
            }
        });
    }

    @Override
    public void setContent() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.length_unit, R.layout.textview_spinner);
        spnLength.setAdapter(spinnerAdapter);
        Realm myRealm = Realm.getDefaultInstance();
        RealmSettings settings = myRealm.where(RealmSettings.class).findFirst();
        tilDistanceInAdvance.getEditText().setText(settings.getDistanceInAdvance());
        spnLength.setSelection(spinnerAdapter.getPosition(settings.getLengthUnit()));
        myRealm.close();
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void saveToRealm() {
        final Realm myRealm = Realm.getDefaultInstance();
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmSettings settings = myRealm.where(RealmSettings.class).findFirst();
                settings.setDistanceInAdvance(Integer.parseInt(tilDistanceInAdvance
                        .getEditText().getText().toString()));
                settings.setLengthUnit(spnLength.getSelectedItem().toString());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                myRealm.close();
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                myRealm.close();
                Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
