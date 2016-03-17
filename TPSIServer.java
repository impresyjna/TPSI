import com.sun.net.httpserver.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class TPSIServer {
	public static void main(String[] args) throws Exception {
		int port = 8000;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new RootHandler());
		System.out.println("Starting server on port: " + port);
		server.start();
	}

	static class RootHandler implements HttpHandler {
		public void handle(HttpExchange exchange) throws IOException {
			String response = ""; 
			BufferedReader buffer =  new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream("index.html"), "UTF8"));
			String line;
			while((line = buffer.readLine())!=null){
				response = response + line;
			}
			buffer.close();
			System.out.println(response);
			exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes("UTF-8"));
			os.close();
		}
	}

}
