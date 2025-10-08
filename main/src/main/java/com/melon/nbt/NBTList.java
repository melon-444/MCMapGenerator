package com.melon.nbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.melon.nbt.interfac3.RootElement;

public class NBTList extends NBTElement<List<NBTElement<?>>> implements RootElement {

    private byte type = 0;

    private void checkType() {
        type = payLoad != null && payLoad.size() > 0 ? payLoad.get(0).getType() : 0;
    }

    private static int elementPayLoadOffset(byte[] input, int offset) {
        if (input[offset] == Type.END)
            return -1;// get End
        int keyNameLength = (0xFF & input[offset + 1] << 8) | (0xFF & input[offset + 2]);
        return offset + 3 + keyNameLength;
    }

    public NBTList(String keyName, List<NBTElement<?>> value) {
        if (keyName == null)
            keyName = "";
        this.keyName = keyName;
        this.keyNameLength = (short) keyName.getBytes().length;

        boolean legalPayload = true;
        for (NBTElement<?> element : value)
            element.setKeyName("");
        for (NBTElement<?> element : value) {
            if (element.getType() != value.get(0).getType())
                legalPayload = false;
        }

        if (legalPayload) {
            this.payLoad = new ArrayList<>(value);
            checkType();
        } else
            throw new IllegalArgumentException("The element type of a list should keep same.");
    }

    public NBTList(String keyName, NBTElement<?>... value) {
        this(keyName, Arrays.asList(value));
    }

    public NBTList(NBTElement<?>... value) {
        this("", Arrays.asList(value));
    }

    public NBTList(List<NBTElement<?>> value) {
        this("", new ArrayList<>());
    }

    public NBTList(String keyName) {
        this(keyName, new ArrayList<>());
    }

    @Override
    public void setPayLoad(List<NBTElement<?>> payLoad) {
        boolean legalPayload = true;
        for (NBTElement<?> element : payLoad) {
            if (element.getType() != payLoad.get(0).getType())
                legalPayload = false;
        }

        if (legalPayload) {
            this.payLoad = payLoad;
            checkType();
        } else
            throw new IllegalArgumentException("The element type of a list should keep same.");

    }

    public void addElement(NBTElement<?> element) {
        if (payLoad.size() == 0 || payLoad.get(0).getType() == element.getType()) {
            this.payLoad.add(element);
            checkType();
        } else
            throw new IllegalArgumentException("The element type of a list should keep same.");
    }

    public boolean removeElement(NBTElement<?> element) {
        boolean success = this.payLoad.remove(element);
        checkType();
        return success;

    }

    public boolean removeElement(String keyname) {
        Iterator<NBTElement<?>> it = payLoad.iterator();
        while (it.hasNext()) {
            NBTElement<?> element = it.next();
            if (element.getKeyName().equals(keyname)) {
                it.remove();
                checkType();
                return true;
            }
        }
        return false;
    }

    @Override
    public byte[] toBytes() {
        int totalLength = 3 + keyNameLength + 5; // Type + Key Length + Key Name + Element Type + List Length
        if (payLoad != null)
            for (NBTElement<?> element : payLoad) {
                totalLength += (element.toBytes().length - 3 - element.getKeyName().getBytes().length);
            }
        byte[] result = new byte[totalLength];
        result[0] = NBTElement.Type.LIST;
        result[1] = (byte) ((0xFF) & keyNameLength >> 8);
        result[2] = (byte) ((0xFF) & keyNameLength);
        System.arraycopy(keyName.getBytes(), 0, result, 3, keyNameLength);
        int index = 3 + keyNameLength;
        if (payLoad != null && payLoad.size() > 0) {
            result[index++] = (byte) ((0xFF) & type);
        } else {
            result[index++] = NBTEnd.getEnd(); // Empty list
            return result;
        }
        result[index++] = (byte) ((0xFF) & (payLoad.size() >> 24));
        result[index++] = (byte) ((0xFF) & (payLoad.size() >> 16));
        result[index++] = (byte) ((0xFF) & (payLoad.size() >> 8));
        result[index++] = (byte) ((0xFF) & payLoad.size());
        for (NBTElement<?> element : payLoad) {
            byte[] elementBytes = element.toBytes();
            int offset = elementPayLoadOffset(elementBytes, 0);
            int actual_len = elementBytes.length - offset;
            System.arraycopy(elementBytes, offset, result, index, actual_len);// only payload
            index += actual_len;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(keyName.isEmpty() ? "" : keyName + ":");
        sb.append("[");
        if (payLoad != null && !payLoad.isEmpty()) {
            for (NBTElement<?> e : payLoad)
                sb.append(e + ",");
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toJsonString() {
        StringBuilder sb = new StringBuilder(keyName.isEmpty() ? "" : new NBTString(keyName) + ":");
        sb.append("[");
        if (payLoad != null && !payLoad.isEmpty()) {
            for (NBTElement<?> e : payLoad)
                sb.append(e.toJsonString() + ",");
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

}
