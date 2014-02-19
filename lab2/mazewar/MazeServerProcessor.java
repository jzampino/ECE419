import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;

public class MazeServerProcessor extends Thread {

	public static LinkedBlockingQueue<PlayerPacket> requestLog = new LinkedBlockingQueue<PlayerPacket>();

	public void run() {
		boolean queueProcessing = true;

		try {
	  		while(queueProcessing) {
				PlayerPacket toProcess = (PlayerPacket) requestLog.take();

				System.out.println("Processing packet");
				
				for (Map.Entry<Integer, PlayerInfo> player : MazeServerRequestHandler.playerList.entrySet()) {
					PlayerInfo pInfo = player.getValue();

					System.out.println("Host: " + pInfo.hostName + " Port: " + pInfo.listenPort);

					Socket socket = new Socket(pInfo.hostName, pInfo.listenPort);

					ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
	
					toClient.writeObject(toProcess);
	
					toClient.close();
					socket.close();
				}
			}
		} catch (IOException e) {
			if(!queueProcessing)
				e.printStackTrace();
		} catch (InterruptedException e) {
			if(!queueProcessing)
				e.printStackTrace();
		}
	}
}
