
public class BoardConfig {
	public int[] pixelArray;
	public int[][][] bots; // outer array is the player number, within the bot number, within the x and y
							// coordinate
	public final int[] influenceRadii;

	public BoardConfig(int[] pixelArray, int[] influenceRadii) {
		this.pixelArray = new int[Util.BOARD_SIZE*Util.BOARD_SIZE];
		System.arraycopy(pixelArray, 0, this.pixelArray, 0, Util.BOARD_SIZE*Util.BOARD_SIZE);
		this.bots = new int[3][3][2]; // 3 players, 3 bots, 2 coordinates
		this.influenceRadii = influenceRadii;
	}

	public void moveBot(int playerID, int botID, int x, int y) {
		this.bots[playerID][botID][0] = x;
		this.bots[playerID][botID][1] = y;
	}

	public int getColor(int x, int y) {
		return this.pixelArray[y * Util.BOARD_SIZE + x];
	}

	public boolean isWalkable(int x, int y) {
		return this.getColor(x, y) != 0;
	}
}
