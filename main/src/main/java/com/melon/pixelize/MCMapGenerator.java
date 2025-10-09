package com.melon.pixelize;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.melon.nbt.io.GZipNBTWriter;
import com.melon.pixelize.utils.ConvertTools;
import com.melon.pixelize.utils.MapTileDatapackGenerator;
import com.melon.pixelize.utils.MapTileGenerator;
import com.melon.pixelize.utils.MapTileDatapackGenerator.Version;

public class MCMapGenerator {
    static class Test {
        public static void main(String[] args) {
            try (Scanner sc = new Scanner(System.in)) {

                loop1: while (true) {
                    System.out.println("Delete dat (Y/N):");
                    String choice = sc.nextLine();
                    switch (choice) {
                        case "Y":
                        case "y":
                            for (int i = 1; Path.of("ignore/map_" + i + ".dat").toFile().exists(); i++)
                                Path.of("ignore/map_" + i + ".dat").toFile().delete();
                            break loop1;
                        case "N":
                        case "n":
                            break loop1;
                        default:
                            break;
                    }
                }
                boolean always = false, ask = false, never = false;
                Version v;
                System.out.println("Avaliable game version:");
                StringBuilder sb = new StringBuilder();
                for (String ver : Version.getAvaliableVersions())
                    sb.append(ver + ", ");
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
                System.out.println(sb.toString());
                boolean flag = true;
                do {
                    System.out.println("Plaese enter game version:");
                    String gameVersion = sc.nextLine();
                    v = Version.getVersion(gameVersion);
                    if (v != Version.NULL) {
                        flag = false;
                    } else
                        System.out.println("Invalid version.");
                } while (flag);

                flag = true;
                do {
                    System.out.println("Do you want to cut the picture(always/ask/never):");
                    String opt = sc.nextLine();
                    switch (opt) {
                        case "always":
                        case "Always":
                            always = true;
                            flag = false;
                            break;
                        case "ask":
                        case "Ask":
                            ask = true;
                            flag = false;
                            break;
                        case "never":
                        case "Never":
                            never = true;
                            flag = false;
                            break;
                        default:
                            System.out.println("Invalid input.");
                            break;
                    }
                } while (flag);

                while (true) {
                    int index = 1;
                    while (Path.of("ignore/map_" + index + ".dat").toFile().exists())
                        index++;

                    System.out.println("Input image path in ignore/ :");
                    String targetPath = sc.nextLine().trim();

                    BufferedImage image = ImageIO.read(Path.of("ignore/" + targetPath).toFile());
                    if (!never && always) {
                        cuttedImage(image);
                    } else if (!never && ask) {
                        loop: while (true) {
                            System.out.println("Cut the picture (Y/N):");
                            String choice = sc.nextLine();
                            switch (choice) {
                                case "Y":
                                case "y":
                                    cuttedImage(image);
                                    break loop;
                                case "N":
                                case "n":
                                    break loop;
                                default:
                                    break;
                            }
                        }
                    }

                    ArrayList<ArrayList<byte[]>> targetTiles = ConvertTools
                            .convertImageToMapTIles(image);

                    MapTileDatapackGenerator.generateDatapack(Path.of("ignore/" + targetPath.split("\\.")[0] + ".zip"),
                            targetTiles, v, index);

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

        private static BufferedImage cuttedImage(BufferedImage image) {
            Graphics2D graphics2d = image.createGraphics();
            graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int w = image.getWidth(), h = image.getHeight();
            BufferedImage newImage = new BufferedImage(w - (w % 128), h - (h % 128), BufferedImage.TYPE_INT_ARGB);
            graphics2d.drawImage(newImage, 0, 0, w - (w % 128), h - (h % 128), null);
            return newImage;
        }
    }
}
