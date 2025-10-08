package com.melon.nbt.interfac3;

import com.melon.nbt.NBTElement;

public interface RootElement {
    public boolean removeElement(String keyname);
    public boolean removeElement(NBTElement<?> element);
    public void addElement(NBTElement<?> element);
}
