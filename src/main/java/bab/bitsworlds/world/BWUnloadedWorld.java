package bab.bitsworlds.world;

public class BWUnloadedWorld implements BWorld {
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public BWUnloadedWorld(String name) {
        this.name = name;
    }
}
