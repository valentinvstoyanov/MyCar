package stoyanov.valentin.mycar.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Vehicle;

public class MyRealmRecyclerViewAdapter extends
        RealmBasedRecyclerViewAdapter<Vehicle, MyRealmRecyclerViewAdapter.ViewHolder>{


    public MyRealmRecyclerViewAdapter(android.content.Context context,
                                      RealmResults<Vehicle> realmResults,
                                      boolean automaticUpdate,
                                      boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_vehicles_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindRealmViewHolder(final ViewHolder viewHolder, int position) {
        Vehicle vehicle = realmResults.get(position);
        viewHolder.imageView.setColorFilter(vehicle.getColor());
        viewHolder.tvVehicleName.setText(/*vehicle.getName()*/"MyCar" + position);
        viewHolder.tvVehicleBrand.setText(/*vehicle.getBrand().getName()*/"Audi");
        viewHolder.tvVehicleModel.setText("RS7");
        viewHolder.tvVehicleManufactureDate.setText("09.12.2016"/*vehicle.getManufactureDate().toString()*/);
    }

    public class ViewHolder extends RealmViewHolder {
        public ImageView imageView;
        public TextView tvVehicleName;
        public TextView tvVehicleBrand;
        public TextView tvVehicleModel;
        public TextView tvVehicleManufactureDate;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imgv_row_vehicle);
            this.tvVehicleName = (TextView) itemView.findViewById(R.id.tv_row_vehicle_name);
            this.tvVehicleBrand = (TextView) itemView.findViewById(R.id.tv_row_vehicle_brand);
            this.tvVehicleModel = (TextView) itemView.findViewById(R.id.tv_row_vehicle_model);
            this.tvVehicleManufactureDate = (TextView) itemView.findViewById(R.id.tv_row_vehicle_manufacture_date);
        }
    }
}
