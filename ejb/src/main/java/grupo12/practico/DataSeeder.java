package grupo12.practico;

import grupo12.practico.models.*;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.ClinicalHistory.ClinicalHistoryRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
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
    private ClinicalHistoryRepositoryLocal clinicalHistoryRepository;

    @PostConstruct
    public void seedData() {
        System.out.println("Starting data seeding...");
        createClinics();
        createHealthWorkers();
        createHealthUsers();
        createClinicalDocuments();
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
        clinic1.setType("HOSPITAL");
        clinicRepository.add(clinic1);

        Clinic clinic2 = new Clinic();
        clinic2.setName("Clínica Sanatorio Americano");
        clinic2.setEmail("info@sanatorio.com.uy");
        clinic2.setPhone("+598 2711 5678");
        clinic2.setAddress("Bv. Artigas 456, Montevideo");
        clinic2.setDomain("sanatorio.com.uy");
        clinic2.setType("PRIVATE_PRACTICE");
        clinicRepository.add(clinic2);

        Clinic clinic3 = new Clinic();
        clinic3.setName("Centro Médico Punta Gorda");
        clinic3.setEmail("contacto@puntagorda.uy");
        clinic3.setPhone("+598 2604 7890");
        clinic3.setAddress("Rambla Gandhi 789, Punta del Este");
        clinic3.setDomain("puntagorda.uy");
        clinic3.setType("DIAGNOSTIC_CENTER");
        clinicRepository.add(clinic3);

        Clinic clinic4 = new Clinic();
        clinic4.setName("Policlínica Rivera");
        clinic4.setEmail("info@policlinicorivera.com.uy");
        clinic4.setPhone("+598 4622 3456");
        clinic4.setAddress("Av. Sarandí 321, Rivera");
        clinic4.setDomain("policlinicorivera.com.uy");
        clinic4.setType("POLYCLINIC");
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

        var clinics = clinicRepository.findAll();

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

        ClinicalHistory ch1 = new ClinicalHistory();
        ch1.setHealthUser(hu1);
        hu1.setClinicalHistory(ch1);
        clinicalHistoryRepository.add(ch1);

        if (!clinics.isEmpty()) {
            Set<Clinic> clinics1 = new HashSet<>();
            clinics1.add(clinics.get(0));
            hu1.setClinics(clinics1);
        }

        healthUserRepository.add(hu1);

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

        ClinicalHistory ch2 = new ClinicalHistory();
        ch2.setHealthUser(hu2);
        hu2.setClinicalHistory(ch2);
        clinicalHistoryRepository.add(ch2);

        if (!clinics.isEmpty()) {
            Set<Clinic> clinics2 = new HashSet<>();
            clinics2.add(clinics.get(1));
            hu2.setClinics(clinics2);
        }

        healthUserRepository.add(hu2);

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

        ClinicalHistory ch3 = new ClinicalHistory();
        ch3.setHealthUser(hu3);
        hu3.setClinicalHistory(ch3);
        clinicalHistoryRepository.add(ch3);

        if (clinics.size() > 2) {
            Set<Clinic> clinics3 = new HashSet<>();
            clinics3.add(clinics.get(2));
            hu3.setClinics(clinics3);
        }

        healthUserRepository.add(hu3);

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

        ClinicalHistory ch4 = new ClinicalHistory();
        ch4.setHealthUser(hu4);
        hu4.setClinicalHistory(ch4);
        clinicalHistoryRepository.add(ch4);

        if (clinics.size() > 3) {
            Set<Clinic> clinics4 = new HashSet<>();
            clinics4.add(clinics.get(3));
            hu4.setClinics(clinics4);
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

        ClinicalDocument doc1 = new ClinicalDocument();
        doc1.setTitle("Initial Consultation - Cardiovascular Check");
        doc1.setContentUrl(
                "https://health-records-bucket.s3.amazonaws.com/documents/lucia-cardiovascular-check-2024.pdf");
        doc1.setClinicalHistory(healthUsers.get(0).getClinicalHistory());

        Set<HealthWorker> healthWorkers1 = new HashSet<>();
        healthWorkers1.add(healthWorkers.get(0));
        doc1.setHealthWorkers(healthWorkers1);

        clinicalDocumentRepository.add(doc1);

        // Document 2 - Prescription for Miguel
        ClinicalDocument doc2 = new ClinicalDocument();
        doc2.setTitle("Blood Pressure Medication Prescription");
        doc2.setContentUrl("https://health-records-bucket.s3.amazonaws.com/documents/miguel-prescription-2024.pdf");
        doc2.setClinicalHistory(healthUsers.get(1).getClinicalHistory());

        Set<HealthWorker> healthWorkers2 = new HashSet<>();
        healthWorkers2.add(healthWorkers.get(1));
        doc2.setHealthWorkers(healthWorkers2);

        clinicalDocumentRepository.add(doc2);

        // Document 3 - Pediatric Check for Sofia
        ClinicalDocument doc3 = new ClinicalDocument();
        doc3.setTitle("Well-child Visit - 8 Years Old");
        doc3.setContentUrl("https://health-records-bucket.s3.amazonaws.com/documents/sofia-pediatric-check-2024.pdf");
        doc3.setClinicalHistory(healthUsers.get(2).getClinicalHistory());

        Set<HealthWorker> healthWorkers3 = new HashSet<>();
        healthWorkers3.add(healthWorkers.get(2));
        doc3.setHealthWorkers(healthWorkers3);

        clinicalDocumentRepository.add(doc3);

        // Document 4 - Surgery Report for Roberto
        ClinicalDocument doc4 = new ClinicalDocument();
        doc4.setTitle("Appendectomy Surgery Report");
        doc4.setContentUrl(
                "https://health-records-bucket.s3.amazonaws.com/documents/roberto-appendectomy-report-2024.pdf");
        doc4.setClinicalHistory(healthUsers.get(3).getClinicalHistory());
        Set<HealthWorker> healthWorkers4 = new HashSet<>();
        healthWorkers4.add(healthWorkers.get(3));
        doc4.setHealthWorkers(healthWorkers4);

        clinicalDocumentRepository.add(doc4);
    }
}
