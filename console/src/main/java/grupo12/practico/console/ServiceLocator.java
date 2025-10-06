package grupo12.practico.console;

import javax.naming.Context;
import javax.naming.NamingException;

import grupo12.practico.services.HealthWorker.HealthWorkerServiceRemote;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerRemote;
import grupo12.practico.messaging.HealthWorker.HealthWorkerRegistrationProducerRemote;
import grupo12.practico.messaging.Clinic.ClinicRegistrationProducerRemote;
import grupo12.practico.services.Clinic.ClinicServiceRemote;
import grupo12.practico.services.HealthUser.HealthUserServiceRemote;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceRemote;
import grupo12.practico.messaging.ClinicalDocument.ClinicalDocumentRegistrationProducerRemote;

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
        String jndi = ejb("HealthUserServiceBean", HealthUserServiceRemote.class.getName());
        return (HealthUserServiceRemote) context.lookup(jndi);
    }

    public HealthUserRegistrationProducerRemote healthUserRegistrationProducer() throws NamingException {
        String jndi = ejb("HealthUserRegistrationProducerBean",
                HealthUserRegistrationProducerRemote.class.getName());
        return (HealthUserRegistrationProducerRemote) context.lookup(jndi);
    }

    public HealthWorkerServiceRemote healthWorkerService() throws NamingException {
        String jndi = ejb("HealthWorkerServiceBean", HealthWorkerServiceRemote.class.getName());
        return (HealthWorkerServiceRemote) context.lookup(jndi);
    }

    public HealthWorkerRegistrationProducerRemote healthWorkerRegistrationProducer() throws NamingException {
        String jndi = ejb("HealthWorkerRegistrationProducerBean",
                HealthWorkerRegistrationProducerRemote.class.getName());
        return (HealthWorkerRegistrationProducerRemote) context.lookup(jndi);
    }

    public ClinicServiceRemote clinicService() throws NamingException {
        String jndi = ejb("ClinicServiceBean", ClinicServiceRemote.class.getName());
        return (ClinicServiceRemote) context.lookup(jndi);
    }

    public ClinicRegistrationProducerRemote clinicRegistrationProducer() throws NamingException {
        String jndi = ejb("ClinicRegistrationProducerBean",
                ClinicRegistrationProducerRemote.class.getName());
        return (ClinicRegistrationProducerRemote) context.lookup(jndi);
    }

    public ClinicalDocumentServiceRemote clinicalDocumentService() throws NamingException {
        String jndi = ejb("ClinicalDocumentServiceBean", ClinicalDocumentServiceRemote.class.getName());
        return (ClinicalDocumentServiceRemote) context.lookup(jndi);
    }

    public ClinicalDocumentRegistrationProducerRemote clinicalDocumentRegistrationProducer() throws NamingException {
        String jndi = ejb("ClinicalDocumentRegistrationProducerBean",
                ClinicalDocumentRegistrationProducerRemote.class.getName());
        return (ClinicalDocumentRegistrationProducerRemote) context.lookup(jndi);
    }
}
