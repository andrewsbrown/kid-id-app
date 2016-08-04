package edu.pdx.anb2;

import android.bluetooth.BluetoothDevice;

class DeviceSpinnerItem {
    public final String id;
    public final String name;
    public final BluetoothDevice device;

    public DeviceSpinnerItem(BluetoothDevice device) {
        this.device = device;
        this.id = device.getAddress();
        this.name = device.getName();
    }

    @Override
    public String toString() {
        return name + "\n" + id;
    }
}
