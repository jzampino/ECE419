import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MazeServerRequestHandler extends Thread {
	
	private Socket socket = null;

	public MazeServerRequestHandler(Socket socket) {
		super("MazeServerRequestHandler");
		this.socket = socket;
	}

	public void run() {

		try {
			ObjectInputStream fromPlayer = new ObjectInputStream(socket.getInputStream());
			PlayerPacket pPacket;

			while( (pPacket = (PlayerPacket) fromPlayer.readObject()) != null) {

				PlayerPacket cPacket = new PlayerPacket();
				PlayerInfo pInfo = new PlayerInfo();

				if(pPacket.type == PlayerPacket.PLAYER_REGISTER) {
					if(pPacket.uID == -1) {
						MazeServer.pCount++;
						pInfo.hostName = pPacket.hostName;
						pInfo.playerName = pPacket.playerName;
						pInfo.uID = MazeServer.pCount;
						pInfo.listenPort = pPacket.listenPort;
					}
					else {
						System.err.println("ERROR: Duplicate Register Request! User with pID " + pPacket.uID + " already registered!");
						System.exit(-1);
					}
						
					MazeServer.playerList.put(MazeServer.pCount, pInfo);

					cPacket = pPacket;
					cPacket.type = PlayerPacket.PLAYER_REGISTER_REPLY;
					cPacket.uID = MazeServer.pCount;

					System.out.println("Registered user: " + pPacket.playerName + ", from: " + pPacket.hostName);

					// Add request to FIFO, should cause handler thread to wake up

					MazeServer.requestLog.put(cPacket);

					break;
				}
			}

			fromPlayer.close();
			socket.close();
		} catch (IOException e) {
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
	}
}
