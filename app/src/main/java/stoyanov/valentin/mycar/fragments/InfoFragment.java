package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class InfoFragment extends Fragment {

    private View view;

    public InfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        setContent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent();
    }

    private void setContent() {
        Realm myRealm = Realm.getDefaultInstance();
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_fragment_info);
        linearLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        displayView("Total vehicles", String.valueOf(myRealm.where(Vehicle.class).count()),
                inflater);
        RealmResults<Service> services = myRealm.where(Service.class).findAll();
        long p = 0;
        for (Service service : services) {
            p += service.getAction().getPrice();
        }
        displayView("Total services", String.valueOf(services.size()), inflater);
        String text = MoneyUtils.longToString(new BigDecimal(p));
        displayView("Money spent for services", text, inflater);
        RealmResults<Insurance> insurances = myRealm.where(Insurance.class).findAll();
        displayView("Total insurances", String.valueOf(insurances.size()), inflater);
        p = 0;
        for (Insurance insurance : insurances) {
            p += insurance.getAction().getPrice();
        }
        text = MoneyUtils.longToString(new BigDecimal(p));
        displayView("Money spent for insurances", text, inflater);
        RealmResults<Refueling> refuelings = myRealm.where(Refueling.class).findAll();
        displayView("Total refuelings", String.valueOf(refuelings.size()), inflater);
        p = 0;
        for (Refueling refueling : refuelings) {
            p += refueling.getAction().getPrice();
        }
        text = MoneyUtils.longToString(new BigDecimal(p));
        displayView("Money spent for refuelings", text, inflater);
        RealmResults<Expense> expenses = myRealm.where(Expense.class).findAll();
        displayView("Total expenses", String.valueOf(expenses.size()), inflater);
        p = 0;
        for (Expense expense : expenses) {
            p += expense.getAction().getPrice();
        }
        text = MoneyUtils.longToString(new BigDecimal(p));
        displayView("Money spent for expenses", text, inflater);
        text = MoneyUtils.longToString(new BigDecimal(myRealm.where(Action.class)
                .sum(RealmTable.PRICE).longValue()));
        displayView("Total cost", text, inflater);
        myRealm.close();
    }

    private void displayView(String title, String value, LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.item_view_activity, null);
        TextView tvTitle = (TextView) v.findViewById(R.id.tv_item_view_title);
        TextView tvValue = (TextView) v.findViewById(R.id.tv_item_view_value);
        tvTitle.setText(title);
        tvValue.setText(value);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_fragment_info);
        linearLayout.addView(v);
    }
}
