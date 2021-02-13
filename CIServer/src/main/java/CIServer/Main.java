package CIServer;

public class Main {

	public static void main(String[] args) throws Exception {
		CIServer server = new CIServer();
		server.startServer(args[1], args[2]);
	}
}
