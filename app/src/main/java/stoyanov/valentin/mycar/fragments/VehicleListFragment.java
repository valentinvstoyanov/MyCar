package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.adapters.VehicleRealmRecyclerViewAdapter;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IVehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleRepository;

public class VehicleListFragment extends Fragment {

    public VehicleListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        VehicleRepository vehicleRepository = new VehicleRepository();
        final View view = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        final RealmRecyclerView recyclerView = (RealmRecyclerView) view.findViewById(R.id.realm_recycler_view);
        vehicleRepository.getAllVehicles(new IVehicleRepository.OnGetAllVehiclesCallback() {
            @Override
            public void onSuccess(RealmResults<Vehicle> vehicles) {
                VehicleRealmRecyclerViewAdapter recyclerViewAdapter =
                        new VehicleRealmRecyclerViewAdapter(getContext(), vehicles, true, true);
                    recyclerView.setAdapter(recyclerViewAdapter);
            }
        });
        return view;
    }
}
