import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import sun.misc.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;

/**
 * Created by impresyjna on 08.04.2016.
 */
public class TPSIServer {

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        System.out.println("Starting server on port: " + port);
        System.out.println(server.getAddress());
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {

            try {/* Send request */
                URL url = new URL(exchange.getRequestURI().toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(exchange.getRequestMethod());
                connection.setReadTimeout(15 * 1000);
                Set<String> keys = exchange.getRequestHeaders().keySet();
                for (String key : keys) {
                    List<String> values = exchange.getRequestHeaders().get(key);
                    String valuesString = String.join(",", values);
                    /*for (String value : values) { */
                        connection.setRequestProperty(key, valuesString);
                    //}
                }
                connection.setDoOutput(true);
                byte[] requestBytes = IOUtils.readFully(exchange.getRequestBody(), -1, true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBytes);
                outputStream.close();

                connection.connect();

            /* Get response */
                int responseCode = connection.getResponseCode();
                System.out.println(responseCode);
                InputStream responseInputStream;
                try {
                    responseInputStream = connection.getInputStream();
                } catch (Exception e) {
                    responseInputStream = connection.getErrorStream();
                }
                byte[] responseBytes = IOUtils.readFully(responseInputStream, -1, true);
                Map<String, List<String>> responseHeaders = connection.getHeaderFields();
                for (Map.Entry<String, List<String>> key : responseHeaders.entrySet()) {
                    List<String> values = key.getValue();
                    for (String value : values) {
                        if (key.getKey() != null && !key.getKey().equals("Transfer-Encoding")) {
                            exchange.getResponseHeaders().set(key.getKey(), value);
                        }
                    }
                }
                exchange.sendResponseHeaders(responseCode, responseBytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
