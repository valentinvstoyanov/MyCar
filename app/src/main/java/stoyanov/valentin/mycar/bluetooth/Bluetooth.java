package stoyanov.valentin.mycar.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import java.util.Set;

public class Bluetooth {

    public final static int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private Activity activity;
    private Set<BluetoothDevice> pairedDevices;

    public Bluetooth(Activity activity) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
    }

    public void enable() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
