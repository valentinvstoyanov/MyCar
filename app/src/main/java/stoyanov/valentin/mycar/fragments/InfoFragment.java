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
import io.realm.RealmQuery;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.RealmSettings;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class InfoFragment extends Fragment {

    public static final String TAG = "InfoFragment";

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
        RealmSettings settings = myRealm.where(RealmSettings.class).findFirst();
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_fragment_info);
        linearLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        BigDecimal totalCost = new BigDecimal(0);
        displayView("Total vehicles", String.valueOf(myRealm.where(Vehicle.class).count()),
                inflater);
        RealmQuery<Service> services = myRealm.where(Service.class);
        displayView("Total services", String.valueOf(services.count()), inflater);
        BigDecimal bigDecimal = new BigDecimal(services.sum(RealmTable.PRICE).toString());
        String text = "%s " + settings.getCurrencyUnit();
        totalCost = totalCost.add(bigDecimal);
        displayView("Money spent for services", String.format(text, MoneyUtils.longToString(bigDecimal)), inflater);

        RealmQuery<Insurance> insurances = myRealm.where(Insurance.class);
        displayView("Total insurances", String.valueOf(insurances.count()), inflater);
        bigDecimal = new BigDecimal(insurances.sum(RealmTable.PRICE).toString());
        //text = MoneyUtils.longToString(bigDecimal);
        totalCost = totalCost.add(bigDecimal);
        displayView("Money spent for insurances", String.format(text, MoneyUtils.longToString(bigDecimal)), inflater);

        RealmQuery<Refueling> refuelings = myRealm.where(Refueling.class);
        displayView("Total refuelings", String.valueOf(refuelings.count()), inflater);
        bigDecimal = new BigDecimal(refuelings.sum(RealmTable.PRICE).toString());
        //text = MoneyUtils.longToString(bigDecimal);
        totalCost = totalCost.add(bigDecimal);
        displayView("Money spent for refuelings", String.format(text, MoneyUtils.longToString(bigDecimal)), inflater);

        RealmQuery<Expense> expenses = myRealm.where(Expense.class);
        displayView("Total expenses", String.valueOf(expenses.count()), inflater);
        bigDecimal = new BigDecimal(expenses.sum(RealmTable.PRICE).toString());
        //text = MoneyUtils.longToString(bigDecimal);
        displayView("Money spent for expenses", String.format(text, MoneyUtils.longToString(bigDecimal)), inflater);
        totalCost = totalCost.add(bigDecimal);

        /*text = MoneyUtils.longToString(new BigDecimal(myRealm.where(Action.class)
                .sum(RealmTable.PRICE).longValue()));*/
        displayView("TOTAL COSTS", String.format(text, MoneyUtils.longToString(totalCost)), inflater);
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
