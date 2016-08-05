package edu.pdx.anb2.bluetooth;

public class BluetoothApplicationState {
    public final int illustration;
    public final boolean success;

    public BluetoothApplicationState(int illustration, boolean success) {
        this.illustration = illustration;
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothApplicationState that = (BluetoothApplicationState) o;
        return illustration == that.illustration && success == that.success;
    }

    @Override
    public int hashCode() {
        int result = illustration;
        result = 31 * result + (success ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BluetoothApplicationState{" +
                "illustration=" + illustration +
                ", success=" + success +
                '}';
    }
}
