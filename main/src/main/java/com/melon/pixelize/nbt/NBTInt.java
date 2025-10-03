package com.melon.pixelize.nbt;

public class NBTInt extends NBTElement<Integer> {

    public NBTInt(String keyName, int value) {
        if(keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.getBytes().length;
        this.payLoad = value;
    }

    public NBTInt(int value) {
        this(null, value);
    }

    public NBTInt(Integer value) {
        super(value);
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[3 + 4 + keyNameLength];
        result[0] = NBTElement.Type.INT;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = (byte) ((0xFF) & payLoad >> 24);
        result[4 + keyNameLength] = (byte) ((0xFF) & payLoad >> 16);
        result[5 + keyNameLength] = (byte) ((0xFF) & payLoad >> 8);
        result[6 + keyNameLength] = (byte) ((0xFF) & payLoad);
        return result;
    }

}
