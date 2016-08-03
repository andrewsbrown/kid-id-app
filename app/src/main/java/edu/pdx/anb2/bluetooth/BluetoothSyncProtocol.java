package edu.pdx.anb2.bluetooth;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothSyncProtocol {
    private boolean listening = true;
    private final DataInputStream in;
    private final DataOutputStream out;
    private BluetoothApplicationState currentState;
    private OnChange observer;

    public interface OnChange {
        void onChange(BluetoothApplicationState oldState, BluetoothApplicationState newState);
    }

    public BluetoothSyncProtocol(InputStream in, OutputStream out) {
        this.in = new DataInputStream(in);
        this.out = new DataOutputStream(out);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (listening) {
                    try {
                        BluetoothApplicationState newState = read();
                        BluetoothApplicationState oldState = currentState;
                        synchronized (BluetoothSyncProtocol.this) {
                            currentState = newState;
                        }
                        triggerObserver(oldState, newState);
                    } catch (IOException e) {
                        Log.e(BluetoothApplicationState.class.getSimpleName(), "Failed while reading Bluetooth", e);
                    }
                }
            }
        }).start();
    }

    public void stop() {
        listening = false;
    }

    public BluetoothApplicationState read() throws IOException {
        int i = in.readInt();
        boolean b = in.readBoolean();
        return new BluetoothApplicationState(i, b);
    }

    public void write(BluetoothApplicationState newState) throws IOException {
        BluetoothApplicationState oldState = currentState;
        synchronized (this) {
            out.writeInt(newState.illustration);
            out.writeBoolean(newState.success);
            currentState = newState;
        }
        triggerObserver(oldState, newState);
    }

    public void onChange(OnChange observer) throws IOException {
        this.observer = observer;
    }

    void triggerObserver(BluetoothApplicationState oldState, BluetoothApplicationState newState) {
        if (observer != null) {
            observer.onChange(oldState, newState);
        }
    }
}
