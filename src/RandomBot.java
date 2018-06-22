import lenz.htw.zpifub.Update;
import lenz.htw.zpifub.net.NetworkClient;

public class RandomBot implements Runnable {
	private String hostname;
	private String name;
	private String winMessage;
	private int playerNumber;
	private BoardConfig boardConfig;

	public RandomBot(String hostname, String name, String winMessage) {
		this.hostname = hostname;
		this.name = name;
		this.winMessage = winMessage;
	}

	@Override
	public void run() {
		NetworkClient client = new NetworkClient(this.hostname, this.name, this.winMessage);
		this.playerNumber = client.getMyPlayerNumber();
		this.boardConfig = this.getInitialBoard(client);
		Update update;
		client.setMoveDirection(0, 1, 0); // bot 0 go right
		client.setMoveDirection(1, -1, 0); // bot 1 go left
		client.setMoveDirection(2, 0, 1); // bot 2 go up
		while ((update = client.pullNextUpdate()) != null) {
		    if (update.type == null) {
		    	System.out.println("Bot at " + update.x + ", " + update.y);
//		        bot[update.player][update.bot].pos = update.x, update.y
		    } else if (update.player == -1) {
		        //update spawned, type, position
		    	System.out.println("Powerup at " + update.x + ", " + update.y);
		    } else {
		        //update collected
		    	System.out.println("update!");
		    }
		}
	}

	private BoardConfig getInitialBoard(NetworkClient client) {
		int[] pixelArray = new int[Util.BOARD_SIZE * Util.BOARD_SIZE];
		for (int y = 0; y < Util.BOARD_SIZE; y++) {
			for (int x = 0; x < Util.BOARD_SIZE; x++) {
				int val = client.getBoard(x, y);
				pixelArray[y * Util.BOARD_SIZE + x] = client.getBoard(x, y);
			}
		}
		return new BoardConfig(pixelArray);
	}

}
