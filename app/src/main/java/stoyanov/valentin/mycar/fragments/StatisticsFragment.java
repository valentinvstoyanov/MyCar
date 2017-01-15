package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.MainActivity;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class StatisticsFragment extends Fragment {

    private Realm myRealm;

    public StatisticsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        LineChart lineChart = (LineChart) view.findViewById(R.id.lineChart);
        Bundle bundle = getArguments();
        String vehicleId = bundle.getString(RealmTable.ID);
        MainActivity.Actions type = MainActivity.Actions
                .values()[bundle.getInt(MainActivity.STATISTIC_TYPE)];
        myRealm = Realm.getDefaultInstance();
        Vehicle vehicle = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, vehicleId).findFirst();
        switch (type) {
            case SERVICE:
                RealmResults<Service> services = vehicle.getServices().where().findAll();
                //RealmLineDataSet<Service> dataSet = new RealmLineDataSet<>(services, "action.price");
                ArrayList<Entry> entries = new ArrayList<>();
                int i = 0;
                for (Service service : services) {
                    entries.add(new Entry((float)i,
                            new BigDecimal(MoneyUtils.longToString(new BigDecimal(service.getAction().getPrice()))).floatValue()));
                    i += 20;
                }
                LineDataSet dataSet = new LineDataSet(entries, "Services");
                ArrayList<ILineDataSet> dataSetList = new ArrayList<>();
                dataSetList.add(dataSet);
                LineData lineData = new LineData(dataSetList);
                lineChart.setData(lineData);
                lineChart.invalidate();
                break;
            case EXPENSE:
                break;
            case INSURANCE:
                break;
            case REFUELING:
                break;
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myRealm.close();
    }
}
