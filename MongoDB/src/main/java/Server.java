import com.mongodb.MongoClient;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import other.Model;
import resources.DateParamConverterProvider;

import java.net.URI;

/**
 * Created by impresyjna on 19.04.2016.
 */
public class Server {
    public static final String BASE_URI = "http://localhost:8000";
    private static Model model = Model.getInstance();

    public static void main(String[] args) throws Exception {
        //MongoClient mongoClient = new MongoClient();

        MongoClient mongo = new MongoClient("localhost", 27017);
        Morphia morphia = new Morphia();
        morphia.mapPackage("model");
        Datastore ds = morphia.createDatastore(mongo, "REST");
        ds.ensureIndexes();
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
        DateParamConverterProvider dateParamConverterProvider = new DateParamConverterProvider("yyyy-MM-dd");
        final ResourceConfig rc = new ResourceConfig()
                .packages("rest")
                .register(DeclarativeLinkingFeature.class).register(dateParamConverterProvider);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
}
