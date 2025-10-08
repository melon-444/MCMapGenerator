package com.melon.nbt;

public class NBTShort extends NBTElement<Short> {

    public NBTShort(String keyName, short value) {
        if(keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.getBytes().length;
        this.payLoad = value;
    }
    
    public NBTShort(String keyName, Short value) {
        super(keyName, value);
    }

    public NBTShort(short value) {
        this(null, value);
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[3 + 2 + keyNameLength];
        result[0] = NBTElement.Type.SHORT;
        result[1] = (byte)((0xFF)&keyNameLength>>8);
        result[2] = (byte)((0xFF)&keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = (byte)(0xFF&payLoad>>8);
        result[4 + keyNameLength] = (byte)(0xFF&payLoad);
        return result;
    }

    @Override
    public String toString(){
        return super.toString()+"s";
    }

}
