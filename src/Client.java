import java.io.*;
import java.net.*;
import java.util.*;

// Client class
class Client {

	// driver code
	public static void main(String[] args) throws InterruptedException {
		// establish a connection by providing host and port
		// number
//		Thread.sleep(2000);
		try (Socket socket = new Socket("localhost", 1235)) {

			// writing to server
			PrintWriter out = new PrintWriter(
					socket.getOutputStream(), true);

			// reading from server, optional - could be used to receive commands from master
			BufferedReader in
					= new BufferedReader(new InputStreamReader(
					socket.getInputStream()));


//			System.out.println("sending 'starting...'");
			out.println(args[0] + " :starting...");
			out.flush();
			Thread.sleep(1000);
//			System.out.println("sending 'running...'");
			out.println(args[0] + " :running...");
			out.flush();
			Thread.sleep(5000);
//			System.out.println("sending 'done...'");
			out.println(args[0] + " :done...");
			out.flush();
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}