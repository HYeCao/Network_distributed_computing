package src.ssd8.socket.http;

import java.io.BufferedOutputStream;
import java.io.IOException;

public interface DealRequest {
	
	void Handle(String method,	String body,StringBuffer header,BufferedOutputStream ostream) throws IOException;
}
