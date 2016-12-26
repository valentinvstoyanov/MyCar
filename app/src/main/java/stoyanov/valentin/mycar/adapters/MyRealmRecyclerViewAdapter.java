package stoyanov.valentin.mycar.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.MainActivity;
import stoyanov.valentin.mycar.activities.ViewVehicleActivity;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.DateUtils;

import static android.support.design.R.styleable.CoordinatorLayout;

public class MyRealmRecyclerViewAdapter extends
        RealmBasedRecyclerViewAdapter<Vehicle, MyRealmRecyclerViewAdapter.ViewHolder>{


    public MyRealmRecyclerViewAdapter(Context context,
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
        //imageview
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
                intent.putExtra(ViewVehicleActivity.CAR_NAME, realmResults.get(position).getId());
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
