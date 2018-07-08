import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SprayBehaviour extends BotBehaviour {

    private static final int BOT_ID = 0;
    private static final int A_STAR_LAYER = 4;
    int lastX = 0;
    int lastY = 0;
    long timeOfLastMove = 0;

    public SprayBehaviour() {
	super();
    }

    @Override
    public void run() {
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
	int[][] coords = AStar.AStar(startNew, destNew, bot.board.walklayer[bot.aStarLayer],
		bot.board.walklayer[bot.aStarLayer].length, null);
	return coords;

    }

    private void restartSearchIfNoMoveHappendInMilliseconds(int milliseconds) {
	int x = bot.board.bots[bot.playerNumber][bot.botId][0] - lastX;
	int y = bot.board.bots[bot.playerNumber][bot.botId][1] - lastY;
	if (x < 0)
	    x = -x;
	if (y < 0)
	    y = -y;
	if (x < 2 && y < 2) {
	    if (System.currentTimeMillis() - timeOfLastMove > milliseconds)
		bot.searching = true;
	} else {
	    lastX = bot.board.bots[bot.playerNumber][bot.botId][0];
	    lastY = bot.board.bots[bot.playerNumber][bot.botId][1];
	    timeOfLastMove = System.currentTimeMillis();
	}
    }

    // private final int playerID;
    // private final BoardConfig boardConfig;
    // private int pathIndex;
    // private int[][] pathCoords;
    // private int[] direction = { 0, 0 };
    // private int[] destination = new int[] { 0, 0 };
    // private boolean searching;
    // private Random random;
    //
    // public BrushThread(BoardConfig boardConfig, int playerID) {
    // this.boardConfig = boardConfig;
    // this.playerID = playerID;
    // this.searching = true;
    // random = new Random();
    // }
    //
    // @Override
    // public void run() {
    // // find the highest scored tile to go to
    // // int[] startPos = this.boardConfig.bots[this.playerID][BOT_ID];
    // // int[] goalPos = this.findTargetPosition();
    // Thread searchThread = new Thread(new GoalSearch());
    // while (this.boardConfig.bots[this.playerID][BOT_ID][0] == 0) {
    // idleMove(); // Idle bis Erstininitialsierung
    // }
    // searchThread.start();
    // while (true) {
    // if (searching || pathCoords == null || this.pathCoords.length == 0) {
    // if (pathCoords == null || this.pathCoords.length == 0)
    // searching = true;
    // idleMove(); // Idle wenn kein Ziel gefunden
    // } else {
    // walkPath(); // Erstmal direkt aufs Ziel gehen
    // // TODO: N�chster Schritt,Hier A* Einbinden und testen
    // }
    //
    // }
    // }
    //
    // private void idleMove() {
    // this.direction[0] = random.nextInt(100) - 50;
    // this.direction[1] = random.nextInt(100) - 50;
    // }
    //
    // private void walkPath() {
    // if (walkToDestination(this.pathCoords[pathIndex][0] * (1 << A_STAR_LAYER),
    // pathCoords[pathIndex][1] * (1 << A_STAR_LAYER), 12)) {
    // pathIndex++;
    // if (pathIndex >= pathCoords.length) {
    // searching = true;
    // }
    // }
    // if (pathIndex >= pathCoords.length) {
    // searching = true;
    // }
    // }
    //
    // private boolean walkToDestination(int destX, int destY, int range) {
    // int posX = this.boardConfig.bots[this.playerID][BOT_ID][0];
    // int posY = this.boardConfig.bots[this.playerID][BOT_ID][1];
    // this.direction[0] = destX - posX;
    // this.direction[1] = destY - posY;
    // int x = destX - posX;
    // int y = destY - posY;
    // double length = Math.sqrt(x * x + y * y);
    // if (length < range) {
    // return true;
    // }
    // return false;
    //
    // }
    //
    // @Override
    // public void collectPowerUp(int[] path) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public int[] getMoveDirection() {
    // return direction;
    // }
    //
    // class GoalSearch implements Runnable {
    // int lastX = 0;
    // int lastY = 0;
    // long timeOfLastMove = 0;
    //
    // public GoalSearch() {
    //
    // }
    //
    // @Override
    // public void run() {
    // while (true) {
    // if (searching) {
    // destination = this.findTargetPosition();
    // pathCoords = getPathToDestination();
    // pathIndex = 0;
    // searching = false;
    // System.out.println(searching || pathCoords.length == 0);
    // } else {
    // restartSearchIfNoMoveHappendInMilliseconds(1000);
    // }
    //
    // }
    // }
    //
    private int[] findTargetPosition() {
	// Wait for the heatmap to be filled initially, otherwise all scores are 0.
	while (!bot.board.isScoreHeatmapInitialized) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
	    }
	}
	int heatmapSize = Util.BOARD_SIZE >> A_STAR_LAYER;
	List<int[]> scores = new ArrayList<>(); // [x, y, score]
	for (int y = 0; y < heatmapSize; y++) {
	    for (int x = 0; x < heatmapSize; x++) {
		int[] score = new int[3];
		score[0] = x;
		score[1] = y;
		score[2] = bot.board.scoreHeatmap[A_STAR_LAYER][x][y];
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
	int centerOffset = A_STAR_LAYER == 0 ? 0 : 1 << (A_STAR_LAYER - 1);
	position[0] = (pickedScore[0] << A_STAR_LAYER) + centerOffset;
	position[1] = (pickedScore[1] << A_STAR_LAYER) + centerOffset;
	return position;
    }
    //
    // // private void searchDestination() {
    // //
    // // int gegnerA = (playerID + 1) % 3;
    // // int bot = 0;
    // // destination[0] = boardConfig.bots[gegnerA][bot][0];
    // // destination[1] = boardConfig.bots[gegnerA][bot][1];
    // //
    // // // do {
    // // // //Erstmal nur Random ein begehbares Ziel suchen
    // // // //TODO: Hier mit Bewertungsfunktion suchen
    // // // destination[0] = random.nextInt(board.layer[aStarLayer].length);
    // // // destination[1] = random.nextInt(board.layer[aStarLayer].length);
    // // // }
    // while(board.walklayer[aStarLayer][destination[0]][destination[1]]==0);
    // // // destination[0] *= (1<<aStarLayer);
    // // // destination[1] *= (1<<aStarLayer);
    // //
    // // }
    //
    // private int[][] getPathToDestination() {
    // int[] startNew = new int[] { boardConfig.bots[playerID][BOT_ID][0],
    // boardConfig.bots[playerID][BOT_ID][1] };
    // startNew[0] = startNew[0] / (1 << A_STAR_LAYER);
    // startNew[1] = startNew[1] / (1 << A_STAR_LAYER);
    //
    // int[] destNew = new int[] { destination[0], destination[1] };
    // destNew[0] /= (1 << A_STAR_LAYER);
    // destNew[1] /= (1 << A_STAR_LAYER);
    // int[][] coords = AStar.AStar(startNew, destNew,
    // boardConfig.walklayer[A_STAR_LAYER],
    // boardConfig.walklayer[A_STAR_LAYER].length,
    // boardConfig.scoreHeatmap[A_STAR_LAYER]);
    // return coords;
    // }
    //
    // private void restartSearchIfNoMoveHappendInMilliseconds(int milliseconds) {
    // int x = boardConfig.bots[playerID][BOT_ID][0] - lastX;
    // int y = boardConfig.bots[playerID][BOT_ID][1] - lastY;
    // if (x < 0) {
    // x = -x;
    // }
    // if (y < 0) {
    // y = -y;
    // }
    // if (x < 2 && y < 2) {
    // // counter++;
    // if (System.currentTimeMillis() - timeOfLastMove > milliseconds) {
    // searching = true;
    // }
    // } else {
    // lastX = boardConfig.bots[playerID][BOT_ID][0];
    // lastY = boardConfig.bots[playerID][BOT_ID][1];
    // timeOfLastMove = System.currentTimeMillis();
    // }
    // }
    // }
}
