package com.melon.nbt;

public class NBTByte extends NBTElement<Byte> {

    public NBTByte(String keyName, byte value) {
        if(keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.length();
        this.payLoad = value;
    }

    public NBTByte(String keyName, Byte value) {
        super(keyName, value);
    }

    public NBTByte(byte value) {
        this(null, value);
    }

    public NBTByte(Byte value) {
        super(value);
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[3 + 1 + keyNameLength];
        result[0] = NBTElement.Type.BYTE;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = payLoad;
        return result;
    }

    @Override
    public String toString(){
        return super.toString()+"b";
    }

}
