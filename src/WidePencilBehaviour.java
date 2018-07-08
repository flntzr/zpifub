import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WidePencilBehaviour extends BotBehaviour{
	 int lastX = 0;
	    int lastY = 0;
	    long timeOfLastMove = 0;

	    public WidePencilBehaviour() {
	    	super();
	    }

	    @Override
	    public void run() {
	    	bot.aStarLayer = 5;
	    	bot.pointReachRange = 25;
			while (true) {
			    if (bot.searching) {
				bot.destination = this.findTargetPosition();
				bot.pathCoords = getPathToDestination();
				bot.pathIndex = 0;
				bot.searching = false;
			    } else {
				restartSearchIfNoMoveHappendInMilliseconds(1000);
			    }
		
			}
	    }

	    private int[][] getPathToDestination() {

			int[] startNew = new int[] { bot.board.bots[bot.playerNumber][bot.botId][0],
				bot.board.bots[bot.playerNumber][bot.botId][1] };
			startNew[0] = startNew[0] / (1 << bot.aStarLayer);
			startNew[1] = startNew[1] / (1 << bot.aStarLayer);
		
			int[] destNew = new int[] { bot.destination[0], bot.destination[1] };
			destNew[0] /= (1 << bot.aStarLayer);
			destNew[1] /= (1 << bot.aStarLayer);
			int[][] coords = AStar.search(startNew, destNew, bot.board.walklayer[bot.aStarLayer],
				bot.board.walklayer[bot.aStarLayer].length, null);
			return coords;
	    }

	    private void restartSearchIfNoMoveHappendInMilliseconds(int milliseconds) {
			int x = bot.board.bots[bot.playerNumber][bot.botId][0] - lastX;
			int y = bot.board.bots[bot.playerNumber][bot.botId][1] - lastY;
			if (x < 0) x = -x;
			if (y < 0) y = -y;
			if (x < 2 && y < 2) {
			    if (System.currentTimeMillis() - timeOfLastMove > milliseconds)
				bot.searching = true;
			} else {
			    lastX = bot.board.bots[bot.playerNumber][bot.botId][0];
			    lastY = bot.board.bots[bot.playerNumber][bot.botId][1];
			    timeOfLastMove = System.currentTimeMillis();
			}
	    }
	    
	    private int[] findTargetPosition() {
			// Wait for the heatmap to be filled initially, otherwise all scores are 0.
//			while (!bot.board.isScoreHeatmapInitialized) {
//			    try {
//			    	Thread.sleep(100);
//			    } 
//			    catch (InterruptedException e) {
//			    	
//			    }
//			}

			int pX = bot.board.bots[bot.playerNumber][bot.botId][0]/(1<<4);
			int py = bot.board.bots[bot.playerNumber][bot.botId][1]/(1<<4);
			int radius = 5;
			for(int x = -radius + pX; x < radius + pX; x++){
				for(int y = -radius + py; y < radius + py; y++) {
			    	System.out.println(x);
					if(x<0||x>=bot.board.layer[bot.aStarLayer].length || y<0||y>=bot.board.layer[bot.aStarLayer].length ) continue;
					if(bot.board.walklayer[bot.aStarLayer][x][y] == 0) continue;

					int color = bot.board.layer[bot.aStarLayer][x][y];
					int r = (color>> 16) & 0x000000ff;
					int g = (color>> 8) & 0x000000ff;
					int b = (color) & 0x000000ff;
					
					if(bot.playerNumber == 0){
						//System.out.println("Ich bin Rot");
						if(r<=g || r<=b){
							return new int[]{x*(1<<bot.aStarLayer),y*(1<<bot.aStarLayer)};
						}
					}
					
					else if(bot.playerNumber == 1){
						//System.out.println("Ich bin Grün");
						if(g<=r || g<=b){
							return new int[]{x*(1<<bot.aStarLayer),y*(1<<bot.aStarLayer)};
						}
					}

					else if(bot.playerNumber == 2){
						//System.out.println("Ich bin blau");
						if(b<=r || b<=g){
							return new int[]{x*(1<<bot.aStarLayer),y*(1<<bot.aStarLayer)};
						}
					}
				}	
			}
			
			while(true){
				int x = bot.random.nextInt(bot.board.layer[4].length);
				int y = bot.random.nextInt(bot.board.layer[4].length);
				if(bot.board.walklayer[4][x][y] == 0) continue;
				return new int[]{x*(1<<4),y*(1<<4)};
			
			}
	    }
}
