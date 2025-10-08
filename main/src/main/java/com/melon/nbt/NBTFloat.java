package com.melon.nbt;

public class NBTFloat extends NBTElement<Float> {

    public NBTFloat(String keyName, float value) {
        if(keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.getBytes().length;
        this.payLoad = value;
    }

    public NBTFloat(String keyName, Float value) {
        super(keyName, value);
    }

    public NBTFloat(float value) {
        this(null, value);
    }

    public NBTFloat(Float value) {
        super(value);
    }

    @Override
    public byte[] toBytes() {
        int intBits = Float.floatToIntBits(payLoad);
        byte[] result = new byte[3 + 4 + keyNameLength];
        result[0] = NBTElement.Type.FLOAT;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = (byte) ((0xFF) & intBits >> 24);
        result[4 + keyNameLength] = (byte) ((0xFF) & intBits >> 16);
        result[5 + keyNameLength] = (byte) ((0xFF) & intBits >> 8);
        result[6 + keyNameLength] = (byte) ((0xFF) & intBits);
        return result;
    }

    @Override
    public String toString(){
        return super.toString()+"f";
    }

}
