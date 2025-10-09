package com.melon.pixelize.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipOutputStream;

import com.melon.nbt.NBTCompound;
import com.melon.nbt.NBTInt;
import com.melon.nbt.NBTObjectBuilder;

public final class MapTileDatapackGenerator {
    public static ArrayList<String> functionGenerator(Facing direction, ArrayList<ArrayList<byte[]>> MapTiles,
            int index) {
        int x = MapTiles.size(), y = MapTiles.get(0).size();
        boolean oddX = x % 2 != 0, oddY = y % 2 != 0;
        ArrayList<String> result = new ArrayList<>();

        result.add("#program generated");


        CommandTemplate summon = new CommandTemplate("summon", "minecraft:glow_item_frame", null, null, null, null);
        NBTObjectBuilder components = NBTObjectBuilder.buildCompound("components").Int("\"minecraft:map_id\"", 0);
        
        int THREAD_CNT = x * y;
        final int total_prog = THREAD_CNT;
        final int[] progress = new int[] { 0 };
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_CNT);
        for (int i = 0; i < x; i++)
            for (int j = 0; j < y; j++) {
                final int ti = i, tj = j, tindex = index;
                pool.submit(() -> {
                    try {
                        NBTObjectBuilder copyCom =null;
                    try {
                        copyCom = components.copy();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    NBTObjectBuilder entityData = NBTObjectBuilder.buildCompound()
                            .directCompound(
                                    NBTObjectBuilder.buildCompound("Item")
                                            .String("id", "minecraft:filled_map")
                                            .Int("count", 1)
                                            .directCompound(
                                                    copyCom.set("\"minecraft:map_id\"", NBTInt.class, tindex)
                                                            .toCompound())
                                            .endCompound())
                            .Byte("Facing", (byte) direction.ordinal())
                            .Boolean("Invulnerable", true)
                            .Boolean("Fixed", true)
                            .Boolean("Invisible", true);

                    switch (direction) {
                        case BOTTOM:
                            result.add(
                                    summon.toActualCmd("~" + (ti + -x / 2+ (oddX ? 1 : 0)), "~-2", "~" + (-tj + y / 2 + (oddY ? 1 : 0)),
                                            entityData.endCompound().toString()));
                            break;
                        case TOP:
                            result.add(summon.toActualCmd("~" + (ti + -x / 2+ (oddX ? 1 : 0)), "~4", "~" + (tj + -y / 2+ (oddY ? 1 : 0)),
                                    entityData.endCompound().toString()));
                            break;
                        case SOUTH:
                            result.add(summon.toActualCmd("~" + (x - ti), "~" + (y - tj), "~",
                                    entityData.endCompound().toString()));
                            break;
                        case NORTH:
                            result.add(summon.toActualCmd("~" + (ti - x), "~" + (y - tj), "~",
                                    entityData.endCompound().toString()));
                            break;
                        case EAST:
                            result.add(summon.toActualCmd("~", "~" + (y - tj), "~" + (ti),
                                    entityData.endCompound().toString()));
                            break;
                        case WEST:
                            result.add(summon.toActualCmd("~", "~" + (y - tj), "~" + (-ti),
                                    entityData.endCompound().toString()));
                            break;
                        default:
                            throw new IllegalArgumentException("Wrong direction " + direction.name() + " !");
                    }
                    progress[0]++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                index++;
            }

        pool.shutdown();

        int rolling_prog = 0;
        char[] rolling_char = new char[] { '\\', '|', '/', '-' };
        while (!pool.isTerminated()) {
            System.out.println(
                    "Command " + direction.name().toLowerCase() + " progress: " + progress[0] + " / " + total_prog
                            + rolling_char[rolling_prog = (++rolling_prog % 4)]);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //for(int a:idmap.keySet()){
            //System.out.println(a+" : "+idmap.get(a)); //TODO:
        //}

        return result;
    }

    public static void generateDatapack(Path PackName, ArrayList<ArrayList<byte[]>> MapTiles,Version version, int index) {
        try (ZipOutputStream datapackOut = new ZipOutputStream(new FileOutputStream(PackName.toFile()))) {
            datapackOut.putNextEntry(new java.util.zip.ZipEntry("pack.mcmeta"));
            datapackOut.write(GenerateMCMeta(version).getBytes());
            for (Facing dir : Facing.values()) {
                ArrayList<String> function = functionGenerator(dir, MapTiles, index);
                String path = "data/pixelize/function/" + PackName.getFileName().toString().split("\\.zip")[0] + "_"
                        + dir.name().toLowerCase() + ".mcfunction";
                datapackOut.putNextEntry(new java.util.zip.ZipEntry(path));
                for (String cmd : function) {
                    datapackOut.write((cmd + "\n").getBytes());
                }
                datapackOut.closeEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected static String GenerateMCMeta(Version version){
        NBTCompound mcmeta = NBTObjectBuilder
        .buildCompound()
        .directCompound(NBTObjectBuilder
            .buildCompound("pack")
            .String("description","Program generated datapack")
            .Int("pack_format",version.DatapackVersion)
            .endCompound())
        .endCompound();
        return mcmeta.toJsonString();
    }

    public enum Facing {
        BOTTOM, TOP, SOUTH, NORTH, EAST, WEST
    }

    public enum Version{
        NULL,V1_13("1.13",4),V1_21("1.21",48);

        int DatapackVersion;
        String versionName;

        Version(){
            versionName = null;
            DatapackVersion = 0;
        }

        Version(String versionName,int DatapackVersion){
            this.DatapackVersion = DatapackVersion;
            this.versionName = versionName;
        }

        public static Version getVersion(String versionName){
            for(Version v:values()){
                if(versionName .equals(v.versionName) )
                    return v;
            }
            return NULL;
        }

        public static List<String> getAvaliableVersions(){
            ArrayList<String> arr = new ArrayList<>();
            for(Version v:values())
                if(v!=NULL)arr.add(v.versionName);
            return arr;
        }

    }

    
}

class CommandTemplate {
    ArrayList<String> paramList;
    ArrayList<Integer> changeIndex;

    public CommandTemplate(String... param) {
        ArrayList<Integer> changeIndex = new ArrayList<>();
        int i = 0;
        for (String s : param) {
            if (s == null)
                changeIndex.add(i);
            i++;
        }
        paramList = new ArrayList<>(Arrays.asList(param));
        this.changeIndex = changeIndex;
    }

    public synchronized String toActualCmd(String... arg) {
        if (arg.length != changeIndex.size())
            throw new IllegalArgumentException("arguments count doesn't match to changable arguments count!");
        int index = 0;
        for (int i : changeIndex) {
            paramList.set(i, arg[index++]);
        }
        StringBuilder result = new StringBuilder();
        for (String e : paramList) {
            result.append(e + " ");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }
}