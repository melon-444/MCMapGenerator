package com.melon.pixelize.nbt;

public class NBTDouble extends NBTElement<Double> {

    public NBTDouble(String keyName, double value) {
        if(keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.getBytes().length;
        this.payLoad = value;
    }

    public NBTDouble(double value) {
        this(null, value);
    }

    public NBTDouble(Double value) {
        super(value);
    }

    @Override
    public byte[] toBytes() {
        long longBits = Double.doubleToLongBits(payLoad);
        byte[] result = new byte[3 + 8 + keyNameLength];
        result[0] = NBTElement.Type.DOUBLE;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = (byte) ((0xFF) & longBits >> 56);
        result[4 + keyNameLength] = (byte) ((0xFF) & longBits >> 48);
        result[5 + keyNameLength] = (byte) ((0xFF) & longBits >> 40);
        result[6 + keyNameLength] = (byte) ((0xFF) & longBits >> 32);
        result[7 + keyNameLength] = (byte) ((0xFF) & longBits >> 24);
        result[8 + keyNameLength] = (byte) ((0xFF) & longBits >> 16);
        result[9 + keyNameLength] = (byte) ((0xFF) & longBits >> 8);
        result[10 + keyNameLength] = (byte) ((0xFF) & longBits);
        return result;
    }

    @Override
    public String toString(){
        return super.toString()+"d";
    }

}
