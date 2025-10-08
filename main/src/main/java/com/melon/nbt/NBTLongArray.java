package com.melon.nbt;

public class NBTLongArray extends NBTElement<long[]> {

    public NBTLongArray(String keyName, long[] payLoad) {
        super(keyName, payLoad);
    }

    public NBTLongArray(long[] value) {
        this(null, value);
    }

    @Override
    public byte[] toBytes() {
        int arrayLength = payLoad.length;
        byte[] result = new byte[3 + keyNameLength + 4 + (8 * arrayLength)];
        result[0] = NBTElement.Type.LONG_ARRAY;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = (byte) ((0xFF) & arrayLength >> 24);
        result[4 + keyNameLength] = (byte) ((0xFF) & arrayLength >> 16);
        result[5 + keyNameLength] = (byte) ((0xFF) & arrayLength >> 8);
        result[6 + keyNameLength] = (byte) ((0xFF) & arrayLength);
        for (int i = 0; i < arrayLength; i++) {
            long value = payLoad[i];
            int baseIndex = 7 + keyNameLength + (i * 8);
            result[baseIndex] = (byte) ((0xFF) & value >> 56);
            result[baseIndex + 1] = (byte) ((0xFF) & value >> 48);
            result[baseIndex + 2] = (byte) ((0xFF) & value >> 40);
            result[baseIndex + 3] = (byte) ((0xFF) & value >> 32);
            result[baseIndex + 4] = (byte) ((0xFF) & value >> 24);
            result[baseIndex + 5] = (byte) ((0xFF) & value >> 16);
            result[baseIndex + 6] = (byte) ((0xFF) & value >> 8);
            result[baseIndex + 7] = (byte) ((0xFF) & value);
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(keyName.isEmpty()?"":keyName+":");
        sb.append("[L;");
        if(payLoad!=null)
        for(long e:payLoad)
            sb.append(e+",");
        if(payLoad!=null&&payLoad.length!=0)
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toJsonString(){
        StringBuilder sb = new StringBuilder(keyName.isEmpty()?"":new NBTString(keyName)+":");
        sb.append("[");
        if(payLoad!=null)
        for(long e:payLoad)
            sb.append(e+",");
        if(payLoad!=null&&payLoad.length!=0)
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

}
