package com.melon.pixelize.utils;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
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
}
