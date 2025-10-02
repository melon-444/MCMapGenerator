package com.melon.pixelize.nbt.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import com.melon.pixelize.nbt.NBTElement;

public class GZipNBTWriter extends NBTWriter{
        private final GZIPOutputStream gzipOut;

    public GZipNBTWriter(GZIPOutputStream gzipOut) {
        this.gzipOut = gzipOut;
    }

    public GZipNBTWriter(Path file) {
        try {
            if(!file.toFile().exists()) file.toFile().createNewFile();
            this.gzipOut = new GZIPOutputStream(new FileOutputStream(file.toFile()));
        } catch (IOException e) {
            throw new RuntimeException("Construction Failed:"+e.getMessage());
        }
    }

    @Override
    public void flush() {
        try {
            gzipOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        try {
            gzipOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void write(int b) throws IOException {
        gzipOut.write(b);
    }

    @Override
    public void write(NBTElement<?> element) throws IOException{
            gzipOut.write(element.toBytes());
    };
}
