import java.util.Arrays;


class PencilBehaviour extends BotBehaviour{

		int lastX = 0;
		int lastY = 0;
		long timeOfLastMove = 0;
		int gegnerId;
		int botId;	
		public PencilBehaviour() {
			super();
		}

		@Override
		public void run() {
			gegnerId = (bot.playerNumber + (bot.random.nextInt(1)+1))%3;
			botId =	bot.random.nextInt(3);
//			System.out.println(this.bot.playerNumber);
			System.out.println(gegnerId);
			System.out.println(botId);

			while(true) {
				if(bot.searching) {
//					System.out.println("Suche neues Ziel...");
					searchDestination();

					bot.pathCoords = getPathToDestination();

					bot.pathIndex = 0;
					bot.searching = false;
					//System.out.println(bot.searching || bot.pathCoords.length == 0);
				} else{
					//System.out.println("Test");
					restartSearchIfNoMoveHappendInMilliseconds(1000);
				}

			}			
		}
		
		private void searchDestination() {
			
			bot.destination[0] = bot.board.bots[gegnerId][botId][0];
			bot.destination[1] = bot.board.bots[gegnerId][botId][1];
		}
		
		private int[][] getPathToDestination(){

			int[] startNew = new int[]{bot.board.bots[bot.playerNumber][bot.botId][0],bot.board.bots[bot.playerNumber][bot.botId][1]};
			startNew[0] = startNew[0]/(1<<bot.aStarLayer); 
			startNew[1] = startNew[1]/(1<<bot.aStarLayer);
			
			int[] destNew = new int[]{bot.destination[0],bot.destination[1]};
			destNew[0] /=(1<<bot.aStarLayer); 
			destNew[1] /=(1<<bot.aStarLayer);
			int[][] coords = AStar.search(startNew,destNew,bot.board.walklayer[bot.aStarLayer],bot.board.walklayer[bot.aStarLayer].length, bot.board.scoreHeatmap[bot.aStarLayer]);
//			System.out.println("Start"+Arrays.toString(startNew));
//			System.out.println("Ende"+Arrays.toString(destNew));
//			for(int i = 0; i<coords.length; i++){
//				System.out.println(Arrays.toString(coords[i]));
//			}
			return coords;
			
		}
		
		private void restartSearchIfNoMoveHappendInMilliseconds(int milliseconds){
			int x = bot.board.bots[bot.playerNumber][bot.botId][0] - lastX;
			int y = bot.board.bots[bot.playerNumber][bot.botId][1] - lastY;
			if(x < 0) x =- x;
			if(y < 0) y =- y;
			if(x<2 && y<2){
				//if(System.currentTimeMillis()-timeOfLastMove>milliseconds) bot.searching = true;
			}
			else{
				lastX = bot.board.bots[bot.playerNumber][bot.botId][0];
				lastY = bot.board.bots[bot.playerNumber][bot.botId][1];
				timeOfLastMove=System.currentTimeMillis();
			}
		}
	}

