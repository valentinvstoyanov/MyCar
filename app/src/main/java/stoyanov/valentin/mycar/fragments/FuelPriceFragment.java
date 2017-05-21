package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class FuelPriceFragment extends Fragment {

    public static final String TAG = "FuelPriceFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_properties, container, false);
        final LineChart lineChart = (LineChart) view.findViewById(R.id.lineChart);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm myRealm = Realm.getDefaultInstance();
                RealmResults<Refueling> refuelings = myRealm
                        .where(Refueling.class)
                        .equalTo(Constants.FUEL_TYPE, "Diesel")
                        .findAll();

                ArrayList<Entry> dDataSet = new ArrayList<>();
                int i = 0;
                for (Refueling refueling : refuelings) {
                    float f = MoneyUtils.longToFloat(new BigDecimal(refueling.getFuelPrice()));
                    dDataSet.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet dataSet = new LineDataSet(dDataSet, "Diesel");
                int color = ResourcesCompat.getColor(getResources(), R.color.pink_dark, null);
                dataSet.setLineWidth(3f);
                dataSet.setCircleRadius(6f);
                dataSet.setCircleHoleRadius(3f);
                dataSet.setColor(color);
                dataSet.setCircleColor(color);
                dataSet.setHighLightColor(color);
                dataSet.setDrawValues(false);
                final LineData lineData = new LineData(dataSet);

                refuelings = myRealm
                        .where(Refueling.class)
                        .equalTo(Constants.FUEL_TYPE, "Petrol")
                        .findAll();
                ArrayList<Entry> pEntries = new ArrayList<>();
                i = 0;
                for (Refueling refueling : refuelings) {
                    float f = MoneyUtils.longToFloat(new BigDecimal(refueling.getFuelPrice()));
                    pEntries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet pDataSet = new LineDataSet(pEntries, "Petrol");
                color = ResourcesCompat.getColor(getResources(), R.color.blue, null);
                pDataSet.setLineWidth(3f);
                dataSet.setCircleRadius(6f);
                dataSet.setCircleHoleRadius(3f);
                pDataSet.setColor(color);
                pDataSet.setCircleColor(color);
                pDataSet.setHighLightColor(color);
                pDataSet.setDrawValues(false);
                lineData.addDataSet(pDataSet);

                refuelings = myRealm
                        .where(Refueling.class)
                        .equalTo(Constants.FUEL_TYPE, "Autogas")
                        .findAll();
                ArrayList<Entry> aEntries = new ArrayList<>();
                i = 0;
                for (Refueling refueling : refuelings) {
                    float f = MoneyUtils.longToFloat(new BigDecimal(refueling.getFuelPrice()));
                    aEntries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet aDataSet = new LineDataSet(aEntries, "Autogas");
                color = ResourcesCompat.getColor(getResources(), R.color.yellow_dark, null);
                aDataSet.setLineWidth(3f);
                dataSet.setCircleRadius(6f);
                dataSet.setCircleHoleRadius(3f);
                aDataSet.setColor(color);
                aDataSet.setCircleColor(color);
                aDataSet.setHighLightColor(color);
                aDataSet.setDrawValues(false);
                lineData.addDataSet(aDataSet);

                refuelings = myRealm
                        .where(Refueling.class)
                        .equalTo(Constants.FUEL_TYPE, "Electric")
                        .findAll();
                ArrayList<Entry> eEntries = new ArrayList<>();
                i = 0;
                for (Refueling refueling : refuelings) {
                    float f = MoneyUtils.longToFloat(new BigDecimal(refueling.getFuelPrice()));
                    eEntries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet eDataSet = new LineDataSet(eEntries, "Electric");
                color = ResourcesCompat.getColor(getResources(), R.color.green, null);
                eDataSet.setLineWidth(3f);
                dataSet.setCircleRadius(6f);
                dataSet.setCircleHoleRadius(3f);
                eDataSet.setColor(color);
                eDataSet.setCircleColor(color);
                eDataSet.setHighLightColor(color);
                eDataSet.setDrawValues(false);
                lineData.addDataSet(eDataSet);

                lineChart.post(new Runnable() {
                    @Override
                    public void run() {
                        lineChart.setData(lineData);
                        lineChart.invalidate();
                    }
                });
                myRealm.close();
            }
        }).start();
        return view;
    }
}
