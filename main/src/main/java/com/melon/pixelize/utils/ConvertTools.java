package com.melon.pixelize.utils;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvertTools {

    public static byte[] convertImageToMCMapArt(Path input, int THREAD_CNT) {
        int pixHMost = 128, pixWMost = 128, pixelContains = 1;
        try {
            BufferedImage image = ImageIO.read(input.toFile());
            int h = image.getHeight(), w = image.getWidth();
            int pixH = h / pixelContains, pixW = w / pixelContains;
            double hwRate = ((double) pixH / (double) pixW);

            if (pixW > pixWMost || pixH > pixHMost)
                if (hwRate > 1) {
                    pixelContains *= ((double) pixH / (double) pixHMost);
                    pixH = pixHMost;
                    pixW = (int) ((double) pixHMost / hwRate);

                } else {
                    pixelContains *= ((double) pixW / (double) pixWMost);
                    pixW = pixWMost;
                    pixH = (int) ((double) pixWMost * hwRate);
                }

            int pc1 = pixelContains;

            int[][][] colorsumRGB = new int[3][pixW][pixH];// 0 for r ,1 for g, 2 for b

            ExecutorService pool = Executors.newFixedThreadPool(THREAD_CNT);
            int index = 0;

            while (index < THREAD_CNT) {
                final int tpixW = pixW;
                final int start = index * pixH / THREAD_CNT;
                final int end = (index + 1) * pixH / THREAD_CNT;
                pool.submit(() -> {
                    int j = 0, y = 0;
                    try {
                        for (y = start; y < end; y++)
                            for (j = 0; j < tpixW; j++) {
                                for (int clr : image.getRGB(((w % pc1) / 2) + j * pc1, ((h % pc1) / 2) + y * pc1, pc1,
                                        pc1, null, 0, pc1)) {
                                    for (int type = 0; type < 3; type++)
                                        colorsumRGB[type][j][y] += (clr >> (8 * (2 - type))) & 0xff;
                                }
                                for (int i = 0; i < 3; i++)
                                    colorsumRGB[i][j][y] /= (pc1 * pc1);
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("error at (" + j + "," + y + ")");
                    }
                });
                index++;
            }
            pool.shutdown();

            while (!pool.isTerminated()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            byte[] mapData = new byte[16384];
            for (int i = 0; i < pixW; i++)
                for (int j = 0; j < pixH; j++) {
                    MCMapColor clr = MCMapColor.getColorByRGBVal(
                            (colorsumRGB[0][i][j] << 16) | (colorsumRGB[1][i][j] << 8) | colorsumRGB[2][i][j]);
                    mapData[i + 128 * j] = MCMapColor.getMapColor(clr.getBase(), clr.getModifier());
                }

            return mapData;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<ArrayList<byte[]>> convertImageToMapTIles(Path input) {
        BufferedImage image;
        try {
            image = ImageIO.read(input.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int w = image.getWidth(), h = image.getHeight();
        int xTiles = w / 128 + (w % 128 == 0 ? 0 : 1), yTiles = h / 128 + (h % 128 == 0 ? 0 : 1);

        int[] rgbarr = image.getRGB(0, 0, w, h, null, 0, w);

        int THREAD_CNT = xTiles * yTiles;
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_CNT);

        ArrayList<ArrayList<byte[]>> tileMap = new ArrayList<ArrayList<byte[]>>();
        for (int i = 0; i < xTiles; i++){
            ArrayList<byte[]> ele =new ArrayList<>(yTiles);
            for(int j = 0;j<yTiles;j++){
                ele.add(new byte[0]);
            }
            tileMap.add(ele);
        }
            

        int currX = 0, currY = 0;
        int progress[] = new int[]{0};
        final int total_prog = xTiles*yTiles;
        

        while (currY < yTiles) {
            final int tempcX = currX, tempcY = currY;
            pool.submit(() -> {
                byte[] tile = new byte[16384];
                for (int x = 0; x < 128; x++) {
                    for (int y = 0; y < 128; y++) {
                        MCMapColor clr = MCMapColor.NULL;
                        if ((tempcX * 128 + x) < w - 1 && (tempcY * 128 + y) < h - 1)
                            clr = MCMapColor.getColorByRGBVal(rgbarr[(tempcX * 128 + x) + w * (tempcY * 128 + y)]);
                        tile[x + 128 * y] = MCMapColor.getMapColor(clr.getBase(), clr.getModifier());
                    }
                    tileMap.get(tempcX).set(tempcY, tile);
                }
            //System.out.println("Tile ["+tempcX+","+tempcY+"] compelete!");
            progress[0]++;
            });
            if (++currX >= xTiles) {
                currX = 0;
                currY++;
            }
        }

        pool.shutdown();

        
        int rolling_prog = 0;
        char[] rolling_char = new char[]{'\\','|','/','-'};
        while (!pool.isTerminated()) {
            System.out.println("Tile progress: "+progress[0]+" / "+total_prog+rolling_char[rolling_prog=(++rolling_prog%4)]);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        
        

        return tileMap;
    }

    
}
