package bab.bitsworlds.world;

import java.io.File;

public class BWUnloadedWorld implements BWorld {
    private File file;

    @Override
    public String getName() {
        return file.getName();
    }

    public BWUnloadedWorld(File file) {
        this.file = file;
    }
}
