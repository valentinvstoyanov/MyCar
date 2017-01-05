package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class ServiceRecyclerViewAdapter
                extends RealmBasedRecyclerViewAdapter<Service, ServiceRecyclerViewAdapter.ViewHolder> {


    public ServiceRecyclerViewAdapter(Context context, RealmResults<Service> realmResults,
                                      boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ServiceRecyclerViewAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_services_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindRealmViewHolder(ServiceRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        Service service = realmResults.get(position);
        viewHolder.tvType.setText(service.getType().getName());
        viewHolder.tvDatetime.setText(DateUtils
                .datetimeToString(service.getAction().getDate()));
        viewHolder.tvNotifDatetime.setText("PROVIDE ME NOTIFDATE");
        viewHolder.tvPrice.setText(MoneyUtils
                .longToString(new BigDecimal(service.getAction().getPrice())));
        //click listener => dialog
    }

    public class ViewHolder extends RealmViewHolder{

        public TextView tvType, tvDatetime, tvNotifDatetime, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvType = (TextView) itemView.findViewById(R.id.tv_row_service_type);
            tvDatetime = (TextView) itemView.findViewById(R.id.tv_row_service_datetime);
            tvNotifDatetime = (TextView) itemView.findViewById(
                    R.id.tv_row_service_notification_datetime);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_row_service_price);
        }
    }
}
