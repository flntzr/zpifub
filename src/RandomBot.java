import java.util.concurrent.ThreadLocalRandom;

import lenz.htw.zpifub.Update;
import lenz.htw.zpifub.net.NetworkClient;

public class RandomBot implements Runnable {
	private String hostname;
	private String name;
	private String winMessage;
	private int playerNumber;
	private BoardConfig boardConfig;
	private final int[][] randomMoves = {
			{1, 0},
			{-1, 0},
			{0, 1},
			{0, -1},
			{1, 1},
			{1, -1},
			{-1, 1},
			{-1, -1}
	};
	StrategicMap map;

	public RandomBot(String hostname, String name, String winMessage) {
		this.hostname = hostname;
		this.name = name;
		this.winMessage = winMessage;

	}

	@Override
	public void run() {
		NetworkClient client = new NetworkClient(this.hostname, this.name, this.winMessage);
		this.playerNumber = client.getMyPlayerNumber();
		this.boardConfig = Util.getInitialBoard(client);
		
//		map = new StrategicMap(client, this.boardConfig, false);
		Update update;
		client.setMoveDirection(0, 1, 0); // bot 0 go right
		client.setMoveDirection(1, -1, 0); // bot 1 go left
		client.setMoveDirection(2, 0, 1); // bot 2 go up
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
//			map.add(0, 0, Util.BOARD_SIZE);
//			map.update(0, 0, Util.BOARD_SIZE);

			 if(name=="random1"){
				//map.update(0,0,512);
				//map.render(0,0,512);
			 }

			if ((update = client.pullNextUpdate()) == null) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			if (update.type == null) {
				this.boardConfig.moveBot(update.player, update.bot, update.x, update.y);
			} else if (update.player == -1) {
				// update spawned, type, position
			} else {
				// update collected

			}
			for (int i = 0; i < this.boardConfig.bots[this.playerNumber].length; i++) {
				int[] direction = this.pickStupidMove();

				client.setMoveDirection(i, direction[0], direction[1]);
			}
		}
	}

	private int[] pickStupidMove() {
		int randomNum = ThreadLocalRandom.current().nextInt(0, this.randomMoves.length);
		return this.randomMoves[randomNum];
	}
}
