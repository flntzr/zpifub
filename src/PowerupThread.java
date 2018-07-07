import lenz.htw.zpifub.PowerupType;

public class PowerupThread implements Runnable {
    private final int x;
    private final int y;
    private final int playerID;
    private final PowerupType powerupType;
    private final BoardConfig boardConfig;

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
	    // TODO avoid this powerup
	    return;
	}
	int[][][] paths = new int[3][3][];
	int fastestPlayer = 0;
	int fastestBot = 0;
	float fastestTravelTime = Integer.MAX_VALUE;
	for (int playerIndex = 0; playerIndex < 3; playerIndex++) {
	    for (int botIndex = 0; botIndex < 3; botIndex++) {
		int x = this.boardConfig.bots[playerIndex][botIndex][0];
		int y = this.boardConfig.bots[playerIndex][botIndex][1];
		paths[playerIndex][botIndex] = this.boardConfig.aStar(x, y, this.x, this.y, 4);
		float travelTime = Util.BOT_SPEEDS[botIndex] * paths[playerIndex][botIndex].length;
		if (travelTime < fastestTravelTime) {
		    fastestPlayer = playerIndex;
		    fastestBot = botIndex;
		    fastestTravelTime = travelTime;
		}
	    }
	}
	if (fastestPlayer == this.playerID) {
	    BotInterface bot = this.boardConfig.botInstances.get(fastestBot);
	    bot.collectPowerUp(paths[fastestPlayer][fastestBot]);
	}
    }

}
