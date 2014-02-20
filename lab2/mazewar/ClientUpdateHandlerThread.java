import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ClientUpdateHandlerThread extends Thread {

	private Socket socket = null;
	private Maze maze = null;

	public ClientUpdateHandlerThread(Socket socket, Maze maze) {
		super("ClientUpdateHandler");
		this.socket = socket;
		this.maze = maze;
	}

	public void run() {
		try {
			ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

			PlayerPacket pPacket = new PlayerPacket();

			while ( (pPacket = (PlayerPacket) fromServer.readObject()) != null) {

				if(pPacket.type == PlayerPacket.PLAYER_REGISTER_REPLY || pPacket.type == PlayerPacket.PLAYER_REGISTER_UPDATE) {

					System.out.println("Received Join from Player: " + pPacket.playerName);

					Client newClient = new RemoteClient(pPacket.playerName);

					ClientUpdateHandler.playerList.put(pPacket.uID, newClient);

					maze.addClient(newClient);

					fromServer.close();
					socket.close();

					break;
				} else if (pPacket.type == PlayerPacket.PLAYER_FORWARD) {
					Client updateClient = ClientUpdateHandler.playerList.get(pPacket.uID);

					if(maze.moveClientForward(updateClient)) {
                        updateClient.notifyMoveForward();
                	}

					break;
				} else if (pPacket.type == PlayerPacket.PLAYER_BACKUP) {

					Client updateClient = ClientUpdateHandler.playerList.get(pPacket.uID);

					if(maze.moveClientBackward(updateClient)) {
                        updateClient.notifyMoveBackward();
					}
					break;
                } else if (pPacket.type == PlayerPacket.PLAYER_LEFT) {
					Client updateClient = ClientUpdateHandler.playerList.get(pPacket.uID);

                	updateClient.notifyTurnLeft();

					break;
				} else if (pPacket.type == PlayerPacket.PLAYER_RIGHT) {	
					Client updateClient = ClientUpdateHandler.playerList.get(pPacket.uID);

                	updateClient.notifyTurnRight();

					break;
				} else if (pPacket.type == PlayerPacket.PLAYER_FIRE) {
					Client updateClient = ClientUpdateHandler.playerList.get(pPacket.uID);

					if(maze.clientFire(updateClient)) {
                        updateClient.notifyFire();
					}

					break;
				} else if (pPacket.type == PlayerPacket.PLAYER_QUIT) {

					System.out.println("Player quit..Ending game...");

					if(pPacket.uID == -1) {
						Mazewar.quit(1);
					} else {
						Mazewar.quit(0);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
