package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class ServiceRecyclerViewAdapter
                extends RealmBasedRecyclerViewAdapter<Service, ServiceRecyclerViewAdapter.ViewHolder> {

    private int color;
    private String vehicleId;

    public ServiceRecyclerViewAdapter(Context context, RealmResults<Service> realmResults,
                                      boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public ServiceRecyclerViewAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_properties_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindRealmViewHolder(final ServiceRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final Service service = realmResults.get(position);
        viewHolder.tvType.setText(service.getType().getName());
        viewHolder.tvDatetime.setText(DateUtils
                .datetimeToString(service.getAction().getDate()));
        viewHolder.tvNotifDatetime.setText("PROVIDE ME NOTIFDATE");
        viewHolder.tvPrice.setText(MoneyUtils
                .longToString(new BigDecimal(service.getAction().getPrice())));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewActivity.class);
                intent.putExtra(RealmTable.ID, vehicleId);
                intent.putExtra(RealmTable.SERVICES + RealmTable.ID, service.getId());
                intent.putExtra(RealmTable.TYPE, ViewActivity.ViewType.SERVICE.ordinal());
                getContext().startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RealmViewHolder{

        public TextView tvType, tvDatetime, tvNotifDatetime, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            View viewColor = itemView.findViewById(R.id.view_row_service_vehicle_color);
            viewColor.setBackgroundColor(color);
            tvType = (TextView) itemView.findViewById(R.id.tv_row_service_type);
            tvDatetime = (TextView) itemView.findViewById(R.id.tv_row_service_datetime);
            tvNotifDatetime = (TextView) itemView.findViewById(
                    R.id.tv_row_service_notification_datetime);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_row_service_price);
        }
    }
}
