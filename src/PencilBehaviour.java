import java.util.Arrays;


class PencilBehaviour extends BotBehaviour{

		int lastX = 0;
		int lastY = 0;
		long timeOfLastMove = 0;

		public PencilBehaviour() {
			super();
		}

		@Override
		public void run() {
			while(true) {
				if(bot.searching) {
					System.out.println("Suche neues Ziel...");
					searchDestination();
					bot.pathCoords = getPathToDestination();
					bot.pathIndex = 0;
					bot.searching = false;
					System.out.println(bot.searching || bot.pathCoords.length == 0);
				} else{
					restartSearchIfNoMoveHappendInMilliseconds(1000);
				}

			}			
		}
		
		private void searchDestination() {
			int gegnerA = (bot.playerNumber + 1)%3;
			int botId = 0;			
			bot.destination[0] = bot.board.bots[gegnerA][botId][0];
			bot.destination[1] = bot.board.bots[gegnerA][botId][1];
		}
		
		private int[][] getPathToDestination(){

			int[] startNew = new int[]{bot.board.bots[bot.playerNumber][bot.botId][0],bot.board.bots[bot.playerNumber][bot.botId][1]};
			startNew[0] = startNew[0]/(1<<bot.aStarLayer); 
			startNew[1] = startNew[1]/(1<<bot.aStarLayer);
			
			int[] destNew = new int[]{bot.destination[0],bot.destination[1]};
			destNew[0] /=(1<<bot.aStarLayer); 
			destNew[1] /=(1<<bot.aStarLayer);
			int[][] coords = AStar.AStar(startNew,destNew,bot.board.walklayer[bot.aStarLayer],bot.board.walklayer[bot.aStarLayer].length,null);
			for(int i = 0; i< coords.length; i++){
				System.out.println(Arrays.toString(coords[i]));
			}
			return coords;
			
		}
		
		private void restartSearchIfNoMoveHappendInMilliseconds(int milliseconds){
			int x = bot.board.bots[bot.playerNumber][bot.botId][0] - lastX;
			int y = bot.board.bots[bot.playerNumber][bot.botId][1] - lastY;
			if(x < 0) x =- x;
			if(y < 0) y =- y;
			if(x<2 && y<2){
				if(System.currentTimeMillis()-timeOfLastMove>milliseconds) bot.searching = true;
			}
			else{
				lastX = bot.board.bots[bot.playerNumber][bot.botId][0];
				lastY = bot.board.bots[bot.playerNumber][bot.botId][1];
				timeOfLastMove=System.currentTimeMillis();
			}
		}
	}

