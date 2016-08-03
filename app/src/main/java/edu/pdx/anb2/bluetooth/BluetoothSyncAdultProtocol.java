package edu.pdx.anb2.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothSyncAdultProtocol {
    private final DataInputStream in;
    private final DataOutputStream out;

    public BluetoothSyncAdultProtocol(InputStream in, OutputStream out) {
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

    public void writeAndRetry(BluetoothApplicationState state) throws IOException {
        write(state);
        while(!read().equals(state))
            write(state);
    }
}
