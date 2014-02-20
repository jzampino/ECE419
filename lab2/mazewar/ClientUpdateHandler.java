import java.net.*;
import java.io.*;

public class ClientUpdateHandler extends Thread {

	Maze maze = null;
	int listenPort;

	public ClientUpdateHandler (Maze maze, int listenPort) {
		super("ClientUpdateHandler");
		this.maze = maze;
		this.listenPort = listenPort;
	}

	public void run() {

		try {
			ServerSocket socket = new ServerSocket(this.listenPort);

			boolean listening = true;

			while(listening) {
				new ClientUpdateHandlerThread(socket.accept(), maze).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
