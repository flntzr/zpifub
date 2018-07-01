public class Main {

	public static void main(String[] args) {		
		Main.startServer();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Main.startRandomBotThread("random1");
		Main.startRandomBotThread("random2");
		Main.startSmartBotThread("smart1");
	}
	
	private static void startRandomBotThread(String name) {
		RandomBot bot = new RandomBot(null, name, "Oof!");
		(new Thread(bot)).start();
	}
	
	private static void startSmartBotThread(String name) {
		SmartBot bot = new SmartBot(null, name, "Oof!");
		(new Thread(bot)).start();
	}
	
	private static void startServer() {
		String[] args = new String[0];
		ServerThread t = new ServerThread(args);
		(new Thread(t)).start();
	}

}
