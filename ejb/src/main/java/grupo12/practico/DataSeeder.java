package grupo12.practico;

import grupo12.practico.models.*;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import grupo12.practico.repositories.AccessGrant.AccessGrantRepositoryLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Singleton EJB that seeds the database with sample data on application
 * startup.
 * This ensures consistent test data for development and demonstration purposes.
 */
@Singleton
@Startup
public class DataSeeder {

    @EJB
    private ClinicRepositoryLocal clinicRepository;

    @EJB
    private HealthWorkerRepositoryLocal healthWorkerRepository;

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private ClinicalDocumentRepositoryLocal clinicalDocumentRepository;

    @EJB
    private AccessGrantRepositoryLocal accessGrantRepository;

    @PostConstruct
    public void seedData() {
        System.out.println("Starting data seeding...");

        // Create clinics first (independent entities)
        createClinics();

        // Create health workers (may reference clinics)
        createHealthWorkers();

        // Create health users (reference health workers and clinics)
        createHealthUsers();

        // Create clinical documents (reference health users and health workers)
        createClinicalDocuments();

        // Create access grants (reference all entities)
        createAccessGrants();

        System.out.println("Data seeding completed successfully!");
    }

    private void createClinics() {
        System.out.println("Seeding clinics...");

        Clinic clinic1 = new Clinic();
        clinic1.setName("Hospital Central de Montevideo");
        clinic1.setEmail("contacto@hcm.com.uy");
        clinic1.setPhone("+598 2901 2345");
        clinic1.setAddress("Av. 18 de Julio 1234, Montevideo");
        clinic1.setDomain("hcm.com.uy");
        clinic1.setRegistrationNumber("CLINIC-001");
        clinic1.setRegistrationDate(LocalDate.of(1995, 3, 15));
        clinic1.setType(ClinicType.HOSPITAL);
        clinicRepository.add(clinic1);

        Clinic clinic2 = new Clinic();
        clinic2.setName("Clínica Sanatorio Americano");
        clinic2.setEmail("info@sanatorio.com.uy");
        clinic2.setPhone("+598 2711 5678");
        clinic2.setAddress("Bv. Artigas 456, Montevideo");
        clinic2.setDomain("sanatorio.com.uy");
        clinic2.setRegistrationNumber("CLINIC-002");
        clinic2.setRegistrationDate(LocalDate.of(1988, 7, 22));
        clinic2.setType(ClinicType.PRIVATE_PRACTICE);
        clinicRepository.add(clinic2);

        Clinic clinic3 = new Clinic();
        clinic3.setName("Centro Médico Punta Gorda");
        clinic3.setEmail("contacto@puntagorda.uy");
        clinic3.setPhone("+598 2604 7890");
        clinic3.setAddress("Rambla Gandhi 789, Punta del Este");
        clinic3.setDomain("puntagorda.uy");
        clinic3.setRegistrationNumber("CLINIC-003");
        clinic3.setRegistrationDate(LocalDate.of(2005, 11, 8));
        clinic3.setType(ClinicType.DIAGNOSTIC_CENTER);
        clinicRepository.add(clinic3);

        Clinic clinic4 = new Clinic();
        clinic4.setName("Policlínica Rivera");
        clinic4.setEmail("info@policlinicorivera.com.uy");
        clinic4.setPhone("+598 4622 3456");
        clinic4.setAddress("Av. Sarandí 321, Rivera");
        clinic4.setDomain("policlinicorivera.com.uy");
        clinic4.setRegistrationNumber("CLINIC-004");
        clinic4.setRegistrationDate(LocalDate.of(1999, 1, 30));
        clinic4.setType(ClinicType.POLYCLINIC);
        clinicRepository.add(clinic4);
    }

    private void createHealthWorkers() {
        System.out.println("Seeding health workers...");

        // Get all clinics for reference
        var clinics = clinicRepository.findAll();

        // Health Worker 1 - Cardiologist
        HealthWorker hw1 = new HealthWorker();
        hw1.setFirstName("María");
        hw1.setLastName("Rodríguez");
        hw1.setDocument("12345678");
        hw1.setDocumentType(DocumentType.ID);
        hw1.setGender(Gender.FEMALE);
        hw1.setEmail("maria.rodriguez@hcm.com.uy");
        hw1.setPhone("+598 99 123 456");
        hw1.setAddress("Calle Colonia 567, Montevideo");
        hw1.setLicenseNumber("LIC-001");
        hw1.setHireDate(LocalDate.of(2010, 5, 15));
        hw1.setDateOfBirth(LocalDate.of(1975, 8, 20));

        // Add specialties
        Set<Specialty> specialties1 = new HashSet<>();
        Specialty cardio = new Specialty();
        cardio.setName("Cardiology");
        specialties1.add(cardio);
        hw1.setSpecialties(specialties1);

        // Add to first clinic
        if (!clinics.isEmpty()) {
            Set<Clinic> hw1Clinics = new HashSet<>();
            hw1Clinics.add(clinics.get(0));
            hw1.setClinics(hw1Clinics);
        }

        healthWorkerRepository.add(hw1);

        // Health Worker 2 - General Practitioner
        HealthWorker hw2 = new HealthWorker();
        hw2.setFirstName("Carlos");
        hw2.setLastName("Fernández");
        hw2.setDocument("23456789");
        hw2.setDocumentType(DocumentType.ID);
        hw2.setGender(Gender.MALE);
        hw2.setEmail("carlos.fernandez@sanatorio.com.uy");
        hw2.setPhone("+598 99 234 567");
        hw2.setAddress("Av. Rivera 890, Montevideo");
        hw2.setLicenseNumber("LIC-002");
        hw2.setHireDate(LocalDate.of(2012, 3, 10));
        hw2.setDateOfBirth(LocalDate.of(1980, 12, 5));

        Set<Specialty> specialties2 = new HashSet<>();
        Specialty general = new Specialty();
        general.setName("General Medicine");
        specialties2.add(general);
        hw2.setSpecialties(specialties2);

        if (clinics.size() > 1) {
            Set<Clinic> hw2Clinics = new HashSet<>();
            hw2Clinics.add(clinics.get(1));
            hw2.setClinics(hw2Clinics);
        }

        healthWorkerRepository.add(hw2);

        // Health Worker 3 - Pediatrician
        HealthWorker hw3 = new HealthWorker();
        hw3.setFirstName("Ana");
        hw3.setLastName("Gómez");
        hw3.setDocument("34567890");
        hw3.setDocumentType(DocumentType.ID);
        hw3.setGender(Gender.FEMALE);
        hw3.setEmail("ana.gomez@puntagorda.uy");
        hw3.setPhone("+598 99 345 678");
        hw3.setAddress("Rambla Brava 123, Punta del Este");
        hw3.setLicenseNumber("LIC-003");
        hw3.setHireDate(LocalDate.of(2015, 7, 20));
        hw3.setDateOfBirth(LocalDate.of(1985, 4, 15));

        Set<Specialty> specialties3 = new HashSet<>();
        Specialty pediatrics = new Specialty();
        pediatrics.setName("Pediatrics");
        specialties3.add(pediatrics);
        hw3.setSpecialties(specialties3);

        if (clinics.size() > 2) {
            Set<Clinic> hw3Clinics = new HashSet<>();
            hw3Clinics.add(clinics.get(2));
            hw3.setClinics(hw3Clinics);
        }

        healthWorkerRepository.add(hw3);

        // Health Worker 4 - Surgeon
        HealthWorker hw4 = new HealthWorker();
        hw4.setFirstName("José");
        hw4.setLastName("Martínez");
        hw4.setDocument("45678901");
        hw4.setDocumentType(DocumentType.ID);
        hw4.setGender(Gender.MALE);
        hw4.setEmail("jose.martinez@policlinicorivera.com.uy");
        hw4.setPhone("+598 99 456 789");
        hw4.setAddress("Calle Artigas 456, Rivera");
        hw4.setLicenseNumber("LIC-004");
        hw4.setHireDate(LocalDate.of(2008, 9, 5));
        hw4.setDateOfBirth(LocalDate.of(1970, 6, 30));

        Set<Specialty> specialties4 = new HashSet<>();
        Specialty surgery = new Specialty();
        surgery.setName("General Surgery");
        specialties4.add(surgery);
        hw4.setSpecialties(specialties4);

        if (clinics.size() > 3) {
            Set<Clinic> hw4Clinics = new HashSet<>();
            hw4Clinics.add(clinics.get(3));
            hw4.setClinics(hw4Clinics);
        }

        healthWorkerRepository.add(hw4);
    }

    private void createHealthUsers() {
        System.out.println("Seeding health users...");

        var healthWorkers = healthWorkerRepository.findAll();
        var clinics = clinicRepository.findAll();

        // Health User 1
        HealthUser hu1 = new HealthUser();
        hu1.setFirstName("Lucía");
        hu1.setLastName("Silva");
        hu1.setDocument("56789012");
        hu1.setDocumentType(DocumentType.ID);
        hu1.setGender(Gender.FEMALE);
        hu1.setEmail("lucia.silva@gmail.com");
        hu1.setPhone("+598 99 567 890");
        hu1.setAddress("Calle Ejido 789, Montevideo");
        hu1.setDateOfBirth(LocalDate.of(1990, 2, 14));

        if (!healthWorkers.isEmpty()) {
            hu1.addHealthWorker(healthWorkers.get(0)); // Assign to first health worker
        }
        if (!clinics.isEmpty()) {
            hu1.addAffiliatedHealthProvider(clinics.get(0)); // Affiliated with first clinic
        }

        healthUserRepository.add(hu1);

        // Health User 2
        HealthUser hu2 = new HealthUser();
        hu2.setFirstName("Miguel");
        hu2.setLastName("López");
        hu2.setDocument("67890123");
        hu2.setDocumentType(DocumentType.ID);
        hu2.setGender(Gender.MALE);
        hu2.setEmail("miguel.lopez@hotmail.com");
        hu2.setPhone("+598 99 678 901");
        hu2.setAddress("Av. Brasil 234, Montevideo");
        hu2.setDateOfBirth(LocalDate.of(1985, 11, 8));

        if (healthWorkers.size() > 1) {
            hu2.addHealthWorker(healthWorkers.get(1));
        }
        if (clinics.size() > 1) {
            hu2.addAffiliatedHealthProvider(clinics.get(1));
        }

        healthUserRepository.add(hu2);

        // Health User 3 - Child
        HealthUser hu3 = new HealthUser();
        hu3.setFirstName("Sofia");
        hu3.setLastName("Pérez");
        hu3.setDocument("78901234");
        hu3.setDocumentType(DocumentType.ID);
        hu3.setGender(Gender.FEMALE);
        hu3.setEmail("sofia.perez@gmail.com");
        hu3.setPhone("+598 99 789 012");
        hu3.setAddress("Bv. España 567, Montevideo");
        hu3.setDateOfBirth(LocalDate.of(2015, 6, 22));

        if (healthWorkers.size() > 2) {
            hu3.addHealthWorker(healthWorkers.get(2)); // Pediatrician
        }
        if (clinics.size() > 2) {
            hu3.addAffiliatedHealthProvider(clinics.get(2));
        }

        healthUserRepository.add(hu3);

        // Health User 4
        HealthUser hu4 = new HealthUser();
        hu4.setFirstName("Roberto");
        hu4.setLastName("Díaz");
        hu4.setDocument("89012345");
        hu4.setDocumentType(DocumentType.ID);
        hu4.setGender(Gender.MALE);
        hu4.setEmail("roberto.diaz@gmail.com");
        hu4.setPhone("+598 99 890 123");
        hu4.setAddress("Calle 25 de Mayo 890, Rivera");
        hu4.setDateOfBirth(LocalDate.of(1978, 9, 17));

        if (healthWorkers.size() > 3) {
            hu4.addHealthWorker(healthWorkers.get(3));
        }
        if (clinics.size() > 3) {
            hu4.addAffiliatedHealthProvider(clinics.get(3));
        }

        healthUserRepository.add(hu4);
    }

    private void createClinicalDocuments() {
        System.out.println("Seeding clinical documents...");

        var healthUsers = healthUserRepository.findAll();
        var healthWorkers = healthWorkerRepository.findAll();
        var clinics = clinicRepository.findAll();

        if (healthUsers.isEmpty() || healthWorkers.isEmpty() || clinics.isEmpty()) {
            return;
        }

        // Document 1 - Medical Record for Lucia
        ClinicalDocument doc1 = new ClinicalDocument();
        doc1.setTitle("Initial Consultation - Cardiovascular Check");
        doc1.setContent(
                "Patient presents with mild hypertension. Recommended lifestyle changes and follow-up in 3 months.");
        doc1.setClinicalHistory(healthUsers.get(0).getOrCreateClinicalHistory());
        doc1.setAuthor(healthWorkers.get(0));
        doc1.setProvider(clinics.get(0));

        clinicalDocumentRepository.add(doc1);

        // Document 2 - Prescription for Miguel
        ClinicalDocument doc2 = new ClinicalDocument();
        doc2.setTitle("Blood Pressure Medication Prescription");
        doc2.setContent("Prescribed: Enalapril 10mg daily. Monitor blood pressure weekly.");
        doc2.setClinicalHistory(healthUsers.get(1).getOrCreateClinicalHistory());
        doc2.setAuthor(healthWorkers.get(1));
        doc2.setProvider(clinics.get(1));

        clinicalDocumentRepository.add(doc2);

        // Document 3 - Pediatric Check for Sofia
        ClinicalDocument doc3 = new ClinicalDocument();
        doc3.setTitle("Well-child Visit - 8 Years Old");
        doc3.setContent("Height: 125cm, Weight: 28kg. All vaccinations up to date. Development normal.");
        doc3.setClinicalHistory(healthUsers.get(2).getOrCreateClinicalHistory());
        doc3.setAuthor(healthWorkers.get(2));
        doc3.setProvider(clinics.get(2));

        clinicalDocumentRepository.add(doc3);

        // Document 4 - Surgery Report for Roberto
        ClinicalDocument doc4 = new ClinicalDocument();
        doc4.setTitle("Appendectomy Surgery Report");
        doc4.setContent(
                "Emergency appendectomy performed successfully. Patient recovered well, discharged after 3 days.");
        doc4.setClinicalHistory(healthUsers.get(3).getOrCreateClinicalHistory());
        doc4.setAuthor(healthWorkers.get(3));
        doc4.setProvider(clinics.get(3));

        clinicalDocumentRepository.add(doc4);
    }

    private void createAccessGrants() {
        System.out.println("Seeding access grants...");

        var healthUsers = healthUserRepository.findAll();
        var healthWorkers = healthWorkerRepository.findAll();
        var clinics = clinicRepository.findAll();

        if (healthUsers.isEmpty() || healthWorkers.isEmpty() || clinics.isEmpty()) {
            return;
        }

        // Grant access for Lucia's cardiologist to her records
        AccessGrant grant1 = new AccessGrant();
        grant1.setClinicalHistoryId(healthUsers.get(0).getOrCreateClinicalHistory().getId());
        grant1.setSubjectType("WORKER");
        grant1.setSubjectId(healthWorkers.get(0).getId());
        grant1.setScope("READ_WRITE");
        grant1.setStartsAt(LocalDate.now());
        grant1.setEndsAt(LocalDate.now().plusYears(1));
        grant1.setGrantedBy(healthWorkers.get(0).getId());
        grant1.setReason("Cardiovascular treatment and follow-up");

        accessGrantRepository.add(grant1);

        // Grant access for Sofia's pediatrician
        AccessGrant grant2 = new AccessGrant();
        grant2.setClinicalHistoryId(healthUsers.get(2).getOrCreateClinicalHistory().getId());
        grant2.setSubjectType("WORKER");
        grant2.setSubjectId(healthWorkers.get(2).getId());
        grant2.setScope("READ");
        grant2.setStartsAt(LocalDate.now());
        grant2.setEndsAt(LocalDate.now().plusYears(2));
        grant2.setGrantedBy(healthWorkers.get(2).getId());
        grant2.setReason("Pediatric care and vaccination records");

        accessGrantRepository.add(grant2);
    }
}
