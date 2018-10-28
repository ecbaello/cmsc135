import java.net.InetAddress;

public class PlayerMP extends Player {

    public InetAddress ipAddress;
    public int port;

    public PlayerMP(Level level, int x, int y, String username, InetAddress ipAddress, int port) {
        super(level, x, y, username);
        this.ipAddress = ipAddress;
        this.port = port;
    }
    
    public PlayerMP(Level level, int x, int y, String username) {
        super(level, x, y, username);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
