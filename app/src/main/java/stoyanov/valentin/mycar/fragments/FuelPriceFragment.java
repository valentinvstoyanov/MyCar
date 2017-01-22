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
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class FuelPriceFragment extends Fragment {

    private Realm myRealm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_properties, container, false);
        LineChart lineChart = (LineChart) view.findViewById(R.id.lineChart);
        myRealm = Realm.getDefaultInstance();
        RealmResults<Refueling> refuelings = myRealm.where(Refueling.class).findAll();
        ArrayList<Float> floats = new ArrayList<>(refuelings.size());
        ArrayList<Entry> entries = new ArrayList<>(refuelings.size());
        for (Refueling refueling : refuelings) {
            float f = MoneyUtils.longToFloat(new BigDecimal(refueling.getFuelPrice()));
            floats.add(f);
            entries.add(new Entry(refueling.getAction().getDate().getTime(), f));
        }
        LineDataSet dataSet = new LineDataSet(entries, "fuel price");

        int color = ResourcesCompat.getColor(getResources(), R.color.pink, null);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(8f);
        dataSet.setCircleHoleRadius(4f);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setHighLightColor(color);
        dataSet.setDrawValues(false);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myRealm.close();
    }
}
