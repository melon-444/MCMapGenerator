package com.melon.nbt;

public class NBTEnd extends NBTElement<Void> {

    @Deprecated(since = "Use the static method getEnd() instead", forRemoval = true)
    public NBTEnd() {
        this.keyName = "";
        this.keyNameLength = 0;
        this.payLoad = null;
    }

    public static byte getEnd() {
        return (byte) NBTElement.Type.END;
    }

    @Override
    public byte[] toBytes() {
        return new byte[] { NBTElement.Type.END };
    }

    @Override
    public String toString(){
        return "";
    }

    @Override
    public String toJsonString(){
        return "";
    }

}
