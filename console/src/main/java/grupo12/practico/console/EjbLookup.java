package grupo12.practico.console;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public final class EjbLookup {
    private EjbLookup() {}

    public static Context createRemoteContext(String host, int port) throws NamingException {
        Properties props = new Properties();

        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, String.format("http-remoting://%s:%d", host, port));
        props.put("jboss.naming.client.ejb.context", true);

        return new InitialContext(props);
    }
}
