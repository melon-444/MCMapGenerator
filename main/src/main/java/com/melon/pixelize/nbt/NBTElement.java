package com.melon.pixelize.nbt;

import java.nio.ByteBuffer;

/**
 * This is a recoding of Minecraft Java Edition Named Binary Tag (NBT) element
 * 
 * @param <T> The type of the payload
 * @author melon_444
 */
public abstract class NBTElement<T> {
    protected byte Type_ID;

    protected short keyNameLength;

    protected String keyName;

    protected T payLoad;

    public abstract byte[] toBytes();

    public NBTElement() {
        this.keyName = "";
        this.keyNameLength = 0;
    }

    public NBTElement(T value) {
        this.keyName = "";
        this.keyNameLength = 0;
        this.payLoad = value;
    }

    public NBTElement(String keyName, T value) {
        if (keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.getBytes().length;
        this.payLoad = value;
    }

    /**
     * Convert a byte array to an NBT element.
     * 
     * @param NBTbytes The byte array to convert.
     * @return The NBT element.
     */
    public static NBTElement<?> asNBT(byte[] NBTbytes) {
        return NBTDecoder.decode(ByteBuffer.wrap(NBTbytes));
    }

    public static NBTElement<?> asNBT(String SNBT) {
        return SNBTDecoder.decode(SNBT);
    }

    public byte getType() {
        return toBytes()[0];
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
        keyNameLength = (short) keyName.getBytes().length;
    }

    public T getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(T payLoad) {
        this.payLoad = payLoad;
    }

    public String toString() {
        return keyName.isEmpty()?payLoad.toString():keyName+":"+payLoad.toString();
    }

    public String toJsonString(){
        
        String result = new NBTString(keyName)+":"+payLoad.toString();
        return result;
    }

    public static class Type {
        public static final byte END = 0;
        public static final byte BYTE = 1;
        public static final byte BOOLEAN = BYTE;
        public static final byte SHORT = 2;
        public static final byte INT = 3;
        public static final byte LONG = 4;
        public static final byte FLOAT = 5;
        public static final byte DOUBLE = 6;
        public static final byte BYTE_ARRAY = 7;
        public static final byte STRING = 8;
        public static final byte LIST = 9;
        public static final byte COMPOUND = 10;
        public static final byte INT_ARRAY = 11;
        public static final byte LONG_ARRAY = 12;
    }
}
