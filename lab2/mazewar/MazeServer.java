import java.net.*;
import java.io.*;

public class MazeServer {

	public static void main(String[] args) throws IOException {

		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			if (args.length == 2) {
				serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				new MazeServerProcessor(Integer.parseInt(args[1])).start();
			} else {
				System.err.println("ERROR: Invalid number of arguments passed in!");
				System.out.println("Usage: java MazeServer <port_num>");
				System.exit(-1);
			}
		}
		catch (IOException e) {
			System.err.println("ERROR: Could not listen on port!");
			System.exit(-1);
		}

		while (listening) {
			new MazeServerRequestHandler(serverSocket.accept()).start();
		}

		serverSocket.close();
	}
}
