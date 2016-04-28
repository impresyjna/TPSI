import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.misc.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by impresyjna on 08.04.2016.
 */
public class TPSIServer {

    protected static List<String> blackList = new ArrayList<>();
    protected static List<CVSLine> statistics = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        statistics.add(new CVSLine("domain", "req", "sendBytes", "receiveBytes"));
        System.out.println("Starting server on port: " + port);
        System.out.println(server.getAddress());
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {

            /* Send request */
            URL url = new URL(exchange.getRequestURI().toString());
            int sendBytes, receiveBytes;
            try (BufferedReader br = new BufferedReader(new FileReader(new File("blacklist.txt")))) {
                String line;
                while ((line = br.readLine()) != null) {
                    blackList.add(line);
                }
            }
            String host = exchange.getRequestURI().getHost();
            sendBytes = IOUtils.readFully(exchange.getRequestBody(), -1, true).length;
            if (blocked(host)) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(exchange.getRequestMethod());
                connection.setReadTimeout(15 * 1000);
                Set<String> keys = exchange.getRequestHeaders().keySet();
                for (String key : keys) {
                    List<String> values = exchange.getRequestHeaders().get(key);
                    String valuesString = String.join(",", values);
                    connection.setRequestProperty(key, valuesString);
                }

                if (exchange.getRequestMethod().equals("POST") || exchange.getRequestMethod().equals("PUT")) {
                    connection.setDoOutput(true);
                    byte[] requestBytes = IOUtils.readFully(exchange.getRequestBody(), -1, true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestBytes);
                    outputStream.close();
                }

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
                receiveBytes = responseBytes.length;
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(403, "403 Blocked".getBytes().length);
                receiveBytes = "403 Blocked".getBytes().length;
                OutputStream os = exchange.getResponseBody();
                os.write("403 Blocked".getBytes());
                os.close();
            }
            addToCSV(url.toString(), sendBytes, receiveBytes);

        }

        public boolean blocked(String host) {
            for (String line : blackList) {
                if (line.contains(host)) {
                    return false;
                }
            }
            return true;
        }

        public void addToCSV(String URL, int sendBytes, int receiveBytes) {
            boolean found = false;
            for (CVSLine line : statistics) {
                if (line.URL.equals(URL)) {
                    int reqInt = Integer.parseInt(line.req);
                    long sentBytesInt = Long.parseLong(line.sendBytes);
                    long receiveBytesInt = Long.parseLong(line.receiveBytes);
                    reqInt += 1;
                    sentBytesInt += sendBytes;
                    receiveBytesInt += receiveBytes;
                    line.req = Integer.toString(reqInt);
                    line.sendBytes = Long.toString(sentBytesInt);
                    line.receiveBytes = Long.toString(receiveBytesInt);
                    found = true;
                }
            }
            if (!found) {
                CVSLine csvline = new CVSLine(URL, "1", Integer.toString(sendBytes), Integer.toString(receiveBytes));
                statistics.add(csvline);
            }
            FileWriter  writer = null;
            try {
                writer = new FileWriter("statistics.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                for (CVSLine line : statistics) {

                    writer.append(line.URL);
                    writer.append(',');
                    writer.append(line.req);
                    writer.append(',');
                    writer.append(line.sendBytes);
                    writer.append(',');
                    writer.append(line.receiveBytes);
                    writer.append(';');
                    writer.append('\n');

                }

                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
