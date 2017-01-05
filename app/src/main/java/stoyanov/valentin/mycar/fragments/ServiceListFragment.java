package stoyanov.valentin.mycar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewVehicleActivity;
import stoyanov.valentin.mycar.adapters.ServiceRecyclerViewAdapter;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class ServiceListFragment extends Fragment {

    public ServiceListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services_list, container, false);
        Bundle bundle = getArguments();
        String vehicleId = bundle.getString(ViewVehicleActivity.VEHICLE_ID);
        Realm myRealm = Realm.getDefaultInstance();
        Vehicle vehicle = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, vehicleId).findFirst();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
