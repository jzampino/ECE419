import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;

public class MazeServerProcessor extends Thread {

  	private int socketPort = -1;
	public static LinkedBlockingQueue<PlayerPacket> requestLog = new LinkedBlockingQueue<PlayerPacket>();

	public MazeServerProcessor(int socket) {
		super("MazeServerProcessor");
		this.socketPort = socket;
		System.out.println("Processor up and running...");
	}

	public void run() {
		boolean queueProcessing = true;

	  	while(queueProcessing) {
			PlayerPacket toProcess = (PlayerPacket) requestLog.take();
			
			for (Map.Entry<Integer, String> player : MazeServerRequestHandler.playerList.entrySet()) {
				String[] playerKey = player.getValue().split(".");
				Socket socket = new Socket(playerKey[1], this.socketPort);

				ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

				toClient.writeObject(toProcess);

				toClient.close();
				socket.close();
			}
		}
	}
}
