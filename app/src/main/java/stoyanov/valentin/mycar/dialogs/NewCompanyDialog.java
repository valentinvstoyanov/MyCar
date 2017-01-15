package stoyanov.valentin.mycar.dialogs;

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
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class NewCompanyDialog extends DialogFragment {

    private TextInputLayout tilCompanyName;
    private Button btnAdd;
    private OnAddNewCompanyListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_company, null);
        initComponents(view);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    final String companyName = tilCompanyName.getEditText().getText().toString();
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
                    listener.onAddCompany(companyName);
                }
            }
        });
        builder.setCancelable(true);
        builder.setView(view);
        return builder.create();
    }

    private void initComponents(View view) {
        tilCompanyName = (TextInputLayout) view.findViewById(R.id.til_new_company_name);
        btnAdd = (Button) view.findViewById(R.id.btn_dialog_new_company_add);
    }

    private boolean isInputValid() {
        return true;
    }

    public void setListener(OnAddNewCompanyListener listener) {
        this.listener = listener;
    }

    public interface OnAddNewCompanyListener{
        void onAddCompany(String companyName);
    }
}
