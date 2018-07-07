import java.util.ArrayList;
import java.util.List;

import lenz.htw.zpifub.Update;
import lenz.htw.zpifub.net.NetworkClient;

public class SmartBot implements Runnable {
    private String hostname;
    private String name;
    private String winMessage;
    private int playerNumber;
    private BoardConfig boardConfig;
    private StrategicMap map;
    private Thread pencilThread;
    PencilBot pencilBot;

    public SmartBot(String hostname, String name, String winMessage) {
	this.hostname = hostname;
	this.name = name;
	this.winMessage = winMessage;
    }

    @Override
    public void run() {
	NetworkClient client = new NetworkClient(this.hostname, this.name, this.winMessage);
	this.playerNumber = client.getMyPlayerNumber();
	this.boardConfig = Util.getInitialBoard(client);
	this.map = new StrategicMap(client, this.boardConfig, name == "smart1");
	Update update;
	client.setMoveDirection(0, 1, 1); // bot 0 go up-right
	// client.setMoveDirection(1, 1, -1); // bot 1 go down-right
	client.setMoveDirection(2, -1, 0); // bot 2 go left
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	map.initWalkMap();
	map.add(this.boardConfig.bots[2][1][0], this.boardConfig.bots[2][1][1], 1024);
	map.update(this.boardConfig.bots[2][2][0], this.boardConfig.bots[2][2][1], 1024);
	Thread heatmapUpdateThread = new Thread(new ScoreHeatmapUpdateThread(this.playerNumber, this.boardConfig));
	List<Thread> botThreads = new ArrayList<>();
	heatmapUpdateThread.start();
	botThreads.add(new Thread(new BrushThread(this.boardConfig, this.playerNumber)));
	for (Thread t : botThreads) {
	    System.out.println(this.boardConfig);
	    t.start();
	}
	this.pencilBot = new PencilBot(playerNumber, this.boardConfig);
	this.pencilThread = new Thread(pencilBot);
	this.pencilThread.start();

	while (true) {
	    map.add(this.boardConfig.bots[2][1][0], this.boardConfig.bots[2][1][1], 1024);
	    map.update(this.boardConfig.bots[2][2][0], this.boardConfig.bots[2][2][1], 1024);
	    // TODO only update direction if it has changed

//	    map.render();
	    if ((update = client.pullNextUpdate()) == null) {
		try {
		    Thread.sleep(20);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		continue;
	    }
	    if (update.type == null) {
		// System.out.println(this.playerNumber + ": " + "Bot " + update.bot + " of
		// player " + update.player
		// + " at " + update.x + ", " + update.y);
		this.boardConfig.moveBot(update.player, update.bot, update.x, update.y);
	    } else if (update.player == -1) {
		// update spawned, type, position
		// System.out.println(this.playerNumber + ": " + "Powerup at " + update.x + ", "
		// + update.y);
	    } else {
		// update collected
		// System.out.println("update!");
	    }
	    for (int i = 0; i < this.boardConfig.bots[this.playerNumber].length; i++) {
		int[] bot = this.boardConfig.bots[this.playerNumber][i];
		if (bot[0] == 0 && bot[1] == 0) {
		    // Skip this special case where the bot position has not been set yet.
		    continue;
		}
	    }


	    // Spray
	    // int[] direction = this.sprayBot.getMoveDirection();
	    // client.setMoveDirection(1, direction[0], direction[1]);

	    // Pencil
	    int[] direction = this.pencilBot.getMoveDirection();
	    client.setMoveDirection(1, direction[0], direction[1]);

	    // Broad Brush
	    // int[] direction = this.broadBrushBot.getMoveDirection();
	    // client.setMoveDirection(2, direction[0], direction[1]);
	}
    }
}
