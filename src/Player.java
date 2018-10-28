public class Player extends Mob {

    private int colour = Colours.get(-1, 111, 005, 441);
    private int scale = 1;
    protected boolean isSwimming = false;
    private int tickCount = 0;
    private String username;
    public String com;
    public boolean wasPressed = false;
    public char direction = 'w';
    private int xa;
    private int ya;
    public int hp = 100;
    public Game game;

    public Player(Level level, int x, int y, String username) {
        super(level, "Player", x, y, 1);
        this.username = username;
    }

    public void tick() {
        xa = 0;
        ya = 0;
        if(wasPressed){
        	switch(com){
        		case "w":	forward(); break;
        		case "back":	back(); break;
        		case "a":	rotL(); break;
        		case "d":	rotR(); break;
        		default:	System.out.println("Command Not Recognized");
        	}
        }
        
        if (xa != 0 || ya != 0) {
            move(xa, ya);
            isMoving = true;

            /*Packet02Move packet = new Packet02Move(this.getUsername(), this.x, this.y, this.numSteps, this.isMoving,
                    this.movingDir);
            packet.writeData(Game.game.socketClient);*/
        } else {
            isMoving = false;
        }
        if (level.getTile(this.x >> 3, this.y >> 3).getId() == 3) {
            isSwimming = true;
        }
        if (isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3) {
            isSwimming = false;
        }
        
        if(wasPressed){
	    	game.cc.callToSend();
	    	wasPressed = false;
        }
        
        tickCount++;
    }

    public void render(Screen screen) {
        int xTile = 0;
        int yTile = 28;
        int walkingSpeed = 4;
        int flipTop = (numSteps >> walkingSpeed) & 1;
        int flipBottom = (numSteps >> walkingSpeed) & 1;

        if (movingDir == 1) {
            xTile += 2;
        } else if (movingDir > 1) {
            xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;
            flipTop = (movingDir - 1) % 2;
        }

        int modifier = 8 * scale;
        int xOffset = x - modifier / 2;
        int yOffset = y - modifier / 2 - 4;
        if (isSwimming) {
            int waterColour = 0;
            yOffset += 4;
            if (tickCount % 60 < 15) {
                waterColour = Colours.get(-1, -1, 225, -1);
            } else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
                yOffset -= 1;
                waterColour = Colours.get(-1, 225, 115, -1);
            } else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
                waterColour = Colours.get(-1, 115, -1, 225);
            } else {
                yOffset -= 1;
                waterColour = Colours.get(-1, 225, 115, -1);
            }
            screen.render(xOffset, yOffset + 3, 0 + 27 * 32, waterColour, 0x00, 1);
            screen.render(xOffset + 8, yOffset + 3, 0 + 27 * 32, waterColour, 0x01, 1);
        }
        screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, colour, flipTop, scale);
        screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, colour, flipTop,
                scale);

        if (!isSwimming) {
            screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, colour,
                    flipBottom, scale);
            screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile + 1)
                    * 32, colour, flipBottom, scale);
        }
        if (username != null) {
            Font.render(username, screen, xOffset - ((username.length() - 1) / 2 * 8), yOffset - 10,
                    Colours.get(-1, -1, -1, 555), 1);
        }
    }

    public boolean hasCollided(int xa, int ya) {
        int xMin = 0;
        int xMax = 7;
        int yMin = 3;
        int yMax = 7;
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya, x, yMin)) {
                return true;
            }
        }
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya, x, yMax)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya, xMin, y)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya, xMax, y)) {
                return true;
            }
        }
        return false;
    }

    public String getUsername() {
        return this.username;
    }
    
    /**	Actions	**/
    
    private void rotL(){
    	switch(direction){
			case 'w': direction='a'; break;
			case 'a': direction='s'; break;
			case 's': direction='d'; break;
			default: direction='w';
    	}
    }
    
    private void rotR(){
		switch(direction){
			case 'w': direction='d'; break;
			case 'a': direction='w'; break;
			case 's': direction='a'; break;
			default: direction='s';
		}
	}
    
    private void forward(){
    	switch(direction){
    		case 'w':	ya-=8; break;
    		case 's':	ya+=8; break;
    		case 'a':	xa-=8; break;
    		case 'd':	xa+=8; break;
    		default:	System.out.println("Problematic Situation");
    	}
    }
    
    private void back(){
    	switch(direction){
			case 'w':	ya+=8; break;
			case 's':	ya-=8; break;
			case 'a':	xa+=8; break;
			case 'd':	xa-=8; break;
			default:	System.out.println("Problematic Situation");
    	}
    }
}
