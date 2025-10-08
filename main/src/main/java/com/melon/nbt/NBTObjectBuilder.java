package com.melon.nbt;

import java.lang.reflect.InvocationTargetException;

import com.melon.nbt.interfac3.Copyable;
import com.melon.nbt.interfac3.RootElement;

public class NBTObjectBuilder implements Copyable {

    private RootElement lastBuilt = null;

    private NBTObjectBuilder() {
    }

    /**
     * Create a new Compound NBT element with name
     *
     * @param rootName the name of the root compound.
     * @return this builder instance
     */
    public static NBTObjectBuilder buildCompound(String rootName) {
        NBTObjectBuilder b = new NBTObjectBuilder();
        b.lastBuilt = new NBTCompound(rootName);
        return b;
    }

    /**
     * Create a new Compound NBT element with no name
     *
     * @return this builder instance
     */
    public static NBTObjectBuilder buildCompound() {
        NBTObjectBuilder b = buildCompound("");
        return b;
    }

    public static NBTObjectBuilder buildList(String rootName) {
        NBTObjectBuilder b = new NBTObjectBuilder();
        b.lastBuilt = new NBTList(rootName);
        return b;
    }

    public static NBTObjectBuilder buildList() {
        NBTObjectBuilder b = buildList("");
        return b;
    }

    public NBTObjectBuilder Byte(String keyName, byte value) {
        checkBuilt();
        lastBuilt.addElement(new NBTByte(keyName, value));
        return this;
    }

    public NBTObjectBuilder Boolean(String keyName, boolean value) {
        checkBuilt();
        lastBuilt.addElement(new NBTBoolean(keyName, value));
        return this;
    }

    public NBTObjectBuilder Short(String keyName, short value) {
        checkBuilt();
        lastBuilt.addElement(new NBTShort(keyName, value));
        return this;
    }

    public NBTObjectBuilder Int(String keyName, int value) {
        checkBuilt();
        lastBuilt.addElement(new NBTInt(keyName, value));
        return this;
    }

    public NBTObjectBuilder Long(String keyName, long value) {
        checkBuilt();
        lastBuilt.addElement(new NBTLong(keyName, value));
        return this;
    }

    public NBTObjectBuilder Float(String keyName, float value) {
        checkBuilt();
        lastBuilt.addElement(new NBTFloat(keyName, value));
        return this;
    }

    public NBTObjectBuilder Double(String keyName, double value) {
        checkBuilt();
        lastBuilt.addElement(new NBTDouble(keyName, value));
        return this;
    }

    public NBTObjectBuilder String(String keyName, String value) {
        checkBuilt();
        lastBuilt.addElement(new NBTString(keyName, value));
        return this;
    }

    public NBTObjectBuilder ByteArray(String keyName, byte[] value) {
        checkBuilt();
        lastBuilt.addElement(new NBTByteArray(keyName, value));
        return this;
    }

    public NBTObjectBuilder IntArray(String keyName, int[] value) {
        checkBuilt();
        lastBuilt.addElement(new NBTIntArray(keyName, value));
        return this;
    }

    public NBTObjectBuilder LongArray(String keyName, long[] value) {
        checkBuilt();
        lastBuilt.addElement(new NBTLongArray(keyName, value));
        return this;
    }

    public NBTObjectBuilder Compound(String keyName, NBTElement<?>... values) {
        checkBuilt();
        NBTCompound value = new NBTCompound(keyName, values);
        lastBuilt.addElement(value);
        return this;
    }

    public NBTObjectBuilder directCompound(NBTCompound value) {
        checkBuilt();
        value.isTransmitByNet(false);
        lastBuilt.addElement(value);
        return this;
    }

    public NBTObjectBuilder List(String keyName, NBTElement<?>... values) {
        checkBuilt();
        NBTList value = new NBTList(keyName, values);
        lastBuilt.addElement(value);
        return this;
    }

    public NBTObjectBuilder directList(NBTList value) {
        checkBuilt();
        lastBuilt.addElement(value);
        return this;
    }

    public NBTObjectBuilder Byte(byte value) {
        checkBuilt();
        lastBuilt.addElement(new NBTByte(value));
        return this;
    }

    public NBTObjectBuilder Boolean(boolean value) {
        checkBuilt();
        lastBuilt.addElement(new NBTBoolean(value));
        return this;
    }

    public NBTObjectBuilder Short(short value) {
        checkBuilt();
        lastBuilt.addElement(new NBTShort(value));
        return this;
    }

    public NBTObjectBuilder Int(int value) {
        checkBuilt();
        lastBuilt.addElement(new NBTInt(value));
        return this;
    }

    public NBTObjectBuilder Long(long value) {
        checkBuilt();
        lastBuilt.addElement(new NBTLong(value));
        return this;
    }

    public NBTObjectBuilder Float(float value) {
        checkBuilt();
        lastBuilt.addElement(new NBTFloat(value));
        return this;
    }

    public NBTObjectBuilder Double(double value) {
        checkBuilt();
        lastBuilt.addElement(new NBTDouble(value));
        return this;
    }

    public NBTObjectBuilder String(String value) {
        checkBuilt();
        lastBuilt.addElement(new NBTString(value));
        return this;
    }

    public NBTObjectBuilder ByteArray(byte[] value) {
        checkBuilt();
        lastBuilt.addElement(new NBTByteArray(value));
        return this;
    }

    public NBTObjectBuilder IntArray(int[] value) {
        checkBuilt();
        lastBuilt.addElement(new NBTIntArray(value));
        return this;
    }

    public NBTObjectBuilder LongArray(long[] value) {
        checkBuilt();
        lastBuilt.addElement(new NBTLongArray(value));
        return this;
    }

    public NBTObjectBuilder anonymousCompound(NBTElement<?>... values) {
        checkBuilt();
        NBTCompound value = new NBTCompound(values);
        lastBuilt.addElement(value);
        return this;
    }

    public NBTObjectBuilder anonymousList(NBTElement<?>... values) {
        checkBuilt();
        NBTList value = new NBTList(values);
        lastBuilt.addElement(value);
        return this;
    }

    public NBTObjectBuilder delete(String keyName) {
        checkBuilt();
        lastBuilt.removeElement(keyName);
        return this;
    }

    public NBTObjectBuilder delete(NBTElement<?> element) {
        checkBuilt();
        lastBuilt.removeElement(element);
        return this;
    }

    public NBTObjectBuilder set(String keyName, NBTElement<?> element) {
        checkBuilt();
        lastBuilt.removeElement(keyName);
        element.setKeyName(keyName);
        lastBuilt.addElement(element);
        return this;
    }

    public <T> NBTObjectBuilder set(String keyName, Class<? extends NBTElement<T>> elementType, T value) {
        checkBuilt();
        lastBuilt.removeElement(keyName);
        NBTElement<T> instance = null;
        try {
            instance = elementType.getDeclaredConstructor(value.getClass()).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        instance.setKeyName(keyName);
        lastBuilt.addElement(instance);
        return this;
    }

    public NBTElement<?> end() {
        checkBuilt();
        if (lastBuilt instanceof RootElement result) {
            lastBuilt = null;
            return (NBTElement<?>) result;
        }
        throw new IllegalStateException("The result element isn't a root Element.");
    }

    public NBTCompound endCompound() {
        checkBuilt();
        if (lastBuilt instanceof NBTCompound result) {
            lastBuilt = null;
            return result;
        }
        throw new IllegalStateException("The result element isn't a Compound Element.");
    }

    public NBTCompound toCompound() {
        checkBuilt();
        if (lastBuilt instanceof NBTCompound result) {
            return result;
        }
        throw new IllegalStateException("The result element isn't a Compound Element.");
    }

    public NBTList endList() {
        checkBuilt();
        if (lastBuilt instanceof NBTList result) {
            lastBuilt = null;
            return result;
        }
        throw new IllegalStateException("The result element isn't a List Element.");
    }

    public NBTList toList() {
        checkBuilt();
        if (lastBuilt instanceof NBTList result) {
            return result;
        }
        throw new IllegalStateException("The result element isn't a List Element.");
    }

    private void checkBuilt() {
        if (lastBuilt == null)
            throw new IllegalStateException("Call build() before creating a new compound element");
    }

    @Override
    public NBTObjectBuilder copy() {
        NBTObjectBuilder copyBuilder = new NBTObjectBuilder();
        RootElement copyLastBuilt = null;

        if (lastBuilt instanceof NBTCompound result) {
            copyLastBuilt = (RootElement)(NBTCompound)result.copy();
        } else if (lastBuilt instanceof NBTList result) {
            copyLastBuilt = (RootElement)(NBTList)result.copy();
        }

        copyBuilder.lastBuilt = copyLastBuilt;
        return copyBuilder;
    }
}
