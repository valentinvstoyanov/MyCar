package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class RefuelingRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<Refueling,
        RefuelingRecyclerViewAdapter.ViewHolder> {

    private int color;

    public RefuelingRecyclerViewAdapter(Context context, RealmResults<Refueling> realmResults, boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public RefuelingRecyclerViewAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_refuelings_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindRealmViewHolder(RefuelingRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        Refueling refueling = realmResults.get(position);
        String text;
        Realm myRealm = Realm.getDefaultInstance();
        FuelTank fuelTank = myRealm.where(FuelTank.class)
                .equalTo(RealmTable.ID, refueling.getFuelTankId()).findFirst();
        viewHolder.tvFuelType.setText(fuelTank.getFuelType().getName());
        text = fuelTank.getFuelType().getUnit();
        myRealm.close();
        text = String.format("%d %s", refueling.getQuantity(), text);
        viewHolder.tvQuantity.setText(text);
        text = String.format(getContext().getString(R.string.fuel_price_placeholder),
                MoneyUtils.longToString(new BigDecimal(refueling.getFuelPrice())));
        viewHolder.tvFuelPrice.setText(text);
        text = String.format(getContext().getString(R.string.price_placeholder),
                MoneyUtils.longToString(new BigDecimal(refueling.getAction().getPrice())));
        viewHolder.tvPrice.setText(text);
    }

    public class ViewHolder extends RealmViewHolder{

        public TextView tvFuelType, tvFuelPrice, tvQuantity, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            View viewColor = itemView.findViewById(R.id.view_row_refueling_vehicle_color);
            viewColor.setBackgroundColor(color);
            tvFuelType = (TextView) itemView.findViewById(R.id.tv_row_refueling_fuel_type);
            tvFuelPrice = (TextView) itemView.findViewById(R.id.tv_row_refueling_fuel_price);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_row_refueling_quantity);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_row_refueling_price);
        }
    }
}
