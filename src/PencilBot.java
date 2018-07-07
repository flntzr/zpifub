import java.util.List;
import java.util.Random;

public class PencilBot implements Runnable{

	public int[] direction;
	private Random random;
	private BoardConfig board;
	private int playerNumber;
	private final int botId = 1;
	private Thread searchThread;
	private int [] destination = new int[]{0,0};
	public PencilBot(int playerNumber,BoardConfig board) {
		this.playerNumber = playerNumber;
		this.board = board;
		this.direction = new int[]{0,0};
		this.random = new Random();
	}
	
	public int[] getMoveDirection() {
		return this.direction;
	}

	boolean searching = true;
	
	@Override
	public void run() {
		searchThread = new Thread(new GoalSearch());
		while(this.board.bots[this.playerNumber][botId][0] == 0){
			idleMove(); //Idle bis Erstininitialsierung
		}
		searchThread.start();
		while(true){

			if(searching) {
				idleMove(); //Idle wenn kein Ziel gefunden
			} else {
				walkPath(); //Erstmal direkt aufs Ziel gehen
				//TODO: Nächster Schritt,Hier A* Einbinden und testen
			}

		}
	}

	 
	
	private void idleMove() {
		this.direction[0] = random.nextInt(100)-50;
		this.direction[1] = random.nextInt(100)-50;
	}
	
	int pathIndex;
	int[] pathCoords;
	
	private void walkPath(){
		int id = pathIndex*2;
		if(walkToDestination(pathCoords[id],pathCoords[id+1],8)){
			pathIndex++;
			System.out.println("next Step");
		}
		
		if(pathIndex*2>=pathCoords.length){
			searching = true;			
		}
	}
	
	private boolean walkToDestination(int destX, int destY, int range) {
		this.direction[0] = destX - this.board.bots[this.playerNumber][botId][0];
		this.direction[1] = destY - this.board.bots[this.playerNumber][botId][1];
		int x = destX - this.board.bots[this.playerNumber][botId][0];
		int y = destY - this.board.bots[this.playerNumber][botId][1];
		double length = Math.sqrt(x*x+y*y);

		if( length < range ) {
			return true;
		}
		return false;
		
	}
	
	


	private void pattern() {
		int bottom = getBottomBorder();
	}
	
	private int getBottomBorder() {
		int x = board.bots[playerNumber][botId][0];
		int y = board.bots[playerNumber][botId][1];
		for(int i = y; i < board.walklayer[4].length; i++) {
			if(board.walklayer[4][x][i]==0) return i--;
		}
		return y;
	}
	
//	public void ourTurn() {
//
//		//Spieler 2 ist Blau
//		//Bot 0 ist Spraydose
//		//Bot 1 ist Spraydose
////		if(path!=null && path.size()==0){
////			path = null;
////		}
//		
//		if(this.name == "smart1" && path == null){
//			int[] pos = map.getValuableDestination(boardConfig.bots[this.playerNumber][1][0], boardConfig.bots[this.playerNumber][1][1], this.playerNumber);
//			// runOnce = false;
////			client.setMoveDirection(0, 1, 1); // bot 0 go up-right
//			client.setMoveDirection(1, 1, -1); // bot 1 go down-right
////			client.setMoveDirection(2, -1, 0); // bot 2 go left			
//			path = this.boardConfig.aStar(20*64+10,40*64+40, 4);
//		}
//
//		if(path!=null && path.size()>0){
//			
//			if(next == false) {
//			int bX = boardConfig.bots[this.playerNumber][1][0]-lastX;
//			int bY = boardConfig.bots[this.playerNumber][1][1]-lastY;
//
//			}
//			int index = path.remove(0);
//
//			int x = index % 64;
//			int y = index / 64;
//			int dirX = (x*64)-boardConfig.bots[this.playerNumber][1][0];
//			int dirY = (y*64)-boardConfig.bots[this.playerNumber][1][1];
//			client.setMoveDirection(1, dirX, dirY); // bot 1 go down-right
//			System.out.println(path.size());
//			
//		}
//		
////		if (!debugRunning) {
////			debugRunning = true;
////			System.out.println("Start A*");
////			List<Integer> path = this.boardConfig.aStar(20*64+10,
////					 40*64+40, 4);
////			System.out.println(path);
////		}
//	}

	
	class GoalSearch implements Runnable{
		int lastX = 0;
		int lastY = 0;
		long timeOfLastMove = 0;
		public GoalSearch() {

		}

		@Override
		public void run() {
			while(true) {
				if(searching) {
					searchDestination();
					pathCoords = getPathToDestination();
					pathIndex = 0;
					searching = false;
				} else{
					restartSearchIfNoMoveHappendInMilliseconds(1000);
				}

			}			
		}
		
		private void searchDestination() {
			int layer = 0;
			do {
				//Erstmal nur Random ein begehbares Ziel suchen
				//TODO: Hier mit Bewertungsfunktion suchen
				destination[0] = random.nextInt(board.layer[layer].length);
				destination[1] = random.nextInt(board.layer[layer].length);
			} while(board.layer[layer][destination[0]][destination[1]]==0);

		}
		
		private int[] getPathToDestination(){			
			List<Integer> path = board.aStar(board.bots[playerNumber][botId][0], board.bots[playerNumber][botId][1], destination[0], destination[1], 4);
			int[] coords = new int[path.size()*2];
			for(int i = 0; i < path.size(); i++){
				coords[i*2] = path.get(i)%64; 	//TODO sollte layerunabhägig sein
				coords[i*2 + 1] = path.get(i)/64;//TODO sollte layerunabhägig sein
			}
			System.out.println(coords.length+"Length");
			return coords;
		}
		
		private void restartSearchIfNoMoveHappendInMilliseconds(int milliseconds){
			int x = board.bots[playerNumber][botId][0] - lastX;
			int y = board.bots[playerNumber][botId][1] - lastY;
			if(x < 0) x =- x;
			if(y < 0) y =- y;
			if(x<2 && y<2){
				//counter++;
				if(System.currentTimeMillis()-timeOfLastMove>milliseconds) searching = true;
			}
			else{
				lastX = board.bots[playerNumber][botId][0];
				lastY = board.bots[playerNumber][botId][1];
				timeOfLastMove=System.currentTimeMillis();
			}
			//System.out.println(System.currentTimeMillis()-timeOfLastMove);
		}
	}
}
