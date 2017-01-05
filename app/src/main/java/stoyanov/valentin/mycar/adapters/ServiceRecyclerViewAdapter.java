package stoyanov.valentin.mycar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.math.BigDecimal;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class ServiceRecyclerViewAdapter
                extends RecyclerView.Adapter<ServiceRecyclerViewAdapter.ViewHolder>{

    private Service[] services;

    public ServiceRecyclerViewAdapter(Service[] services) {
        this.services = services;
    }

    @Override
    public ServiceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_services_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServiceRecyclerViewAdapter.ViewHolder holder, int position) {
        Service service = services[position];
        holder.tvDatetime.setText(DateUtils.datetimeToString(service.getAction().getDate()));
        //notifdate
        holder.tvType.setText(service.getType().getName());
        String price = MoneyUtils.longToString(new BigDecimal(service.getAction().getPrice()));
        holder.tvPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return services.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDatetime, tvNotificationDatetime, tvType, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDatetime = (TextView) itemView.findViewById(R.id.tv_service_datetime);
            tvNotificationDatetime = (TextView) itemView
                    .findViewById(R.id.tv_service_notification_datetime);
            tvType = (TextView) itemView.findViewById(R.id.tv_service_type);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_service_price);
        }
    }
}
