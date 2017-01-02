package stoyanov.valentin.mycar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.FuelTank;

public class ViewVehicleRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> titles;
    private String[] values;
    private FuelTank[] fuelTanks;
    private OnRecyclerViewItemClickListener listener;

    public ViewVehicleRecyclerViewAdapter(Context context,
                                          ArrayList<String> titles,
                                          String[] values, FuelTank[] fuelTanks) {
        this.context = context;
        this.titles = titles;
        this.values = values;
        this.fuelTanks = fuelTanks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.row_view_vehicle, parent, false);
            return new ViewHolder(view);
        }else {
            view = inflater.inflate(R.layout.row_view_fuel_tank, parent, false);
            return new ViewHolderFuelTank(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tvTitle.setText(titles.get(position));
            int elementPosition = values.length;
            if (position >= elementPosition) {
                elementPosition--;
            }else {
                elementPosition = position;
            }
            viewHolder.tvValue.setText(values[elementPosition]);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(position, viewHolder.tvTitle.getText().toString());
                }
            });
        }else {
            ViewHolderFuelTank viewHolderFuelTank = (ViewHolderFuelTank) holder;
            int elementPosition = titles.indexOf(titles.get(position)) + 1 - values.length;
            FuelTank fuelTank = fuelTanks[elementPosition];
            String text = String.format(context.getString(R.string.fuel_type_placeholder),
                            fuelTank.getFuelType().getName());
            viewHolderFuelTank.tvFuelType.setText(text);
            text = String.format(context.getString(R.string.capacity_placeholder),
                    fuelTank.getCapacity());
            viewHolderFuelTank.tvCapacity.setText(text);
            text = String.format(context.getString(R.string.consumption_placeholder),
                    fuelTank.getConsumption());
            viewHolderFuelTank.tvConsumption.setText(text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (titles.get(position).startsWith("ft")) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvValue;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tv_row_view_vehicle_title);
            this.tvValue = (TextView) itemView.findViewById(R.id.tv_row_view_vehicle_value);
        }
    }

    public class ViewHolderFuelTank extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView tvFuelType;
        public TextView tvCapacity;
        public TextView tvConsumption;
        public ViewHolderFuelTank(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgv_row_view_ft_fuel_type);
            tvFuelType = (TextView) itemView.findViewById(R.id.tv_row_view_ft_fuel_type);
            tvCapacity = (TextView) itemView.findViewById(R.id.tv_row_view_ft_capacity);
            tvConsumption = (TextView) itemView.findViewById(R.id.tv_row_view_ft_consumption);
        }
    }

    public void changeValue(int position, String value) {
        int elementPosition = values.length;
        if (position >= elementPosition) {
            elementPosition--;
        }else {
            elementPosition = position;
        }
        values[elementPosition] = value;
        notifyItemChanged(position);
    }

    public void setListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnRecyclerViewItemClickListener{
        void onClick(int position, String title);
    }
}
