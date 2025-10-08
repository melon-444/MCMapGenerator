package com.melon.nbt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.melon.nbt.NBTElement.Type;

public class NBTDecoder {

    protected static NBTElement<?> decode(ByteBuffer buf) {
        byte type = buf.get(); // 读类型
        if (type == Type.END) return null;

        String key = readKey(buf);
        return decodePayload(type, key, buf);
    }

    private static String readKey(ByteBuffer buf) {
        // NBT key 是一个短整型表示长度，后跟 UTF-8 字节
        short keyLen = buf.getShort();
        byte[] keyBytes = new byte[keyLen];
        buf.get(keyBytes);
        return new String(keyBytes, StandardCharsets.UTF_8);
    }

    private static NBTElement<?> decodePayload(byte type, String key, ByteBuffer buf) {
        switch (type) {
            case Type.BYTE:
                return new NBTByte(key, buf.get());
            case Type.SHORT:
                return new NBTShort(key, buf.getShort());
            case Type.INT:
                return new NBTInt(key, buf.getInt());
            case Type.LONG:
                return new NBTLong(key, buf.getLong());
            case Type.FLOAT:
                return new NBTFloat(key, buf.getFloat());
            case Type.DOUBLE:
                return new NBTDouble(key, buf.getDouble());
            case Type.BYTE_ARRAY: {
                int len = buf.getInt();
                byte[] arr = new byte[len];
                buf.get(arr);
                return new NBTByteArray(key, arr);
            }
            case Type.STRING: {
                short len = buf.getShort();
                byte[] strBytes = new byte[len];
                buf.get(strBytes);
                return new NBTString(key, new String(strBytes, StandardCharsets.UTF_8));
            }
            case Type.LIST: {
                byte elemType = buf.get();
                int len = buf.getInt();
                List<NBTElement<?>> elements = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    elements.add(decodePayload(elemType, "", buf));
                }
                return new NBTList(key, elements);
            }
            case Type.COMPOUND: {
                List<NBTElement<?>> elements = new ArrayList<>();
                while (true) {
                    byte nestedType = buf.get();
                    if (nestedType == Type.END) break;
                    String nestedKey = readKey(buf);
                    elements.add(decodePayload(nestedType, nestedKey, buf));
                }
                return new NBTCompound(key, elements);
            }
            case Type.INT_ARRAY: {
                int len = buf.getInt();
                int[] arr = new int[len];
                for (int i = 0; i < len; i++) arr[i] = buf.getInt();
                return new NBTIntArray(key, arr);
            }
            case Type.LONG_ARRAY: {
                int len = buf.getInt();
                long[] arr = new long[len];
                for (int i = 0; i < len; i++) arr[i] = buf.getLong();
                return new NBTLongArray(key, arr);
            }
            default:
                throw new IllegalArgumentException("Unsupported NBT type: " + type);
        }
    }
}
