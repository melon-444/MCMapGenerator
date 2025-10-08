package com.melon.nbt;

public class NBTIntArray extends NBTElement<int[]> {

    public NBTIntArray(String keyName, int[] payLoad) {
        super(keyName, payLoad);
    }

    public NBTIntArray(int[] payLoad) {
        this(null, payLoad);
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[3 + keyNameLength + 4 + (4 * payLoad.length)];
        result[0] = NBTElement.Type.INT_ARRAY;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        int index = 3 + keyNameLength;
        int arrayLength = payLoad.length;
        result[index] = (byte) ((0xFF) & (arrayLength >> 24));
        result[index + 1] = (byte) ((0xFF) & (arrayLength >> 16));
        result[index + 2] = (byte) ((0xFF) & (arrayLength >> 8));
        result[index + 3] = (byte) ((0xFF) & (arrayLength));
        index += 4;
        for (int i = 0; i < payLoad.length; i++) {
            int value = payLoad[i];
            result[index] = (byte) ((0xFF) & (value >> 24));
            result[index + 1] = (byte) ((0xFF) & (value >> 16));
            result[index + 2] = (byte) ((0xFF) & (value >> 8));
            result[index + 3] = (byte) ((0xFF) & (value));
            index += 4;
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(keyName.isEmpty()?"":keyName+":");
        sb.append("[I;");
        if(payLoad!=null)
        for(int e:payLoad)
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
        for(int e:payLoad)
            sb.append(e+",");
        if(payLoad!=null&&payLoad.length!=0)
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

}
