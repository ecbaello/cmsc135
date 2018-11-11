import java.util.List;

public class Player extends Mob {

    private int colour = Colours.get(-1, 500, 555, 443);
    private int scale = 1;
    protected boolean isSwimming = false;
    protected boolean isBurning = false;
    protected boolean isDead = false;
    private int tickCount = 0;
    public String username;
    public String com;
    public boolean wasPressed = false;
    public char direction = 's';
    private int xa;
    private int ya;
    public int hp = 10000;
    public Game game;
    public int weapon=1;	/* 1-pistol; 2-shotgun; 3-sniper */

    public Player(Level level, int x, int y, String username) {
        super(level, username, x, y, 1);
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
        		case "q":	weaponQ(); break;
        		case "e":	weaponE(); break;
        		case "pew":	checkTarget(); break;
        		default:	System.out.println("Command Not Recognized");
        	}
        }
        
        if ((xa != 0 || ya != 0) && !isDead) {
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
        
        if (level.getTile(this.x >> 3, this.y >> 3).getId() == 4) {
        	isBurning = true;
        }
        if (isBurning && level.getTile(this.x >> 3, this.y >> 3).getId() != 4) {
            isBurning = false;
        }
        
        if (hp == 0) {
        	isDead = true;
        }
        
        if(wasPressed && !isDead){
	    	game.cc.callToSend();
	    	wasPressed = false;
        }else {
        	wasPressed = false;
        }
        
        tickCount++;
    }

    public void render(Screen screen) {
        int xTile = 0;
        int yTile = 28;
        int walkingSpeed = 0;
        int flipTop = (numSteps >> walkingSpeed) & 1;
        int flipBottom = (numSteps >> walkingSpeed) + 1 & 1;
        
        if (movingDir == 1) {
            xTile += 2;
        } else if (movingDir > 1) {
            xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;
            flipTop = (movingDir - 1) % 2;
        }

        int modifier = 8 * scale;
        int xOffset = x - modifier / 2;
        int yOffset = y - modifier / 2 - 4;
        if(!isDead) {
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
	        if (!isSwimming) {
	            screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, colour,flipBottom, scale);
	            screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, colour, flipBottom, scale);
	        }
	        
	        if(!isBurning) {
	        	screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, colour, flipTop, scale);
	        	screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, colour, flipTop, scale);
	        }else {
	        	hp -= 1;
	        	screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, Colours.get(-1, 400, 444, 332), flipTop, scale);
	        	screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, Colours.get(-1, 400, 444, 332), flipTop,
	                scale);
	        	screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, Colours.get(-1, 400, 444, 332),
	                    flipBottom, scale);
	            screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile + 1)
	                    * 32, Colours.get(-1, 400, 444, 332), flipBottom, scale);
	        }
	        
    	}else {
    		screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, Colours.get(-1, 000, 000, 000), flipTop, scale);
        	screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, Colours.get(-1, 000, 000, 000), flipTop,
                scale);
        	screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, Colours.get(-1, 000, 000, 000),
                    flipBottom, scale);
            screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile + 1)
                    * 32, Colours.get(-1, 000, 000, 000), flipBottom, scale);
        }
        if (username != null) {
        	String disp = username+" "+String.valueOf(hp/100);
            Font.render(disp, screen, xOffset - ((disp.length() - 1) / 2 * 8), yOffset - 10,
                    Colours.get(-1, -1, -1, 555), 1);
        }
    }

    public boolean hasCollided(int xa, int ya) {
        int xMin = 0;
        int xMax = 7;
        int yMin = 3;
        int yMax = 7;
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya-8, x, yMin-8)) {
                return true;
            }
        }
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya-8, x, yMax-8)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya-8, xMin, y-8)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya, xMax, y-8)) {
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
    
    private void weaponQ(){
    	switch(weapon){
			case 1:	weapon = 3; break;
			case 2:	weapon = 1; break;
			case 3:	weapon = 2; break;
			default:	System.out.println("Problematic Situation");
    	}
    	printWeapon();
    }
    
    private void weaponE(){
    	switch(weapon){
			case 1:	weapon = 2; break;
			case 2:	weapon = 3; break;
			case 3:	weapon = 1; break;
			default:	System.out.println("Problematic Situation");
    	}
    	printWeapon();
    }
    
    public void checkTarget(){
    	List<Entity> ent = game.level.getEntities();
    	
    	for(Entity e:ent){
    		switch(weapon){
    			case 1: checkPistolRange(e); break;
    			case 2: checkShotgunRange(e); break;
    			case 3: checkSniperRange(e); break;
    		}
    	}
    }
    
    /** Display purposes **/
    
    public void printWeapon(){
    	String gun = "";
    	switch(weapon){
    		case 1:	gun = "Pistol"; break;
    		case 2:	gun = "Shotgun"; break;
    		case 3: gun = "Sniper"; break;
    		default: gun = "???"; break;
    	}
    	game.cc.gunField.setText(gun);
    }
    
    public void printHP(){
    	game.cc.hpField.setText(String.valueOf(hp/100));
    }
    
    /** For Checking **/
    
    private void checkPistolRange(Entity ent){
    	switch(direction){
    		case 'w':
    			if( x==ent.x && ( (y-8)==ent.y || (y-16)==ent.y ) ){
    				game.cc.callDmg(ent.username + "-_-" + "1");
    			}
    			break;
    		case 'a':
    			if( y==ent.y && ( (x-8)==ent.x || (x-16)==ent.x ) ){
    				game.cc.callDmg(ent.username + "-_-" + "1");
    			}
    			break;
    		case 's':
    			if( x==ent.x && ( (y+8)==ent.y || (y+16)==ent.y ) ){
    				game.cc.callDmg(ent.username + "-_-" + "1");
    			}
    			break;
    		case 'd':
    			if( y==ent.y && ( (x+8)==ent.x || (x+16)==ent.x ) ){
    				game.cc.callDmg(ent.username + "-_-" + "1");
    			}
    			break;
    		default:	System.out.println("Bakit may ganyan");
    	}
    }
    
    private void checkShotgunRange(Entity ent){
    	switch(direction){
	    	case 'w':
				if( ( ( (y-16)==ent.y || (y-24)==ent.y ) && ( x==ent.x || (x-8)==ent.x || (x+8)==ent.x ) ) ){
					game.cc.callDmg(ent.username + "-_-" + "2");
				}
				break;
			case 'a':
				if( ( ( (x-16)==ent.x || (x-24)==ent.x ) && ( y==ent.y || (y-8)==ent.y || (y+8)==ent.y ) ) ){
					game.cc.callDmg(ent.username + "-_-" + "2");
				}
				break;
			case 's':
				if( ( ( (y+16)==ent.y || (y+24)==ent.y ) && ( x==ent.x || (x-8)==ent.x || (x+8)==ent.x ) ) ){
					game.cc.callDmg(ent.username + "-_-" + "2");
				}
				break;
			case 'd':
				if( ( ( (x+16)==ent.x || (x+24)==ent.x ) && ( y==ent.y || (y-8)==ent.y || (y+8)==ent.y ) ) ){
					game.cc.callDmg(ent.username + "-_-" + "2");
				}
				break;
			default:	System.out.println("Bakit may ganyan");
    	}
    }
    
    private void checkSniperRange(Entity ent){
    	switch(direction){
			case 'w':
				if(x!=ent.x) return;
				if( (y-32)==ent.y || (y-40)==ent.y || (y-48)==ent.y ){
					game.cc.callDmg(ent.username + "-_-" + "3");
				}
				break;
			case 'a':
				if(y!=ent.y) return;
				if( (x-32)==ent.x || (x-40)==ent.x || (x-48)==ent.x ){
					game.cc.callDmg(ent.username + "-_-" + "3");
				}
				break;
			case 's':
				if(x!=ent.x) return;
				if( (y+32)==ent.y || (y+40)==ent.y || (y+48)==ent.y ){
					game.cc.callDmg(ent.username + "-_-" + "3");
				}
				break;
			case 'd':
				if(y!=ent.y) return;
				if( (x+32)==ent.x || (x+48)==ent.x || (x+48)==ent.x ){
					game.cc.callDmg(ent.username + "-_-" + "3");
				}
				break;
			default:	System.out.println("Bakit may ganyan");
    	}
    }
    
}
