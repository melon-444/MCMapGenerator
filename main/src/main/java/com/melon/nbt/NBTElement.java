package com.melon.nbt;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import com.melon.nbt.interfac3.Copyable;

/**
 * This is a recoding of Minecraft Java Edition Named Binary Tag (NBT) element
 * 
 * @param <T> The type of the payload
 * @author melon_444
 */
public abstract class NBTElement<T> implements Copyable {
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
        return keyName.isEmpty() ? payLoad.toString() : keyName + ":" + payLoad.toString();
    }

    public String toJsonString() {

        String result = new NBTString(keyName) + ":" + payLoad.toString();
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

    @SuppressWarnings("unchecked")
    @Override
    public NBTElement<T> copy() {
        T obj = payLoad;
        java.lang.reflect.Type supertype = this.getClass().getGenericSuperclass();
        Class<?> actuaType = getClassFromType(((ParameterizedType)supertype).getActualTypeArguments()[0]);

        //System.out.println("supertype: "+supertype+"\nactuaType: "+actuaType);

        try {

            // Don't copy box type and String
            if (obj instanceof Number || obj instanceof String || obj instanceof Boolean
                    || obj instanceof Character) {
                return (NBTElement<T>) this.getClass().getDeclaredConstructor(String.class, actuaType)
                        .newInstance(keyName, obj);
            }

            // Array copy
            if (obj.getClass().isArray()) {
                int length = Array.getLength(obj);
                Object copy = Array.newInstance(obj.getClass().getComponentType(), length);
                for (int i = 0; i < length; i++) {
                    Object elem = Array.get(obj, i);
                    if (elem instanceof Copyable e)
                        Array.set(copy, i, e.copy()); // recursion deep copy
                    else {
                        Method m = obj.getClass().getDeclaredMethod("clone");
                        m.setAccessible(true);
                        Array.set(copy, i, m.invoke(obj));
                    }
                }
                return (NBTElement<T>) this.getClass().getDeclaredConstructor(String.class, actuaType)
                        .newInstance(keyName, (T) copy);
            }

            // Collection copy
            if (obj instanceof java.util.Collection) {
                java.util.Collection<?> col = (java.util.Collection<?>) obj;
                java.util.Collection<Object> copy = col instanceof java.util.List ? new java.util.ArrayList<>()
                        : new java.util.HashSet<>();
                for (Object elem : col) {
                    if (elem instanceof Copyable e)
                        copy.add(e.copy()); // recursion deep copy
                    else {
                        Method m = obj.getClass().getDeclaredMethod("clone");
                        m.setAccessible(true);
                        copy.add(m.invoke(obj));
                    }
                }
                return (NBTElement<T>) this.getClass().getDeclaredConstructor(String.class, actuaType)
                        .newInstance(keyName, (T) copy);
            }

            // if it use Copyable interface,use the method itself has.
            if (obj instanceof Copyable c) {
                return (NBTElement<T>) this.getClass().getDeclaredConstructor(String.class, actuaType)
                        .newInstance(keyName, c.copy());
            }

            // else try clone()
            Method m = obj.getClass().getDeclaredMethod("clone");
            m.setAccessible(true);
            return (NBTElement<T>) this.getClass().getDeclaredConstructor(String.class, actuaType)
                    .newInstance(keyName, (T) m.invoke(obj));
        } catch (Exception e) {
            // fallback
            throw new UnsupportedOperationException("Cannot copy " + obj.getClass(), e);
        }
    }

        private static Class<?> getClassFromType(java.lang.reflect.Type type) {
        if (type instanceof Class<?> cls) {
            return cls; // 普通类
        } else if (type instanceof ParameterizedType pt) {
            // 泛型类型，取原始类型
            java.lang.reflect.Type rawType = pt.getRawType();
            if (rawType instanceof Class<?> rawCls) {
                return rawCls;
            }
        } else if (type instanceof GenericArrayType gat) {
            // 泛型数组，递归获取数组元素类型
            Class<?> componentClass = getClassFromType(gat.getGenericComponentType());
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            }
        } else if (type instanceof java.lang.reflect.TypeVariable<?> tv) {
            // 类型变量，尝试获取上界（默认 Object）
            java.lang.reflect.Type[] bounds = tv.getBounds();
            if (bounds.length > 0) {
                return getClassFromType(bounds[0]);
            }
        } else if (type instanceof java.lang.reflect.WildcardType wt) {
            // 通配符 ? extends X
            java.lang.reflect.Type[] upperBounds = wt.getUpperBounds();
            if (upperBounds.length > 0) {
                return getClassFromType(upperBounds[0]);
            }
        }

        // 无法确定
        return null;
    }


}
