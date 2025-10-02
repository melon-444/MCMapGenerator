package com.melon.pixelize.nbt;

import java.util.ArrayList;
import java.util.List;

import com.melon.pixelize.nbt.NBTElement.Type;

public class SNBTDecoder {
    protected static NBTElement<?> decode(String SNBT) {

        String keyName = getKeyName(SNBT, 0);
        byte type = getType(SNBT, getPayLoadIndex(keyName));
        String payLoad = getPayLoad(SNBT, getPayLoadIndex(keyName));
        return decodePayload(type, keyName, payLoad);
    }

    private static String getKeyName(String SNBT, int startIndex) {
        StringBuilder keyName = new StringBuilder();
        int index = startIndex;
        boolean inQuotes = false;// ""
        int inBrace = 0;// {}
        int inBracket = 0;// []
        while (index < SNBT.length()) {
            char c = SNBT.charAt(index);
            if (c == '\"') {
                inQuotes = !inQuotes;
            }
            if (c == '{'&& !inQuotes) {
                inBrace++;
            }
            if (c == '['&& !inQuotes) {
                inBracket++;
            } else if (c == ':' && !inQuotes && inBrace == 0 && inBracket == 0) {
                return keyName.toString().trim();
            } else if ((c == '}' || c == ']' || c == ',') && !inQuotes && inBrace == 0 && inBracket == 0) {
                return "";
            }
            if (c == '}'&& !inQuotes) {
                inBrace--;
            }
            if (c == ']'&& !inQuotes) {
                inBracket--;
            }
             else if(inBrace == 0 && inBracket == 0){
                keyName.append(c);
            } else if(inBrace<0||inBracket<0){
                throw new IllegalArgumentException("Unclosed bracket/brace at index " + index);
            }
            index++;
        }
        return "";
    }

    private static int getPayLoadIndex(String keyName) {
        return keyName.isEmpty() ? 0 : keyName.length() + 1;
    }

    private static byte getType(String SNBT, int payLoadIndex) {
        char c = SNBT.charAt(payLoadIndex);
        if (c == '{') {
            return Type.COMPOUND;
        } else if (c == '[') {
            if (SNBT.startsWith("[I;", payLoadIndex))
                return Type.INT_ARRAY;
            else if (SNBT.startsWith("[L;", payLoadIndex))
                return Type.LONG_ARRAY;
            else if (SNBT.startsWith("[B;", payLoadIndex))
                return Type.BYTE_ARRAY;
            else
                return Type.LIST;
        } else if (c == '\"' || c == '\'') {
            return Type.STRING;
        } else if (Character.isDigit(c)|| c == '.' || c == '-' || c == '+') {
            int endIndex = payLoadIndex;
            do {
                if (Character.isDigit(c)|| c == '.' || c == '-' || c == '+') {
                    endIndex++;
                } else if (c != ',' && c != '}' && c != ']') {
                    switch (c) {
                        case 'l':
                        case 'L':
                            return Type.LONG;
                        case 'f':
                        case 'F':
                            return Type.FLOAT;
                        case 'd':
                        case 'D':
                            return Type.DOUBLE;
                        case 's':
                        case 'S':
                            return Type.SHORT;
                        case 'b':
                        case 'B':
                            return Type.BYTE;
                        default:
                            throw new IllegalArgumentException("Unsupported NBT type \"" + c+"\" at index "+endIndex);
                    }
                }
                c = SNBT.charAt(endIndex);
            } while (c != ',' && c != '}' && c != ']');
            return Type.INT;
        } else if (SNBT.startsWith("true", payLoadIndex)
                || SNBT.startsWith("false", payLoadIndex)) {
            return Type.BOOLEAN;
        } else
            throw new IllegalArgumentException("Unsupported NBT type \"" + c+"\" at index "+payLoadIndex);
    }

    /*
     * no comma at the end
     */
    private static String getPayLoad(String SNBT, int payLoadIndex) {
        char c = SNBT.charAt(payLoadIndex);
        if (c == '{') {
            int startIndex = payLoadIndex;
            int nestCounter = 0;
            c = SNBT.charAt(++payLoadIndex);
            while (nestCounter > 0 || c != '}') {
                if (c == '{')
                    nestCounter++;
                else if (c == '}')
                    nestCounter--;
                c = SNBT.charAt(++payLoadIndex);
            }
            return SNBT.substring(startIndex, payLoadIndex + 1);
        } else if (c == '[') {
            int startIndex = payLoadIndex;
            int nestCounter = 0;
            c = SNBT.charAt(++payLoadIndex);
            while (nestCounter > 0 || c != ']') {
                if (c == '[')
                    nestCounter++;
                else if (c == ']')
                    nestCounter--;
                c = SNBT.charAt(++payLoadIndex);
            }
            return SNBT.substring(startIndex, payLoadIndex + 1);
        } else if (c == '"' || c == '\'') {
            int startIndex = payLoadIndex;
            char outerQuote = c;
            do {
                c = SNBT.charAt(++payLoadIndex);
            } while ((c != outerQuote) || ((c == outerQuote) && SNBT.charAt(payLoadIndex - 1) == '\\'));
            return SNBT.substring(startIndex, payLoadIndex + 1);
        } else if (Character.isDigit(c)|| c == '.'  || c == '-' || c == '+') {
            int endIndex = payLoadIndex;
            do {
                c = SNBT.charAt(endIndex);
                if (Character.isDigit(c)|| c == '.'  || c == '-' || c == '+') {
                    endIndex++;
                } else if (c != ',' && c != '}' && c != ']') {
                    return SNBT.substring(payLoadIndex, endIndex + 1);
                }
            } while (c != ',' && c != '}' && c != ']');
            return SNBT.substring(payLoadIndex, endIndex);
        } else if (SNBT.startsWith("true", payLoadIndex)
                || SNBT.startsWith("false", payLoadIndex)) {
            if (SNBT.startsWith("true", payLoadIndex))
                return "true";
            else
                return "false";
        } else
            throw new IllegalArgumentException("Unsupported NBT type \"" + c+"\" at index "+payLoadIndex+"\n Original: "+SNBT);
    }

    private static NBTElement<?> decodePayload(byte type, String key, String SNBTPayload) {
        switch (type) {
            case Type.BYTE:
                try {
                    return new NBTByte(key, Byte.valueOf(SNBTPayload.substring(0, SNBTPayload.length() - 1)));
                } catch (Exception e) {
                    return new NBTBoolean(key, Boolean.valueOf(SNBTPayload));
                }
            case Type.SHORT:
                return new NBTShort(key, Short.valueOf(SNBTPayload.substring(0, SNBTPayload.length() - 1)));
            case Type.INT:
                return new NBTInt(key, Integer.valueOf(SNBTPayload));
            case Type.LONG:
                return new NBTLong(key, Long.valueOf(SNBTPayload.substring(0, SNBTPayload.length() - 1)));
            case Type.FLOAT:
                return new NBTFloat(key, Float.valueOf(SNBTPayload.substring(0, SNBTPayload.length() - 1)));
            case Type.DOUBLE:
                return new NBTDouble(key, Double.valueOf(SNBTPayload.substring(0, SNBTPayload.length() - 1)));
            case Type.STRING:
                return new NBTString(key, SNBTPayload.substring(1, SNBTPayload.length() - 1));
            case Type.LIST: {

                List<NBTElement<?>> elements = new ArrayList<>();

                if (SNBTPayload.charAt(1) != ']') {// if it is not an empty list
                    byte elemType = getType(SNBTPayload, 1);
                    String Ori = SNBTPayload;
                    Ori = Ori.substring(1);

                    while (Ori.length() > 0) {
                        String elemP = getPayLoad(Ori, 0);
                        Ori = Ori.substring(elemP.length() + 1);
                        elements.add(decodePayload(elemType, "", elemP));
                    }
                }

                return new NBTList(key, elements);
            }
            case Type.COMPOUND: {
                String Ori = SNBTPayload;
                Ori = Ori.substring(1);
                List<NBTElement<?>> elements = new ArrayList<>();
                   
                while (Ori.length() > 0) {
                    String nestedKeyName = getKeyName(Ori, 0);
                    String nestedPayLoad = getPayLoad(Ori, getPayLoadIndex(nestedKeyName));
                    byte nestedType = getType(Ori, getPayLoadIndex(nestedKeyName));
                    Ori = Ori.substring(getPayLoadIndex(nestedKeyName) + nestedPayLoad.length() + 1);
                    elements.add(decodePayload(nestedType, nestedKeyName, nestedPayLoad));
                    
                }

                return new NBTCompound(key, elements);
            }
            case Type.BYTE_ARRAY: {

                String Ori = SNBTPayload;
                Ori = Ori.substring(3);
                Ori = Ori.substring(0, Ori.length() - 1);
                String[] bytearrS = Ori.split(",");
                int len = bytearrS.length;
                byte[] arr = new byte[len];
                for (int i = 0; i < len; i++)
                    arr[i] = Byte.valueOf(bytearrS[i]);
                return new NBTByteArray(key, arr);
            }
            case Type.INT_ARRAY: {
                String Ori = SNBTPayload;
                Ori = Ori.substring(3);
                Ori = Ori.substring(0, Ori.length() - 1);
                String[] intarrS = Ori.split(",");
                int len = intarrS.length;
                int[] arr = new int[len];
                for (int i = 0; i < len; i++)
                    arr[i] = Integer.valueOf(intarrS[i]);
                return new NBTIntArray(key, arr);
            }
            case Type.LONG_ARRAY: {
                String Ori = SNBTPayload;
                Ori = Ori.substring(3);
                Ori = Ori.substring(0, Ori.length() - 1);
                String[] longarrS = Ori.split(",");
                int len = longarrS.length;
                long[] arr = new long[len];
                for (int i = 0; i < len; i++)
                    arr[i] = Long.valueOf(longarrS[i]);
                return new NBTLongArray(key, arr);
            }
            default:
                throw new IllegalArgumentException("Unsupported NBT type: " + type+"\n Original: "+SNBTPayload);
        }
    }

}
