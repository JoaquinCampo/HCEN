package grupo12.practico.console;

import javax.naming.Context;
import javax.naming.NamingException;

import grupo12.practico.services.HealthWorker.HealthWorkerServiceRemote;
import grupo12.practico.services.HealthProvider.HealthProviderServiceRemote;
import grupo12.practico.services.HealthUser.HealthUserServiceRemote;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceRemote;

public final class ServiceLocator {

    private final Context context;
    private final String appName;
    private final String moduleName;

    public ServiceLocator(Context context, String appName, String moduleName) {
        this.context = context;
        this.appName = appName;
        this.moduleName = moduleName;
    }

    private String ejb(String beanSimpleName, String remoteInterfaceFqn) {
        return String.format("ejb:%s/%s//%s!%s", appName, moduleName, beanSimpleName, remoteInterfaceFqn);
    }

    public HealthUserServiceRemote userService() throws NamingException {
        String jndi = ejb("UserServiceBean", HealthUserServiceRemote.class.getName());
        return (HealthUserServiceRemote) context.lookup(jndi);
    }

    public HealthWorkerServiceRemote healthWorkerService() throws NamingException {
        String jndi = ejb("HealthWorkerServiceBean", HealthWorkerServiceRemote.class.getName());
        return (HealthWorkerServiceRemote) context.lookup(jndi);
    }

    public HealthProviderServiceRemote healthProviderService() throws NamingException {
        String jndi = ejb("HealthProviderServiceBean", HealthProviderServiceRemote.class.getName());
        return (HealthProviderServiceRemote) context.lookup(jndi);
    }

    public ClinicalDocumentServiceRemote clinicalDocumentService() throws NamingException {
        String jndi = ejb("ClinicalDocumentServiceBean", ClinicalDocumentServiceRemote.class.getName());
        return (ClinicalDocumentServiceRemote) context.lookup(jndi);
    }
}
