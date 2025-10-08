package com.melon.nbt.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import com.melon.nbt.NBTElement;

public class GZipNBTReader extends NBTReader {
    final GZIPInputStream gzipIn;

    public GZipNBTReader(GZIPInputStream gzipIn) {
        this.gzipIn = gzipIn;
    }

    public GZipNBTReader(Path file) {
        try {
            this.gzipIn = new GZIPInputStream(new FileInputStream(file.toFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int read() throws IOException {
        return gzipIn.read();
    }

    public NBTElement<?> readNBTElement() throws IOException {
        return NBTElement.asNBT(gzipIn.readAllBytes());
    }

}
