package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewVehicleActivity;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;

public class VehicleRecyclerViewAdapter extends
        RealmBasedRecyclerViewAdapter<Vehicle, VehicleRecyclerViewAdapter.ViewHolder>{


    public VehicleRecyclerViewAdapter(Context context,
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
    public void onBindRealmViewHolder(final ViewHolder viewHolder, final int position) {
        final Vehicle vehicle = realmResults.get(position);
        viewHolder.relativeLayout.setBackgroundColor(vehicle.getColor());
        Drawable drawable;
        switch (vehicle.getType().getName()) {
            case "Bus":
                drawable = ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.ic_bus_black, null);
                break;
            case "Motorcycle":
                drawable = ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.ic_motorcycle_black, null);
                break;
            case "Truck":
                drawable = ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.ic_truck_black_24dp, null);
                break;
            default:
                drawable = ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.ic_car_black, null);
                break;
        }
        viewHolder.imageView.setBackground(drawable);
        viewHolder.tvVehicleName.setText(vehicle.getName());
        String text = String.format("%s %s", vehicle.getBrand().getName(), vehicle.getModel().getName());
        viewHolder.tvVehicleBrandAndModel.setText(text);
        viewHolder.tvVehicleManufactureDate.setText(DateUtils.manufactureDateToString(vehicle.getManufactureDate()));
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onItemSwipedDismiss(position);
            }
        });
        viewHolder.btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewVehicleActivity.class);
                intent.putExtra(RealmTable.ID, vehicle.getId());
                getContext().startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RealmViewHolder {
        public RelativeLayout relativeLayout;
        public ImageView imageView;
        public TextView tvVehicleName;
        public TextView tvVehicleBrandAndModel;
        public TextView tvVehicleManufactureDate;
        public Button btnDelete;
        public Button btnOpen;

        public ViewHolder(View itemView) {
            super(itemView);
            this.relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_cardview);
            this.imageView = (ImageView) itemView.findViewById(R.id.imgv_recyclerview_vehicle_type);
            this.tvVehicleName = (TextView) itemView.findViewById(R.id.tv_recyclerview_name);
            this.tvVehicleBrandAndModel = (TextView) itemView.findViewById(R.id.tv_recyclerview_brand_model);
            this.tvVehicleManufactureDate = (TextView) itemView.findViewById(R.id.tv_recyclerview_manufacture_date);
            this.btnDelete = (Button) itemView.findViewById(R.id.btn_recyclerview_delete);
            this.btnOpen = (Button) itemView.findViewById(R.id.btn_recyclerview_open);
        }
    }
}
