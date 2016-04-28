import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import other.Model;

import java.net.URI;

/**
 * Created by impresyjna on 19.04.2016.
 */
public class Server {
    public static final String BASE_URI = "http://localhost:8000";
    private static Model model = Model.getInstance();

    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        try {
            server.start();
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("rest");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
}
