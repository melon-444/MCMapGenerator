package com.melon.pixelize.nbt;


/**
 * Represents a boolean NBT tag, stored as a byte (0 for false, 1 for true).
 */
public class NBTBoolean extends NBTByte {
    public NBTBoolean(String name, boolean value) {
        super(name, (byte) (value ? 1 : 0));
    }

    public NBTBoolean(boolean value) {
        super(null, (byte) (value ? 1 : 0));
    }
    
    public NBTBoolean(Boolean value) {
        super(null, (byte) (value ? 1 : 0));
    }

    @Override
    public String toString(){
        return keyName.isEmpty()?payLoad.toString():keyName+":"+(payLoad==0?"false":"true");
    }
}
