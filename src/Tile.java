public abstract class Tile {

    public static final Tile[] tiles = new Tile[256];
    public static final Tile VOID = new BasicSolidTile(0, 0, 0, Colours.get(000, -1, -1, -1), 0xFF000000);
    public static final Tile STONE = new BasicSolidTile(1, 1, 0, Colours.get(-1, 333, -1, -1), 0xFF555555);
    public static final Tile GRASS = new BasicTile(2, 2, 0, Colours.get(-1, 131, 141, -1), 0xFF00FF00);
    public static final Tile WATER = new AnimatedTile(3, new int[][] { { 0, 5 }, { 1, 5 }, { 2, 5 }, { 1, 5 } }, Colours.get(-1, 004, 115, -1), 0xFF0000FF, 1000);
    public static final Tile FIRE = new AnimatedTile(4, new int[][] {{0, 6}, {1, 6}, {2, 6}, {1, 6}}, Colours.get(333, 500, 530, 540), 0xFFFF6C00, 1000);
    public static final Tile ROAD = new BasicTile(5, 1, 0, Colours.get(-1, 444, -1, -1), 0xFF444444);
    public static final Tile ROADLINES = new BasicTile(6, 1, 0, Colours.get(-1, 540, -1, -1), 0xFFFFFC00);
    public static final Tile WALL = new  BasicSolidTile(7, 1, 0, Colours.get(-1, 333, -1, -1), 0xFF333333);
    public static final Tile MUD = new BasicTile(8, 2, 0, Colours.get(-1, 421, 444, -1), 0xFF722E01);
    protected byte id;
    protected boolean solid;
    protected boolean emitter;
    private int levelColour;

    public Tile(int id, boolean isSolid, boolean isEmitter, int levelColour) {
        this.id = (byte) id;
        if (tiles[id] != null)
            throw new RuntimeException("Duplicate tile id on " + id);
        this.solid = isSolid;
        this.emitter = isEmitter;
        this.levelColour = levelColour;
        tiles[id] = this;
    }

    public byte getId() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isEmitter() {
        return emitter;
    }

    public int getLevelColour() {
        return levelColour;
    }

    public abstract void tick();

    public abstract void render(Screen screen, Level level, int x, int y);
}
