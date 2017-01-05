package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.MainActivity;
import stoyanov.valentin.mycar.activities.ViewVehicleActivity;
import stoyanov.valentin.mycar.adapters.ServiceRecyclerViewAdapter;
import stoyanov.valentin.mycar.adapters.VehicleRealmRecyclerViewAdapter;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IVehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleRepository;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class VehicleListFragment extends Fragment {
    private Realm myRealm;

    public VehicleListFragment() {}

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
        String vehicleId = bundle.getString(ViewVehicleActivity.VEHICLE_ID);
        myRealm = Realm.getDefaultInstance();
        Vehicle vehicle = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.NAME, vehicleId).findFirst();
        View view = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        final RealmRecyclerView recyclerView = (RealmRecyclerView) view.findViewById(R.id.realm_recycler_view);
        switch (id) {
            case R.id.nav_my_cars:
                VehicleRepository vehicleRepository = new VehicleRepository();
                vehicleRepository.getAllVehicles(new IVehicleRepository.OnGetAllVehiclesCallback() {
                    @Override
                    public void onSuccess(RealmResults<Vehicle> vehicles) {
                        VehicleRealmRecyclerViewAdapter recyclerViewAdapter =
                                new VehicleRealmRecyclerViewAdapter(getContext(), vehicles, true, true);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }
                });
                break;
            case R.id.nav_services:
                Log.d("onCreateView: ", "i am in nav_services");
                if (vehicle != null) {
                    ServiceRecyclerViewAdapter adapter = new ServiceRecyclerViewAdapter(getContext(),
                            vehicle.getServices().where().findAll(), true, true);
                    recyclerView.setAdapter(adapter);
                    Log.d("onCreateView: ", "vehicle is not null");
                }
                break;
            case R.id.nav_expenses:
                break;
            case R.id.nav_refuelings:
                break;
            case R.id.nav_insurances:
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
