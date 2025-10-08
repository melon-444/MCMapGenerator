package com.melon.pixelize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.Test;

import com.melon.nbt.NBTCompound;
import com.melon.nbt.NBTElement;
import com.melon.nbt.NBTObjectBuilder;
import com.melon.nbt.io.GZipNBTWriter;
import com.melon.pixelize.utils.ConvertTools;

public class MapGenTest {
    @Test void MapGen() throws IOException{
         System.out.println("MCMapGenerator Test");
            try (Scanner sc = new Scanner(System.in)) {
                boolean once = true;
                while (once) {
                    int index = 0;
                    while (Path.of("ignore/map_" +index + ".dat").toFile().exists())
                        index++;
                    System.out.println("Input image path in ignore/ :");
                    String sc1 = "test.png";//sc.nextLine().trim();
                    byte[] target = ConvertTools.convertImageToMCMapArt(Path.of("../ignore/"+sc1), 5);
                    NBTElement<?> dat =
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
                            .end();
                    System.out.println(dat.toString());

                    System.out.println();
                    System.out.println();
                    System.out.println();
                    System.out.println();

                    NBTCompound datS = (NBTCompound)NBTElement.asNBT(dat.toString());

                    List<NBTElement<?>> list = (datS.getPayLoad());
                    Iterator<NBTElement<?>> it = list.iterator();
                    while (it.hasNext()) {
                        NBTElement<?> next = it.next();
                        if(next.getKeyName().equals("DataVersion")){
                            System.out.println(next.toString());
                            break;
                        }else{
                            System.out.println(next.getKeyName()+" is not DataVersion!");
                        }
                    }

                    System.out.println(datS);


                    File f = Path.of("../ignore/map_" + index + ".dat").toFile();
                    if (!f.exists())
                        f.createNewFile();
                    GZipNBTWriter writer = new GZipNBTWriter(new GZIPOutputStream(new FileOutputStream(f)));
                    writer.write(dat);
                    writer.close();

                    once = false;
                }

            } catch (Exception e) {
                //assertTrue(false,"ERRMSG---"+e.getMessage()+"---ERRMSG");
                throw e;

            }
    }
    
}
