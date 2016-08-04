package edu.pdx.anb2.bluetooth;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class BluetoothSyncProtocol {
    private DataInputStream in;
    private DataOutputStream out;
    private BluetoothApplicationState currentState;
    private OnChange observer;

    public interface OnChange {
        void onChange(BluetoothApplicationState oldState, BluetoothApplicationState newState);
    }

    public void onNewConnection(InputStream in, OutputStream out) {
        this.in = new DataInputStream(in);
        this.out = new DataOutputStream(out);
    }

    public void onChange(OnChange observer) {
        this.observer = observer;
    }

    public void listenForStateChange() throws IOException {
        BluetoothApplicationState newState = read();
        BluetoothApplicationState oldState = currentState;
        synchronized (BluetoothSyncProtocol.this) {
            currentState = newState;
        }
        triggerObserver(oldState, newState);
    }

    public BluetoothApplicationState read() throws IOException {
        verifyConnection();
        int i = in.readInt();
        boolean b = in.readBoolean();
        return new BluetoothApplicationState(i, b);
    }

    public void write(BluetoothApplicationState newState) throws IOException {
        verifyConnection();
        BluetoothApplicationState oldState = currentState;
        synchronized (this) {
            out.writeInt(newState.illustration);
            out.writeBoolean(newState.success);
            currentState = newState;
        }
        triggerObserver(oldState, newState);
    }

    void triggerObserver(BluetoothApplicationState oldState, BluetoothApplicationState newState) {
        if (observer != null) {
            observer.onChange(oldState, newState);
        }
    }

    void verifyConnection() throws IOException {
        if (in == null || out == null) {
            throw new IOException("No connected stream available");
        }
    }
}
