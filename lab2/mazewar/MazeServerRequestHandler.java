import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MazeServerRequestHandler extends Thread {
	
	private Socket socket = null;
	private int pCount = 0;
	public static ConcurrentSkipListMap<Integer, PlayerInfo> playerList = new ConcurrentSkipListMap<Integer, PlayerInfo>();

	public MazeServerRequestHandler(Socket socket) {
		super("MazeServerRequestHandler");
		this.socket = socket;
	}

	public void run() {
		boolean gotQuit = false;

		try {
			ObjectInputStream fromPlayer = new ObjectInputStream(socket.getInputStream());
			PlayerPacket pPacket;

			System.out.println("Waiting for players...");

			while( (pPacket = (PlayerPacket) fromPlayer.readObject()) != null) {

				PlayerPacket cPacket = new PlayerPacket();
				PlayerInfo pInfo = new PlayerInfo();

				if(pPacket.type == PlayerPacket.PLAYER_REGISTER) {
					if(pPacket.uID == -1) {
						pCount++;
						pInfo.hostName = pPacket.hostName;
						pInfo.playerName = pPacket.playerName;
						pInfo.uID = pCount;
						pInfo.listenPort = pPacket.listenPort;
					}
					else {
						System.err.println("ERROR: Duplicate Register Request! User with pID " + pPacket.uID + " already registered!");
						System.exit(-1);
					}
						
					playerList.put(pCount, pInfo);

					cPacket = pPacket;
					cPacket.type = PlayerPacket.PLAYER_REGISTER_REPLY;
					cPacket.uID = pCount;

					System.out.println("Registered user: " + pPacket.playerName + ", from: " + pPacket.hostName);

					// Add request to FIFO, should cause handler thread to wake up

					MazeServerProcessor.requestLog.put(cPacket);

					break;
				}
			}

			fromPlayer.close();
			socket.close();
		} catch (IOException e) {
			if(!gotQuit)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotQuit)
				e.printStackTrace();
		} catch (InterruptedException e) {
			if(!gotQuit)
				e.printStackTrace();
		}
	}
}
