package com.melon.pixelize;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;

import com.melon.pixelize.nbt.io.GZipNBTWriter;

import com.melon.pixelize.utils.ConvertTools;
import com.melon.pixelize.utils.MapTileDatapackGenerator;
import com.melon.pixelize.utils.MapTileGenerator;
import com.melon.pixelize.utils.MapTileDatapackGenerator.Version;

public class MCMapGenerator {
    static class Test1 {
        public static void main(String[] args) {
            System.out.println("MCMapGenerator Test");
            try (Scanner sc = new Scanner(System.in)) {
                while (true) {
                    int index = 1;
                    while (Path.of("ignore/map_" + index + ".dat").toFile().exists())
                        index++;
                    System.out.println("Input image path in ignore/ :");
                    byte[] target = ConvertTools.convertImageToMCMapArt(Path.of("ignore/" + sc.nextLine().trim()), 5);

                    File f = Path.of("ignore/map_" + index + ".dat").toFile();
                    if (!f.exists())
                        f.createNewFile();
                    GZipNBTWriter writer = new GZipNBTWriter(new GZIPOutputStream(new FileOutputStream(f)));
                    writer.write(MapTileGenerator.getMapDatByByte(target));
                    writer.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class Test2 {
        public static void main(String[] args) {
            try (Scanner sc = new Scanner(System.in)) {
                Version v;
                System.out.println("Avaliable game version:");
                StringBuilder sb = new StringBuilder();
                for(String ver : Version.getAvaliableVersions())sb.append(ver+", ");
                sb.deleteCharAt(sb.length()-1);
                sb.deleteCharAt(sb.length()-1);
                System.out.println(sb.toString());
                boolean flag = true;
                do{
                    System.out.println("Plaese enter game version:");
                    String gameVersion = sc.nextLine();
                    v = Version.getVersion(gameVersion);
                    if(v != Version.NULL){
                        flag = false;
                    }
                    else System.out.println("Invalid version.");
                }while(flag);

                while (true) {
                    int index = 1;
                    while (Path.of("ignore/map_" + index + ".dat").toFile().exists())
                        index++;

                    System.out.println("Input image path in ignore/ :");
                    String targetPath = sc.nextLine().trim();
                    ArrayList<ArrayList<byte[]>> targetTiles = ConvertTools
                            .convertImageToMapTIles(Path.of("ignore/" + targetPath));
                    
                    MapTileDatapackGenerator.generateDatapack(Path.of("ignore/"+targetPath.split("\\.")[0]+".zip"), targetTiles, v,index);

                    for (int i = 0; i < targetTiles.size(); i++) {
                        for (int j = 0; j < targetTiles.get(0).size(); j++) {
                            Path tile = Path.of("ignore/map_" + index + ".dat");
                            if (!tile.toFile().exists())
                                tile.toFile().createNewFile();
                            try (GZipNBTWriter writer = new GZipNBTWriter(tile);) {
                                writer.write(MapTileGenerator.getMapDatByByte(targetTiles.get(i).get(j)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            index++;
                        }
                    }

                    GZipNBTWriter writer = new GZipNBTWriter(Path.of("ignore/idcounts.dat"));
                    writer.write(MapTileGenerator.getNextUsableCompound(index));
                    writer.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
