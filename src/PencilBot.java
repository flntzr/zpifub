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
				walkToDestination(); //Erstmal direkt aufs Ziel gehen
				//TODO: Nächster Schritt,Hier A* Einbinden und testen
			}

		}
	}

	 
	
	private void idleMove() {
		this.direction[0] = random.nextInt(100)-50;
		this.direction[1] = random.nextInt(100)-50;
	}
	
	private void walkToDestination() {
		this.direction[0] = this.destination[0] - this.board.bots[this.playerNumber][botId][0];
		this.direction[1] = this.destination[1] - this.board.bots[this.playerNumber][botId][1];
		int x = this.destination[0]-this.board.bots[this.playerNumber][botId][0];
		int y = this.destination[1]-this.board.bots[this.playerNumber][botId][1];
		double length = Math.sqrt(x*x+y*y);

		if( length < 32 ) {
			searching = true;
		};
		
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
					searching = false;
				} else{
					restartSearchIfNoMoveHappendInMilliseconds(1000);
				}

			}			
		}
		
		private void restartSearchIfNoMoveHappendInMilliseconds(int milliseconds){
			if(board.bots[playerNumber][botId][0]==lastX &&board.bots[playerNumber][botId][1]==lastY){
				//counter++;
				if(System.currentTimeMillis()-timeOfLastMove>milliseconds) searching = true;
			}
			else{
				lastX = board.bots[playerNumber][botId][0];
				lastY = board.bots[playerNumber][botId][1];
				timeOfLastMove=System.currentTimeMillis();
			}
			System.out.println(System.currentTimeMillis()-timeOfLastMove);
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
	}
}
