import java.util.Random;

public class BasicBot implements Runnable, BotInterface {

    public int[] direction;
    public Random random;
    public BoardConfig board;
    public int playerNumber;
    public final int botId;
    public Thread searchThread;
    public int[] destination = new int[] { 0, 0 };
    public int aStarLayer = 4;
    public int pointReachRange = 25; // Reichweite ab wann ein Punkt erreicht wurde
    public boolean searching = true;
    public int pathIndex;
    public int[][] pathCoords;
    public BotBehaviour botBehaviour;
    public boolean collectPowerup = false;

    public BasicBot(int playerNumber, BoardConfig board, int botId, BotBehaviour botBehaviour) {
	this.playerNumber = playerNumber;
	this.board = board;
	this.direction = new int[] { 0, 0 };
	this.random = new Random();
	this.botId = botId;
	this.botBehaviour = botBehaviour;
	this.botBehaviour.setBot(this);
    }

    public int[] getMoveDirection() {
	return this.direction;
    }

    @Override
    public void run() {
	searchThread = new Thread(botBehaviour);
	while (this.board.bots[this.playerNumber][botId][0] == 0) {

	    idleMove(); // Idle bis Erstininitialsierung
	}
	searchThread.start();
	while (true) {
	    if (searching || pathCoords == null || this.pathCoords.length == 0) {
		if (pathCoords == null || this.pathCoords.length == 0)
		    searching = true;
		idleMove(); // Idle wenn kein Ziel gefunden
	    } else {
		walkPath(); // Erstmal direkt aufs Ziel gehen
		// TODO: N�chster Schritt,Hier A* Einbinden und testen
	    }

	}
    }

    private void idleMove() {
	this.direction[0] = random.nextInt(100) - 50;
	this.direction[1] = random.nextInt(100) - 50;
    }

    private void walkPath() {

	if (walkToDestination(pathCoords[pathIndex][0] * (1 << aStarLayer),
		pathCoords[pathIndex][1] * (1 << aStarLayer), pointReachRange)) {
	    pathIndex++;
	    // System.out.println("next Step");
	    if (pathIndex >= pathCoords.length) {
		// System.out.println("Pfad beendet");
		searching = true;
	    }
	}

	// System.out.println(pathIndex+":"+pathCoords.length);
	if (pathIndex >= pathCoords.length) {
	    searching = true;
	    // System.out.println("Pfad beendet");
	}
    }

    private boolean walkToDestination(int destX, int destY, int range) {
	int posX = this.board.bots[this.playerNumber][botId][0];
	int posY = this.board.bots[this.playerNumber][botId][1];
	this.direction[0] = destX - posX;
	this.direction[1] = destY - posY;
	int x = destX - posX;
	int y = destY - posY;
	double length = Math.sqrt(x * x + y * y);

	if (length < range) {
	    return true;
	}
	return false;

    }

    @Override
    public void collectPowerUp(int[][] path) {
	pathIndex = 0;
	this.pathCoords = path;
	searching = false;
    }

}
