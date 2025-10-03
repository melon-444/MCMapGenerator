package com.melon.pixelize.nbt;

public class NBTLong extends NBTElement<Long> {

    public NBTLong(String keyName, long value) {
        if(keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.getBytes().length;
        this.payLoad = value;
    }

    public NBTLong(long value) {
        this(null, value);
    }

    public NBTLong(Long value) {
        this(null, value);
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[3 + 8 + keyNameLength];
        result[0] = NBTElement.Type.LONG;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = (byte) ((0xFF) & payLoad >> 56);
        result[4 + keyNameLength] = (byte) ((0xFF) & payLoad >> 48);
        result[5 + keyNameLength] = (byte) ((0xFF) & payLoad >> 40);
        result[6 + keyNameLength] = (byte) ((0xFF) & payLoad >> 32);
        result[7 + keyNameLength] = (byte) ((0xFF) & payLoad >> 24);
        result[8 + keyNameLength] = (byte) ((0xFF) & payLoad >> 16);
        result[9 + keyNameLength] = (byte) ((0xFF) & payLoad >> 8);
        result[10 + keyNameLength] = (byte) ((0xFF) & payLoad);
        return result;
    }

    @Override
    public String toString(){
        return super.toString()+"l";
    }

}
