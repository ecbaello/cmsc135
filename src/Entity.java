public abstract class Entity {

	public int x, y;
	protected Level level;
	public String username;

	public Entity(Level level,String name) {
		init(level,name);
	}

	public final void init(Level level, String name) {
		this.level = level;
		this.username = name;
	}

	public abstract void tick();

	public abstract void render(Screen screen);
}
