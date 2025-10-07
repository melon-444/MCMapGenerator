package com.melon.pixelize.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipOutputStream;

import com.melon.pixelize.nbt.NBTInt;
import com.melon.pixelize.nbt.NBTObjectBuilder;

public final class MapTileDatapackGenerator {
    public static ArrayList<String> functionGenerator(Facing direction, ArrayList<ArrayList<byte[]>> MapTiles,
            int index) {
        int x = MapTiles.size(), y = MapTiles.get(0).size();
        boolean oddX = x % 2 != 0, oddY = y % 2 != 0;
        ArrayList<String> result = new ArrayList<>();
        CommandTemplate fill = new CommandTemplate("fill", null, null, null, null, null, null, "minecraft:barrier");
        CommandTemplate summon = new CommandTemplate("summon", "minecraft:glow_item_frame", null, null, null, null);
        NBTObjectBuilder components = NBTObjectBuilder.buildCompound("components").Int("\"minecraft:map_id\"", 0);
        switch (direction) {
            case BOTTOM:
                result.add(fill.toActualCmd("~" + (-x / 2), "~-1", "~" + (-y / 2), "~" + (x / 2 + (oddX ? 1 : 0)),
                        "~-1", "~" + (y / 2 + (oddY ? 1 : 0))));
                break;
            case TOP:
                result.add(fill.toActualCmd("~" + (-x / 2), "~2", "~" + (-y / 2), "~" + (x / 2 + (oddX ? 1 : 0)), "~2",
                        "~" + (y / 2 + (oddY ? 1 : 0))));
                break;
            case SOUTH:
                result.add(fill.toActualCmd("~" + x, "~", "~1", "~", "~" + y, "~1"));
                break;
            case NORTH:
                result.add(fill.toActualCmd("~" + (-x), "~", "~-1", "~", "~" + y, "~-1"));
                break;
            case EAST:
                result.add(fill.toActualCmd("~1", "~", "~" + x, "~1", "~" + y, "~"));
                break;
            case WEST:
                result.add(fill.toActualCmd("~-1", "~", "~" + (-x), "~-1", "~" + y, "~"));
                break;
            default:
                throw new IllegalArgumentException("Wrong direction " + direction.name() + " !");
        }
        int THREAD_CNT = x * y;
        final int total_prog = THREAD_CNT;
        final int[] progress = new int[] { 0 };
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_CNT);
        for (int i = 0; i < x; i++)
            for (int j = 0; j < y; j++) {
                final int ti = i, tj = j, tindex = index;
                pool.submit(() -> {
                    NBTObjectBuilder copyCom = null;
                    try {
                        copyCom = components.clone();
                    } catch (CloneNotSupportedException e) {
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
                            .Byte("Facing", (byte) direction.ordinal());
                    switch (direction) {
                        case BOTTOM:
                            result.add(summon.toActualCmd("~" + (ti + -x / 2), "~-2", "~" + (-tj + y / 2+ (oddY ? 1 : 0)),
                                    entityData.endCompound().toString()));
                            break;
                        case TOP:
                            result.add(summon.toActualCmd("~" + (ti + -x / 2), "~3", "~" + (tj + -y / 2),
                                    entityData.endCompound().toString()));
                            break;
                        case SOUTH:
                            result.add(summon.toActualCmd("~" + (x-ti), "~" + (y - tj), "~",
                                    entityData.endCompound().toString()));
                            break;
                        case NORTH:
                            result.add(summon.toActualCmd("~" + (ti-x), "~" + (y - tj), "~",
                                    entityData.endCompound().toString()));
                            break;
                        case EAST:
                            result.add(summon.toActualCmd("~", "~" + (y - tj), "~" + (ti),
                                    entityData.endCompound().toString()));
                            break;
                        case WEST:
                            result.add(summon.toActualCmd("~", "~" + (y - tj), "~" + (-ti),
                                    entityData.endCompound().toString()));
                        default:
                            throw new IllegalArgumentException("Wrong direction " + direction.name() + " !");
                    }
                    progress[0]++;
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
        return result;
    }


    public static void generateDatapack(Path packname){
    try (ZipOutputStream datapackOut = new ZipOutputStream(new FileOutputStream(packname.toFile()))) {
        datapackOut.putNextEntry(null);
    } catch (IOException e) {
        e.printStackTrace();
    }

    }

    
    
    public enum Facing {
        BOTTOM, TOP, SOUTH, NORTH, EAST, WEST
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

    public String toActualCmd(String... arg) {
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