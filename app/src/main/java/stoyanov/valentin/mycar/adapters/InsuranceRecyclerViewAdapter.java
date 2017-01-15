package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.text.Html;
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
        String text = String.format(getContext().getString(R.string.date_placeholder),
                DateUtils.datetimeToString(insurance.getAction().getDate()));
        viewHolder.tvCompany.setText(insurance.getCompany().getName());
        viewHolder.tvDate.setText(text);
        text = String.format(getContext().getString(R.string.expiration_date_placeholder),
                DateUtils.datetimeToString(insurance.getExpirationDate()));
        viewHolder.tvExpirationDate.setText(text);
        text = String.format(getContext().getString(R.string.price_placeholder),
                MoneyUtils.longToString(new BigDecimal(insurance.getAction().getPrice())));
        viewHolder.tvPrice.setText(text);
    }

    public class ViewHolder extends RealmViewHolder{

        public TextView tvCompany, tvDate, tvExpirationDate, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            View viewColor = itemView.findViewById(R.id.view_row_insurance_vehicle_color);
            viewColor.setBackgroundColor(color);
            tvCompany = (TextView) itemView.findViewById(R.id.tv_row_insurance_company);
            tvDate = (TextView) itemView.findViewById(R.id.tv_row_insurance_date);
            tvExpirationDate = (TextView) itemView.findViewById(R.id.tv_row_insurance_expiration_date);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_row_insurance_price);
        }
    }
}
