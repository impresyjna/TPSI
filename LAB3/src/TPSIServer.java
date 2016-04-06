import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//WCZYTYWAC Z POSTACI BINARNEJ
public class TPSIServer {
    private static List<File> files = new ArrayList<>();
    private static String path;

    public static void main(String[] args) throws Exception {
        int port = 8000;
        path = args[0];
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        System.out.println("Starting server on port: " + port);
        System.out.println(server.getAddress());
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            byte[] responseBytes = new byte[0];
            Path filePath = Paths.get(path + exchange.getRequestURI().getPath());
            File fileToOpen = new File(filePath.toString());
            if(!fileToOpen.getAbsolutePath().equals(fileToOpen.getCanonicalPath())) {
                responseBytes = "403 Forbidden".getBytes();
                exchange.sendResponseHeaders(403, responseBytes.length);
            }
            if (fileToOpen.exists()) {
                if (fileToOpen.isDirectory()) {
                    files = fileList(filePath.toString());
                    for (File file : files) {
                        response += "<a href='" + new File(path).toURI().relativize(file.toURI()).getPath() + "'>" + file.getName() + "</a><br>";
                    }
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    responseBytes = response.getBytes();
                } else {
                    responseBytes = Files.readAllBytes(filePath);
                    exchange.sendResponseHeaders(200, responseBytes.length);
                }
            } else {
                response = "404 Not found";
                responseBytes = response.getBytes();
                exchange.sendResponseHeaders(404, responseBytes.length);
            }
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }

    public static List<File> fileList(String directory) {
        List<File> fileList = new ArrayList<>();
        File path = new File(directory);
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            for (File file : files) {
                fileList.add(file);
            }
        }
        return fileList;
    }
}
