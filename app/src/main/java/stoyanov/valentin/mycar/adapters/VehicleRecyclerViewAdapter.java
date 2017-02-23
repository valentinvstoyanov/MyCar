package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewVehicleActivity;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.ImageViewUtils;
import stoyanov.valentin.mycar.utils.RealmUtils;

public class VehicleRecyclerViewAdapter extends
        RealmBasedRecyclerViewAdapter<Vehicle, VehicleRecyclerViewAdapter.ViewHolder> {

    private View viewForSnackbar;
    private RealmChangeListener<RealmResults<Vehicle>> callback = new RealmChangeListener<RealmResults<Vehicle>>() {
        @Override
        public void onChange(RealmResults<Vehicle> element) {
            notifyDataSetChanged();
        }
    };

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
        viewHolder.relativeLayout.setBackgroundColor(vehicle.getColor().getColor());
        viewHolder.imageView.setBackground(ImageViewUtils.getDrawableByVehicleType(vehicle
                .getType(), getContext(), vehicle.getColor().getTextIconsColor()));
        viewHolder.tvVehicleName.setText(vehicle.getName());
        String text = String.format("%s %s", vehicle.getBrand().getName(), vehicle.getModel().getName());
        viewHolder.tvVehicleBrandAndModel.setText(text);
        viewHolder.tvVehicleManufactureDate.setText(DateUtils.dateToString(vehicle.getManufactureDate()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewVehicleActivity.class);
                intent.putExtra(RealmTable.ID, vehicle.getId());
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSwipedDismiss(final int position) {
        final Realm myRealm = Realm.getDefaultInstance();
        final Vehicle managedVehicle = realmResults.get(position);
        final Vehicle vehicle = myRealm.copyFromRealm(managedVehicle);
        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUtils.deleteVehicle(managedVehicle);
            }
        });
//        super.onItemSwipedDismiss(position);
        String text = vehicle.getType() + " " + vehicle.getName() + " deleted";
        Snackbar snackbar = Snackbar.make(viewForSnackbar, text, Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(vehicle);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        myRealm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        myRealm.close();
                        error.printStackTrace();
                    }
                });
            }
        });
        BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                    if (!myRealm.isClosed()) {
                        myRealm.close();
                    }
                }
                super.onDismissed(transientBottomBar, event);
            }
        };
        snackbar.addCallback(callback);
        snackbar.show();
    }

    public void setViewForSnackbar(View viewForSnackbar) {
        this.viewForSnackbar = viewForSnackbar;
    }

    public void addCallback() {
        realmResults.addChangeListener(callback);
    }

    public void removeCallback() {
        realmResults.removeChangeListener(callback);
    }

    public static class ViewHolder extends RealmViewHolder {
        public RelativeLayout relativeLayout;
        public ImageView imageView;
        public TextView tvVehicleName;
        public TextView tvVehicleBrandAndModel;
        public TextView tvVehicleManufactureDate;

        public ViewHolder(View itemView) {
            super(itemView);
            this.relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_cardview);
            this.imageView = (ImageView) itemView.findViewById(R.id.imgv_recyclerview_vehicle_type);
            this.tvVehicleName = (TextView) itemView.findViewById(R.id.tv_recyclerview_name);
            this.tvVehicleBrandAndModel = (TextView) itemView.findViewById(R.id.tv_recyclerview_brand_model);
            this.tvVehicleManufactureDate = (TextView) itemView.findViewById(R.id.tv_recyclerview_manufacture_date);
        }
    }
}
