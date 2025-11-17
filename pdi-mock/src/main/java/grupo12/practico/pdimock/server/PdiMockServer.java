package grupo12.practico.pdimock.server;

import grupo12.practico.pdimock.PdiMockService;
import jakarta.xml.ws.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdiMockServer {

    private static final Logger logger = LoggerFactory.getLogger(PdiMockServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String BASE_URL_TEMPLATE = "http://localhost:%d%s";
    private static final String SERVICE_PATH = "/wsServicioDeInformacionBasico";

    private Endpoint endpoint;
    private int port;

    public PdiMockServer(int port) {
        this.port = port;
    }

    public PdiMockServer() {
        this(DEFAULT_PORT);
    }

    public void start() throws Exception {
        logger.info("Starting PDI Mock Server on port {}", port);

        String url = String.format(BASE_URL_TEMPLATE, port, SERVICE_PATH);
        PdiMockService service = new PdiMockService();
        endpoint = Endpoint.publish(url, service);

        logger.info("PDI Mock Server started successfully");
        logger.info("Service available at: {}", url);
        logger.info("WSDL available at: {}?wsdl", url);
    }

    public void stop() {
        if (endpoint != null && endpoint.isPublished()) {
            logger.info("Stopping PDI Mock Server");
            endpoint.stop();
            logger.info("PDI Mock Server stopped");
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.error("Invalid port number: {}", args[0]);
                System.exit(1);
            }
        }

        PdiMockServer server = new PdiMockServer(port);
        try {
            server.start();
            logger.info("Press Enter to stop the server...");
            System.in.read();
            server.stop();
        } catch (Exception e) {
            logger.error("Error starting server", e);
            System.exit(1);
        }
    }
}

