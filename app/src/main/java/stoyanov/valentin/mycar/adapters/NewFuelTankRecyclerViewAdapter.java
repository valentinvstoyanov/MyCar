package stoyanov.valentin.mycar.adapters;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import stoyanov.valentin.mycar.R;

public class NewFuelTankRecyclerViewAdapter
        extends RecyclerView.Adapter<NewFuelTankRecyclerViewAdapter.ViewHolder>{

    int size;

    public NewFuelTankRecyclerViewAdapter(int size) {
        this.size = size;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Spinner spnFuelType;
        public TextInputLayout tilCapacity;
        public TextInputLayout tilConsumption;

        public ViewHolder(View itemView) {
            super(itemView);
            this.spnFuelType = (Spinner) itemView.findViewById(R.id.spn_row_ft_fuel_type);
            this.tilCapacity = (TextInputLayout) itemView.findViewById(R.id.til_row_ft_capacity);
            this.tilConsumption = (TextInputLayout) itemView.findViewById(R.id.til_row_ft_consumption);
        }
    }
}
