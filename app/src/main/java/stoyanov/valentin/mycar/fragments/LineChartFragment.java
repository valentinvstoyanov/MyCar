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

import java.util.ArrayList;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.ChartUtils;

public class LineChartFragment extends Fragment {

    public static final String TAG = "LineChartFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_properties, container, false);
        final LineChart lineChart = (LineChart) view.findViewById(R.id.lineChart);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getArguments();
                final String vehicleId = bundle.getString(Constants.ID);
                Realm myRealm = Realm.getDefaultInstance();
                final Vehicle vehicle = myRealm.where(Vehicle.class)
                        .equalTo(Constants.ID, vehicleId).findFirst();

                ArrayList<Float> values = ChartUtils.getPricesFromServices(vehicle.getServices());
                ArrayList<Entry> entries = new ArrayList<>(values.size());
                int i = 0;
                for (Float f : values) {
                    entries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet servicesDataSet = new LineDataSet(entries, "Services");
                int color = ResourcesCompat.getColor(getResources(), R.color.blue, null);
                servicesDataSet.setLineWidth(3f);
                servicesDataSet.setCircleRadius(8f);
                servicesDataSet.setCircleHoleRadius(4f);
                servicesDataSet.setColor(color);
                servicesDataSet.setCircleColor(color);
                servicesDataSet.setHighLightColor(color);
                servicesDataSet.setDrawValues(false);
                final LineData lineData = new LineData(servicesDataSet);


                ArrayList<Float> iValues = ChartUtils.getPricesFromInsurances(vehicle.getInsurances());
                ArrayList<Entry> iEntries = new ArrayList<>(iValues.size());
                i = 0;
                for (Float f : iValues) {
                    iEntries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet iDataSet = new LineDataSet(iEntries, "Insurances");
                color = ResourcesCompat.getColor(getResources(), R.color.green, null);
                iDataSet.setLineWidth(1.75f);
                iDataSet.setCircleRadius(5f);
                iDataSet.setCircleHoleRadius(2.5f);
                iDataSet.setColor(color);
                iDataSet.setCircleColor(color);
                iDataSet.setHighLightColor(color);
                iDataSet.setDrawValues(false);
                lineData.addDataSet(iDataSet);


                final ArrayList<Float> rValues = ChartUtils.getPricesFromRefuelings(vehicle.getRefuelings());
                ArrayList<Entry> rEntries = new ArrayList<>(rValues.size());
                i = 0;
                for (Float f : rValues) {
                    rEntries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet rDataSet = new LineDataSet(rEntries, "Refuelings");
                color = ResourcesCompat.getColor(getResources(), R.color.orange, null);
                rDataSet.setLineWidth(1.75f);
                rDataSet.setCircleRadius(5f);
                rDataSet.setCircleHoleRadius(2.5f);
                rDataSet.setColor(color);
                rDataSet.setCircleColor(color);
                rDataSet.setHighLightColor(color);
                rDataSet.setDrawValues(false);
                lineData.addDataSet(rDataSet);

                ArrayList<Float> eValues = ChartUtils.getPricesFromExpenses(vehicle.getExpenses());
                ArrayList<Entry> eEntries = new ArrayList<>(eValues.size());
                i = 0;
                for (Float f : eValues) {
                    eEntries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet eDataSet = new LineDataSet(eEntries, "Expenses");
                color = ResourcesCompat.getColor(getResources(), R.color.red, null);
                eDataSet.setLineWidth(1.75f);
                eDataSet.setCircleRadius(5f);
                eDataSet.setCircleHoleRadius(2.5f);
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
