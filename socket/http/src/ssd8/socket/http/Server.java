package src.ssd8.socket.http;

import java.io.IOException;
public class Server {

	public Server() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		HttpServer httpServer=new HttpServer();
		try {
			Deal deal=new Deal();
			httpServer.CreateRequestDeal(deal);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
