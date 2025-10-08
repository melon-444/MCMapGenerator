package com.melon.nbt;

public class NBTByteArray extends NBTElement<byte[]> {

    public NBTByteArray(String keyName, byte[] payLoad) {
        super(keyName, payLoad);
    }

    public NBTByteArray(byte[] payLoad) {
        this(null, payLoad);
    }

    @Override
    public byte[] toBytes() {
        int length = payLoad.length;
        byte[] result = new byte[3 + keyNameLength + 4 + length];
        result[0] = NBTElement.Type.BYTE_ARRAY;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        result[3 + keyNameLength] = (byte) ((0xFF) & length >> 24);
        result[4 + keyNameLength] = (byte) ((0xFF) & length >> 16);
        result[5 + keyNameLength] = (byte) ((0xFF) & length >> 8);
        result[6 + keyNameLength] = (byte) ((0xFF) & length);
        System.arraycopy(payLoad, 0, result, 7 + keyNameLength, length);
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(keyName.isEmpty()?"":keyName+":");
        sb.append("[B;");
        if(payLoad!=null)
        for(byte e:payLoad)
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
        for(byte e:payLoad)
            sb.append(e+",");
        if(payLoad!=null&&payLoad.length!=0)
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

}
