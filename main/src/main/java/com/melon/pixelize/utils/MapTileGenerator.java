package com.melon.pixelize.utils;

import com.melon.nbt.NBTCompound;
import com.melon.nbt.NBTObjectBuilder;

public class MapTileGenerator {
    /**
     * Generate a NBTCompound representing a Minecraft map.dat file from a byte array of length 16384.
     * Each byte in the array corresponds to a pixel color in the map.
     * @param target
     * @return NBTCompound representing the map_<i>.dat file.
     */
    public static NBTCompound getMapDatByByte(byte[] target) {

        if(target.length != 16384)
            throw new IllegalArgumentException("Target byte array length must be 16384, however it is "+target.length);

        NBTCompound dat =
                    NBTObjectBuilder.buildCompound()
                            .directCompound(
                            NBTObjectBuilder.buildCompound("data")
                            .ByteArray("colors", target)
                            .String("dimension", "minecraft:overworld")
                            .Boolean("locked", true)
                            .Byte("scale", (byte)0)
                            .Boolean("trackingPosition", false)
                            .Boolean("unlimitedTracking", false)
                            .Int("xCenter", 0)
                            .Int("zCenter",0)
                            .directList(NBTObjectBuilder.buildList("banners")
                            .endList())
                            .endCompound()
                            )
                            .Int("DataVersion", 1343)
                            .endCompound();
        return dat;
    }

    public static NBTCompound getNextUsableCompound(int next){
        NBTCompound idcounts = NBTObjectBuilder.buildCompound()
        .directCompound(NBTObjectBuilder.buildCompound("data").Int("map",next).endCompound())
        .Int("DataVersion", 1343)
        .endCompound();
        return idcounts;
    }
}
