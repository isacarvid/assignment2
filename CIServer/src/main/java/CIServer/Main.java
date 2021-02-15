package CIServer;
 import java.lang.String;
public class Main {

	public static void main(String[] args) throws Exception {
		CIServer server = new CIServer();
		server.startServer(Integer.parseInt(args[0]), args[1], args[2]);
	}
}
