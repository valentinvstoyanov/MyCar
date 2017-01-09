package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.MainActivity;
import stoyanov.valentin.mycar.adapters.ExpenseRecyclerViewAdapter;
import stoyanov.valentin.mycar.adapters.InsuranceRecyclerViewAdapter;
import stoyanov.valentin.mycar.adapters.RefuelingRecyclerViewAdapter;
import stoyanov.valentin.mycar.adapters.ServiceRecyclerViewAdapter;
import stoyanov.valentin.mycar.adapters.VehicleRecyclerViewAdapter;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class ListFragment extends Fragment {
    private Realm myRealm;

    public ListFragment() {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        int id = bundle.getInt(MainActivity.FRAGMENT_TYPE);
        String vehicleId;
        Vehicle vehicle;
        myRealm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        RealmRecyclerView recyclerView = (RealmRecyclerView) view.findViewById(R.id.realm_recycler_view);
        switch (id) {
            case R.id.nav_my_cars:
                RealmResults<Vehicle> vehicles = myRealm
                        .where(Vehicle.class)
                        .findAllSortedAsync(RealmTable.NAME, Sort.ASCENDING);
                VehicleRecyclerViewAdapter recyclerViewAdapter =
                        new VehicleRecyclerViewAdapter(getContext(),
                                vehicles, true, true);
                recyclerView.setAdapter(recyclerViewAdapter);
                break;
            case R.id.nav_services:
                vehicleId = bundle.getString(RealmTable.ID);
                vehicle = myRealm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, vehicleId)
                        .findFirst();
                if (vehicle != null) {
                    RealmResults<Service> services = vehicle.getServices()
                            .where()
                            .findAllAsync();
                    ServiceRecyclerViewAdapter adapter =
                            new ServiceRecyclerViewAdapter(getContext()
                                    , services, true, true);
                    recyclerView.setAdapter(adapter);
                }
                break;
            case R.id.nav_expenses:
                vehicleId = bundle.getString(RealmTable.ID);
                vehicle = myRealm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, vehicleId)
                        .findFirst();
                if (vehicle != null) {
                    RealmResults<Expense> expenses = vehicle.getExpenses()
                            .where()
                            .findAllAsync();
                    ExpenseRecyclerViewAdapter adapter =
                            new ExpenseRecyclerViewAdapter(getContext()
                                    , expenses, true, true);
                    recyclerView.setAdapter(adapter);
                }
                break;
            case R.id.nav_refuelings:
                vehicleId = bundle.getString(RealmTable.ID);
                vehicle = myRealm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, vehicleId)
                        .findFirst();
                if (vehicle != null) {
                    RealmResults<Refueling> refuelings = vehicle.getRefuelings().where().findAllAsync();
                    RefuelingRecyclerViewAdapter adapter =
                            new RefuelingRecyclerViewAdapter(getContext(),
                                    refuelings, true, true);
                    recyclerView.setAdapter(adapter);
                }
                break;
            case R.id.nav_insurances:
                vehicleId = bundle.getString(RealmTable.ID);
                vehicle = myRealm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, vehicleId)
                        .findFirst();
                if (vehicle != null) {
                    RealmResults<Insurance> insurances = vehicle.getInsurances()
                            .where()
                            .findAllAsync();
                    InsuranceRecyclerViewAdapter adapter =
                            new InsuranceRecyclerViewAdapter(getContext(),
                                    insurances, true, true);
                    recyclerView.setAdapter(adapter);
                }
                break;
            case R.id.nav_reminders:
                break;
            case R.id.nav_statistics:
                break;
            case R.id.nav_upcoming_events:
                break;
            case R.id.nav_history:
                break;
        }
        return view;
    }
}
