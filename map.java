import java.util.Random;

public class map {
	private char[][] map;
	private int size;
	private int players = 0;
	private char[] sprite = {'A','B','C','D','E','F','G','H','I','J','#'};
	public player[] playerList = new player[10];
	
	public map(int mapSize){
		size = mapSize+2;
		map = new char[size][size];
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				if(i==0||i==size-1){
					map[i][j] = '*';
				}else if(j==0 || j==size-1){
					map[i][j] = '*';
				}else{
					map[i][j] = '.';
				}
			}
		}
	}
	
	public char[][] getMap(){
		return map;
	}
	
	public char getSprite(int arg){
		return sprite[arg];
	}
	
	public void newPlayer(int arg){
		if(players>=8){
			System.out.println("Sobra na!");
			return;
		}
		playerList[arg] = new player();
		playerList[arg].sprite = sprite[arg];
		Random rand = new Random();
		while(true){
			int r = rand.nextInt(50);
			int c = rand.nextInt(50);
			if(map[r][c]=='.'){
				playerList[arg].rPos = r;
				playerList[arg].cPos = c;
				map[r][c] = sprite[arg];
				break;
			}
		}
		players++;
		System.out.println(players);
	}
	
	public void remPlayer(int arg){
		player pp = playerList[arg];
		map[pp.rPos][pp.cPos] = '.';
		playerList[arg] = null;
		players--;
	}
	
	class player{
		public int hp = 100;
		public int rPos, cPos;
		public char dir = 'w';
		public char sprite;

		public void attack(){
			switch(dir){
				case 'w':
					if(map[rPos-1][cPos] != sprite && map[rPos-1][cPos] != '.' && map[rPos-1][cPos] != '*'){
						dmg(map[rPos-1][cPos]);
					}else if(map[rPos-2][cPos] != sprite && map[rPos-2][cPos] != '.' && map[rPos-2][cPos] != '*'){
						dmg(map[rPos-2][cPos]);
					}else if(map[rPos-3][cPos] != sprite && map[rPos-3][cPos] != '.' && map[rPos-3][cPos] != '*'){
						dmg(map[rPos-3][cPos]);
					}
					break;
				case 's':
					if(map[rPos+1][cPos] != sprite && map[rPos+1][cPos] != '.' && map[rPos+1][cPos] != '*'){
						dmg(map[rPos+1][cPos]);
					}else if(map[rPos+2][cPos] != sprite && map[rPos+2][cPos] != '.' && map[rPos+2][cPos] != '*'){
						dmg(map[rPos+2][cPos]);
					}else if(map[rPos+3][cPos] != sprite && map[rPos+3][cPos] != '.' && map[rPos+3][cPos] != '*'){
						dmg(map[rPos+3][cPos]);
					}
					break;
				case 'a':
					if(map[rPos][cPos-1] != sprite && map[rPos][cPos-1] != '.' && map[rPos][cPos-1] != '*'){
						dmg(map[rPos][cPos-1]);
					}else if(map[rPos][cPos-2] != sprite && map[rPos][cPos-2] != '.' && map[rPos][cPos-2] != '*'){
						dmg(map[rPos][cPos-2]);
					}else if(map[rPos][cPos-3] != sprite && map[rPos][cPos-3] != '.' && map[rPos][cPos-3] != '*'){
						dmg(map[rPos][cPos-3]);
					}
					break;
				case 'd':
					if(map[rPos][cPos+1] != sprite && map[rPos][cPos+1] != '.' && map[rPos][cPos+1] != '*'){
						dmg(map[rPos][cPos+1]);
					}else if(map[rPos][cPos+2] != sprite && map[rPos][cPos+2] != '.' && map[rPos][cPos+2] != '*'){
						dmg(map[rPos][cPos+2]);
					}else if(map[rPos][cPos+3] != sprite && map[rPos][cPos+3] != '.' && map[rPos][cPos+3] != '*'){
						dmg(map[rPos][cPos+3]);
					}
					break;
				default:
					System.out.println("what?");
			}
		}
		
		public void rotL(){
			switch(dir){
				case 'w': dir='a'; break;
				case 'a': dir='s'; break;
				case 's': dir='d'; break;
				default: dir='w';
			}
		}
		public void rotR(){
			switch(dir){
				case 'w': dir='d'; break;
				case 'a': dir='w'; break;
				case 's': dir='a'; break;
				default: dir='s';
			}
		}
		public void move(){
			switch(dir){
				case 'w':
					if(map[rPos-1][cPos]!='.')
						return;
					map[rPos][cPos]='.'; rPos--;
					break;
				case 'a':
					if(map[rPos][cPos-1]!='.')
						return;
					map[rPos][cPos]='.'; cPos--;
					break;
				case 's':
					if(map[rPos+1][cPos]!='.')
						return;
					map[rPos][cPos]='.'; rPos++;
					break;
				default:
					if(map[rPos][cPos+1]!='.')
						return;
					map[rPos][cPos]='.'; cPos++;
			}
			map[rPos][cPos] = sprite;
		}
		public void back(){
			switch(dir){
				case 's':
					if(map[rPos-1][cPos]!='.')
						return;
					map[rPos][cPos]='.'; rPos--;
					break;
				case 'd':
					if(map[rPos][cPos-1]!='.')
						return;
					map[rPos][cPos]='.'; cPos--;
					break;
				case 'w':
					if(map[rPos+1][cPos]!='.')
						return;
					map[rPos][cPos]='.'; rPos++;
					break;
				default:
					if(map[rPos][cPos+1]!='.')
						return;
					map[rPos][cPos]='.'; cPos++;
			}
			map[rPos][cPos] = sprite;
		}
		public void dmg(char eSprite){
			for(int i=0; i<10; i++){
				if(eSprite == playerList[i].sprite){
					playerList[i].hp = playerList[i].hp - 10;
					return;
				}
			}
		}
	}
	
}
