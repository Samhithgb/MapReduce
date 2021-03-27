import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

// Client class
class Worker {

	// driver code
	public static void main(String[] args) throws InterruptedException {
		Mapper m = null;
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

			String id = args[0];
			String file_path = args[1];
			SerFunc func = (SerFunc) fromString(args[2]);

			out.println( id + " :starting... input=" + file_path);
			out.flush();
			Thread.sleep(1000);

			String res = (String) func.apply(file_path);
			out.println(id + " :running... output=" + res);
			out.flush();
			Thread.sleep(5000);

			out.println(args[0] + " :done...");
			out.flush();
		}
		catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/** Read the object from Base64 string. */
	private static Object fromString( String s ) throws IOException ,
			ClassNotFoundException {
		byte [] data = Base64.getDecoder().decode( s );
		ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(  data ) );
		Object o  = ois.readObject();
		ois.close();
		return o;
	}

	/** Write the object to a Base64 string. */
	private static String toString( Serializable o ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

}