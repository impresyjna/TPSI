import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.*;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

public class TPSIServer {
	public static void main(String[] args) throws Exception {
		int port = 8000;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new RootHandler());
		server.createContext("/echo/", new EchoHandler()); 
		server.createContext("/redirect/", new RedirectHandler()); 
		server.createContext("/cookies/", new CookiesHandler());
		server.createContext("/auth/", new AuthHandler());
		server.createContext("/auth2", new Auth2Handler()).setAuthenticator(new BasicAuthenticator("Introduce yourself") {
			@Override
			public boolean checkCredentials(String login, String password) {
				return login.equals("admin") && password.equals("admin");
			}
		});
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
			exchange.getResponseHeaders().set("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
	
	static class EchoHandler implements HttpHandler {
		public void handle(HttpExchange exchange) throws IOException {
			HashMap<String,Object> hashMap = new HashMap<>(); 
			hashMap.put(JsonWriter.PRETTY_PRINT, true);
			String response = JsonWriter.objectToJson(exchange.getRequestHeaders(),hashMap);
			exchange.getResponseHeaders().set("", response);
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
	
	static class RedirectHandler implements HttpHandler { 
		public void handle(HttpExchange exchange) throws IOException {
			System.out.println(exchange.getRequestURI().toString());
			System.out.println(exchange.getRequestMethod().toString());
			String response = ""; 
			exchange.getResponseHeaders().set("Location", "/echo/");
			exchange.sendResponseHeaders(303, 0);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	static class CookiesHandler implements HttpHandler { 
		public void handle(HttpExchange exchange) throws IOException {
			String response ="Hello world";
			UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
			String cookieSet = "ciaseczko3=" + uid.randomUUID() + ";domain=localhost; path=/echo/";
			exchange.getResponseHeaders().add("Set-cookie", cookieSet);
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	static class AuthHandler implements HttpHandler{
		public void handle(HttpExchange exchange) throws IOException {
			if(exchange.getRequestHeaders().get("Authorization") == null) {
				exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=\"Introduce yourself:\"");
				exchange.sendResponseHeaders(401, -1);
			} else {
				String requestData = exchange.getRequestHeaders().getFirst("Authorization").replace("Basic ", "");
				String[] authenticationData = new String(DatatypeConverter.parseBase64Binary(requestData), "UTF-8").split("[:]");
				String response;
				if(authenticationData[0].equals("admin") && authenticationData[1].equals("admin"))
					response = "Hello";
				else
					response = "Oops...";
				exchange.getResponseHeaders().set("Content-Type", "text/plain");
				exchange.sendResponseHeaders(200, response.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}

	static class Auth2Handler implements HttpHandler {
		public void handle(HttpExchange exchange) throws IOException {
			String response="Hello";
			exchange.getResponseHeaders().set("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
}
