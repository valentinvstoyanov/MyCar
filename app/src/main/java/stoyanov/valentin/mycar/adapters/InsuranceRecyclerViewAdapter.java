package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Date;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class InsuranceRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<Insurance,
        InsuranceRecyclerViewAdapter.ViewHolder> {

    private int color;

    public InsuranceRecyclerViewAdapter(Context context, RealmResults<Insurance> realmResults,
                                        boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public InsuranceRecyclerViewAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_insurances_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindRealmViewHolder(InsuranceRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        Insurance insurance = realmResults.get(position);
        viewHolder.tvDate.setText(DateUtils.datetimeToString(insurance.getAction().getDate()));
        viewHolder.tvExpirationDate.setText(DateUtils.datetimeToString(insurance.getExpirationDate()));
        viewHolder.tvPrice.setText(MoneyUtils.longToString(new BigDecimal(insurance.getAction().getPrice())));
    }

    public class ViewHolder extends RealmViewHolder{

        public TextView tvDate, tvExpirationDate, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            View viewColor = itemView.findViewById(R.id.view_row_insurance_vehicle_color);
            viewColor.setBackgroundColor(color);
            tvDate = (TextView) itemView.findViewById(R.id.tv_row_insurance_date);
            tvExpirationDate = (TextView) itemView.findViewById(R.id.tv_row_insurance_expiration_date);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_row_insurance_price);
        }
    }
}
