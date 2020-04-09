package bab.bitsworlds.world;

import java.util.UUID;

public abstract class BWorld {
    BWorldProperties properties;

    public abstract String getName();
    public abstract UUID getUUID();
}
