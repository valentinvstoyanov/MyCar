package stoyanov.valentin.mycar.fragments;

import android.content.Intent;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;
import io.realm.Realm;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.ChartUtils;

public class LineChartFragment extends Fragment {

   // private Realm myRealm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_properties, container, false);
        final LineChart lineChart = (LineChart) view.findViewById(R.id.lineChart);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getArguments();
                final String vehicleId = bundle.getString(RealmTable.ID);
                Realm myRealm = Realm.getDefaultInstance();
                final Vehicle vehicle = myRealm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, vehicleId).findFirst();

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


                ArrayList<Float> ivalues = ChartUtils.getPricesFromInsurances(vehicle.getInsurances());
                ArrayList<Entry> ientries = new ArrayList<>(ivalues.size());
                i = 0;
                for (Float f : ivalues) {
                    ientries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet iDataSet = new LineDataSet(ientries, "Insurances");
                color = ResourcesCompat.getColor(getResources(), R.color.green, null);
                iDataSet.setLineWidth(1.75f);
                iDataSet.setCircleRadius(5f);
                iDataSet.setCircleHoleRadius(2.5f);
                iDataSet.setColor(color);
                iDataSet.setCircleColor(color);
                iDataSet.setHighLightColor(color);
                iDataSet.setDrawValues(false);
                lineData.addDataSet(iDataSet);


                final ArrayList<Float> rvalues = ChartUtils.getPricesFromRefuelings(vehicle.getRefuelings());
                ArrayList<Entry> rentries = new ArrayList<>(rvalues.size());
                i = 0;
                for (Float f : rvalues) {
                    rentries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet rDataSet = new LineDataSet(rentries, "Refuelings");
                color = ResourcesCompat.getColor(getResources(), R.color.orange, null);
                rDataSet.setLineWidth(1.75f);
                rDataSet.setCircleRadius(5f);
                rDataSet.setCircleHoleRadius(2.5f);
                rDataSet.setColor(color);
                rDataSet.setCircleColor(color);
                rDataSet.setHighLightColor(color);
                rDataSet.setDrawValues(false);
                lineData.addDataSet(rDataSet);

                ArrayList<Float> evalues = ChartUtils.getPricesFromExpenses(vehicle.getExpenses());
                ArrayList<Entry> eentries = new ArrayList<>(evalues.size());
                i = 0;
                for (Float f : evalues) {
                    eentries.add(new Entry(i, f));
                    i += 10;
                }
                LineDataSet eDataSet = new LineDataSet(eentries, "Expenses");
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
                        /*lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                            @Override
                            public void onValueSelected(Entry e, Highlight h) {
                                ILineDataSet d = lineData.getDataSetForEntry(e);
                                String label = d.getLabel();
                                int index = d.getEntryIndex(e);
                                Intent intent = new Intent(getContext(), ViewActivity.class);
                                switch (label) {
                                    case "Insurances":
                                        Insurance insurance = vehicle.getInsurances().get(index);
                                        intent.putExtra(RealmTable.INSURANCES + RealmTable.ID, insurance.getId());
                                        intent.putExtra(RealmTable.TYPE, ActivityType.INSURANCE.ordinal());
                                        break;
                                    case "Expenses":
                                        Expense expense = vehicle.getExpenses().get(index);
                                        intent.putExtra(RealmTable.EXPENSES + RealmTable.ID, expense.getId());
                                        intent.putExtra(RealmTable.TYPE, ActivityType.EXPENSE.ordinal());
                                        break;
                                    case "Refuelings":
                                        Refueling refueling = vehicle.getRefuelings().get(index);
                                        intent.putExtra(RealmTable.REFUELINGS + RealmTable.ID, refueling.getId());
                                        intent.putExtra(RealmTable.TYPE, ActivityType.REFUELING.ordinal());
                                        break;
                                    default:
                                        Service service = vehicle.getServices().get(index);
                                        intent.putExtra(RealmTable.SERVICES + RealmTable.ID, service.getId());
                                        intent.putExtra(RealmTable.TYPE, ActivityType.SERVICE.ordinal());
                                        break;
                                }
                                intent.putExtra(RealmTable.ID, vehicleId);
                                startActivity(intent);
                            }

                            @Override
                            public void onNothingSelected() {

                            }
                        });*/
                        lineChart.invalidate();
                    }
                });
                myRealm.close();
            }
        }).start();
        return view;
    }

  /*  @Override
    public void onDestroyView() {
        super.onDestroyView();
        myRealm.close();
    }*/
}
