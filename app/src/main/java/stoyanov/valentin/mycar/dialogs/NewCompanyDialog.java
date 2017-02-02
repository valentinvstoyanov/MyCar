package stoyanov.valentin.mycar.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.interfaces.INewBaseActivity;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.TextUtils;

public class NewCompanyDialog extends DialogFragment implements INewBaseActivity{

    private View view;
    private TextInputLayout tilCompanyName;
    private Button btnAdd;
    private String companyName;
    private OnAddNewCompanyListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        initComponents();
        setComponentListeners();
        setContent();
        builder.setCancelable(true);
        builder.setView(view);
        return builder.create();
    }

    @SuppressLint("InflateParams")
    @Override
    public void initComponents() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_new_company, null);
        tilCompanyName = (TextInputLayout) view.findViewById(R.id.til_new_company_name);
        btnAdd = (Button) view.findViewById(R.id.btn_dialog_new_company_add);
    }

    @Override
    public void setComponentListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    saveToRealm();
                    listener.onAddCompany(companyName);
                }
            }
        });
    }

    @Override
    public void setContent() {}

    @Override
    public boolean isInputValid() {
        boolean valid = true;
        if (TextUtils.getTextFromTil(tilCompanyName).length() < 1) {
            tilCompanyName.setError("Incorrect company name");
            valid = false;
        }
        return valid;
    }

    @Override
    public void saveToRealm() {
        companyName = TextUtils.getTextFromTil(tilCompanyName);
        Realm myRealm = Realm.getDefaultInstance();
        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Company company = realm.where(Company.class)
                        .equalTo(RealmTable.NAME, companyName).findFirst();
                if (company == null) {
                    company = realm.createObject(Company.class, UUID.randomUUID().toString());
                    company.setName(companyName);
                }
            }
        });
        myRealm.close();
    }

    public void setListener(OnAddNewCompanyListener listener) {
        this.listener = listener;
    }

    public interface OnAddNewCompanyListener{
        void onAddCompany(String companyName);
    }
}
