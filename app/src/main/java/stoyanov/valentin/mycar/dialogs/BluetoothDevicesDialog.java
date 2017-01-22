package stoyanov.valentin.mycar.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class BluetoothDevicesDialog extends DialogFragment {

    private String[] devices;
    private OnDevicePickedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Devices")
            .setItems(devices, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int position) {
                    listener.onPicked(position);
                }
            });
        return super.onCreateDialog(savedInstanceState);
    }

    public void setDevices(String[] devices) {
        this.devices = devices;
    }

    public void setListener(OnDevicePickedListener listener) {
        this.listener = listener;
    }

    public interface OnDevicePickedListener {
        void onPicked(int position);
    }
}
