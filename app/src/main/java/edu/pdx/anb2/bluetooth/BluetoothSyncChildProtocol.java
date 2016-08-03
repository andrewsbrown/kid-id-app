package edu.pdx.anb2.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by abrown on 8/1/2016.
 */
public class BluetoothSyncChildProtocol {
    private final DataInputStream in;
    private final DataOutputStream out;

    public BluetoothSyncChildProtocol(InputStream in, OutputStream out) {
        this.in = new DataInputStream(in);
        this.out = new DataOutputStream(out);
    }

    public BluetoothApplicationState read() throws IOException {
        int i = in.readInt();
        boolean b = in.readBoolean();
        return new BluetoothApplicationState(i, b);
    }

    public void write(BluetoothApplicationState state) throws IOException {
        out.writeInt(state.illustration);
        out.writeBoolean(state.success);
    }

    public void readIndefinitely(BluetoothApplicationState initialState) throws IOException {
        write(initialState);
        BluetoothApplicationState state;
        while((state = read()) != null) {
            write(state);
        }
    }
}
