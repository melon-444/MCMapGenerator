package com.melon.nbt.io;

import java.io.IOException;
import java.io.InputStream;

import com.melon.nbt.NBTElement;

public abstract class NBTReader extends InputStream {
    public abstract NBTElement<?> readNBTElement() throws IOException;
}
