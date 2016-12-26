package stoyanov.valentin.mycar.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stoyanov.valentin.mycar.R;

public class ViewVehicleRecyclerviewAdapter
        extends RecyclerView.Adapter<ViewVehicleRecyclerviewAdapter.ViewHolder> {

    private String[] titles;
    private String[] values;

    public ViewVehicleRecyclerviewAdapter(String[] titles, String[] values) {
        this.titles = titles;
        this.values = values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_view_vehicle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewVehicleRecyclerviewAdapter.ViewHolder holder, int position) {
        holder.tvTitle.setText(titles[position]);
        holder.tvValue.setText(values[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvValue;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tv_row_view_vehicle_title);
            this.tvValue = (TextView) itemView.findViewById(R.id.tv_row_view_vehicle_value);
        }
    }
}
