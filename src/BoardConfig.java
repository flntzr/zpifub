
public class BoardConfig {
	public int[] pixelArray;
	public int[][] bots; // outer array is the player number, inner array is the bot number

	public BoardConfig(int[] pixelArray) {
		this.pixelArray = new int[Util.BOARD_SIZE];
		System.arraycopy(pixelArray, 0, this.pixelArray, 0, Util.BOARD_SIZE);
	}
}
