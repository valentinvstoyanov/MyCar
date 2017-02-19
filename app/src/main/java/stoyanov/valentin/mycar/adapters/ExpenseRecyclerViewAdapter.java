package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class ExpenseRecyclerViewAdapter extends
        BaseRealmAdapter<Expense, ExpenseRecyclerViewAdapter.ViewHolder>
    {

    public ExpenseRecyclerViewAdapter(Context context, RealmResults<Expense> realmResults,
                                      boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_properties_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final Expense expense = realmResults.get(position);
        viewHolder.tvType.setText(expense.getType());
        viewHolder.tvDatetime.setText(DateUtils
                .datetimeToString(expense.getDate()));
        String price = MoneyUtils.longToString(new BigDecimal(expense.getPrice()))
                + " " + getRealmSettings().getCurrencyUnit();
        String odometer = expense.getOdometer() + getRealmSettings().getLengthUnit();
        viewHolder.tvOdometer.setText(odometer);
        viewHolder.tvPrice.setText(price);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewActivity.class);
                intent.putExtra(RealmTable.ID, getVehicleId());
                intent.putExtra(RealmTable.EXPENSES + RealmTable.ID, expense.getId());
                intent.putExtra(RealmTable.TYPE, ActivityType.EXPENSE.ordinal());
                getContext().startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RealmViewHolder {

        public TextView tvType, tvDatetime, tvPrice, tvOdometer;

        public ViewHolder(View itemView) {
            super(itemView);
            View viewColor = itemView.findViewById(R.id.view_row_service_vehicle_color);
            viewColor.setBackgroundColor(getColor());
            tvType = (TextView) itemView.findViewById(R.id.tv_row_service_type);
            tvDatetime = (TextView) itemView.findViewById(R.id.tv_row_service_datetime);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_row_service_price);
            tvOdometer = (TextView) itemView.findViewById(R.id.tv_row_service_notification_datetime);
        }
    }
}
