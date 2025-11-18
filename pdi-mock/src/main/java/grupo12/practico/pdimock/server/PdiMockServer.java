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
        boolean daemonMode = false;
        
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.error("Invalid port number: {}", args[0]);
                System.exit(1);
            }
        }
        
        // Check if running in daemon mode (no TTY, e.g., Docker)
        if (args.length > 1 && "daemon".equals(args[1])) {
            daemonMode = true;
        } else {
            // Auto-detect: if System.in is not available or not a TTY, run in daemon mode
            try {
                if (System.in.available() == 0 || System.console() == null) {
                    daemonMode = true;
                }
            } catch (Exception e) {
                daemonMode = true;
            }
        }

        PdiMockServer server = new PdiMockServer(port);
        try {
            server.start();
            
            if (daemonMode) {
                logger.info("Running in daemon mode. Server will keep running until stopped.");
                // Add shutdown hook for graceful shutdown
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    logger.info("Shutdown hook triggered");
                    server.stop();
                }));
                // Keep the main thread alive
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException e) {
                    logger.info("Main thread interrupted");
                    server.stop();
                }
            } else {
                logger.info("Press Enter to stop the server...");
                System.in.read();
                server.stop();
            }
        } catch (Exception e) {
            logger.error("Error starting server", e);
            System.exit(1);
        }
    }
}

