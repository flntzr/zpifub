import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SprayBehaviour extends BotBehaviour {



    int lastX = 0;
    int lastY = 0;
    long timeOfLastMove = 0;

    public SprayBehaviour() {
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
	while (!bot.board.isScoreHeatmapInitialized) {
	    try {
	    	Thread.sleep(100);
	    } 
	    catch (InterruptedException e) {
	    	
	    }
	}
	
	int heatmapSize = Util.BOARD_SIZE >> bot.aStarLayer;
	List<int[]> scores = new ArrayList<>(); // [x, y, score]
	for (int y = 0; y < heatmapSize; y++) {
	    for (int x = 0; x < heatmapSize; x++) {
		int[] score = new int[3];
		score[0] = x;
		score[1] = y;
		score[2] = bot.board.scoreHeatmap[bot.aStarLayer][x][y];
		scores.add(score);
	    }
	}
	Collections.sort(scores, new Comparator<int[]>() {
	    @Override
	    public int compare(int[] o1, int[] o2) {
		return -Integer.compare(o1[2], o2[2]);
	    }
	});

	// TODO also take the distance into account to find the best target
	int[] pickedScore = scores.get(0);
	int[] position = new int[2];
	int centerOffset = bot.aStarLayer == 0 ? 0 : 1 << (bot.aStarLayer - 1);
	position[0] = (pickedScore[0] << bot.aStarLayer) + centerOffset;
	position[1] = (pickedScore[1] << bot.aStarLayer) + centerOffset;
	return position;
    }
}
