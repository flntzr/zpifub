import lenz.htw.zpifub.PowerupType;

public class PowerupThread implements Runnable {
    private final int x;
    private final int y;
    private final int playerID;
    private final PowerupType powerupType;
    private final BoardConfig boardConfig;
    private static final int A_STAR_LAYER = 4;

    public PowerupThread(BoardConfig boardConfig, int x, int y, PowerupType powerupType, int playerID) {
	this.x = x;
	this.y = y;
	this.powerupType = powerupType;
	this.boardConfig = boardConfig;
	this.playerID = playerID;
    }

    @Override
    public void run() {
	if (this.powerupType.name().equals("SLOW")) {
	    this.boardConfig.addSlowPowerup(x, y);
	    return;
	}
	int[][][][] paths = new int[3][3][][];
	int fastestPlayer = 0;
	int fastestBot = 0;
	float fastestTravelTime = Integer.MAX_VALUE;
	// for (int playerIndex = 0; playerIndex < 3; playerIndex++) {
	int playerIndex = this.playerID;
	for (int botIndex = 0; botIndex < 3; botIndex++) {
	    // int x = this.boardConfig.bots[playerIndex][botIndex][0];
	    // int y = this.boardConfig.bots[playerIndex][botIndex][1];
	    // int[] dest = new int[] { x, y };
	    int[] startNew = new int[] { this.boardConfig.bots[playerIndex][botIndex][0],
		    this.boardConfig.bots[playerIndex][botIndex][1] };
	    startNew[0] = startNew[0] / (1 << A_STAR_LAYER);
	    startNew[1] = startNew[1] / (1 << A_STAR_LAYER);

	    int[] destNew = new int[] { x, y };
	    destNew[0] /= (1 << A_STAR_LAYER);
	    destNew[1] /= (1 << A_STAR_LAYER);

	    int[][] coords = AStar.search(startNew, destNew, this.boardConfig.layer[A_STAR_LAYER],
		    Util.BOARD_SIZE >> 4, null);

	    // int[][] coords = AStar.search(this.boardConfig.bots[playerIndex][botIndex],
	    // destNew, this.boardConfig.layer[4],
	    // Util.BOARD_SIZE >> 4, null);
	    paths[playerIndex][botIndex] = coords;
	    float travelTime = Util.BOT_SPEEDS[botIndex] * paths[playerIndex][botIndex].length;
	    if (travelTime < fastestTravelTime) {
		fastestPlayer = playerIndex;
		fastestBot = botIndex;
		fastestTravelTime = travelTime;
	    }
	    // }
	}
	if (fastestPlayer == this.playerID) {
	    BotInterface bot = this.boardConfig.botInstances.get(fastestBot);
	    bot.collectPowerUp(paths[fastestPlayer][fastestBot]);
	}
    }

}
