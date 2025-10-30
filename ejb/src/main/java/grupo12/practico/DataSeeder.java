package grupo12.practico;

import grupo12.practico.models.*;
import grupo12.practico.repositories.AccessPolicy.AccessPolicyRepositoryLocal;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.repositories.Specialty.SpecialtyRepositoryLocal;
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
    private SpecialtyRepositoryLocal specialtyRepository;

    @EJB
    private AccessPolicyRepositoryLocal accessRequestRepository;

    @PostConstruct
    public void seedData() {
        System.out.println("Starting data seeding...");
        createClinics();
        createSpecialties();
        createHealthWorkers();
        createHealthUsers();
        createClinicalDocuments();
        createAccessRequests();
        System.out.println("Data seeding completed successfully!");
    }

    private void createClinics() {
        System.out.println("Seeding clinics...");

        Clinic clinic1 = new Clinic();
        clinic1.setName("Hospital Central de Montevideo");
        clinic1.setEmail("contacto@hcm.com.uy");
        clinic1.setPhone("+598 2901 2345");
        clinic1.setAddress("Av. 18 de Julio 1234, Montevideo");
        clinicRepository.add(clinic1);

        Clinic clinic2 = new Clinic();
        clinic2.setName("Clínica Sanatorio Americano");
        clinic2.setEmail("info@sanatorio.com.uy");
        clinic2.setPhone("+598 2711 5678");
        clinic2.setAddress("Bv. Artigas 456, Montevideo");
        clinicRepository.add(clinic2);

        Clinic clinic3 = new Clinic();
        clinic3.setName("Centro Médico Punta Gorda");
        clinic3.setEmail("contacto@puntagorda.uy");
        clinic3.setPhone("+598 2604 7890");
        clinic3.setAddress("Rambla Gandhi 789, Punta del Este");
        clinicRepository.add(clinic3);

        Clinic clinic4 = new Clinic();
        clinic4.setName("Policlínica Rivera");
        clinic4.setEmail("info@policlinicorivera.com.uy");
        clinic4.setPhone("+598 4622 3456");
        clinic4.setAddress("Av. Sarandí 321, Rivera");
        clinicRepository.add(clinic4);
    }

    private void createSpecialties() {
        System.out.println("Seeding specialties...");

        Specialty cardiology = new Specialty();
        cardiology.setName("Cardiology");
        specialtyRepository.add(cardiology);

        Specialty generalMedicine = new Specialty();
        generalMedicine.setName("General Medicine");
        specialtyRepository.add(generalMedicine);

        Specialty pediatrics = new Specialty();
        pediatrics.setName("Pediatrics");
        specialtyRepository.add(pediatrics);

        Specialty generalSurgery = new Specialty();
        generalSurgery.setName("General Surgery");
        specialtyRepository.add(generalSurgery);

        Specialty neurology = new Specialty();
        neurology.setName("Neurology");
        specialtyRepository.add(neurology);

        Specialty orthopedics = new Specialty();
        orthopedics.setName("Orthopedics");
        specialtyRepository.add(orthopedics);

        Specialty gynecology = new Specialty();
        gynecology.setName("Gynecology");
        specialtyRepository.add(gynecology);

        Specialty psychiatry = new Specialty();
        psychiatry.setName("Psychiatry");
        specialtyRepository.add(psychiatry);

        Specialty dermatology = new Specialty();
        dermatology.setName("Dermatology");
        specialtyRepository.add(dermatology);

        Specialty ophthalmology = new Specialty();
        ophthalmology.setName("Ophthalmology");
        specialtyRepository.add(ophthalmology);

        Specialty urology = new Specialty();
        urology.setName("Urology");
        specialtyRepository.add(urology);
    }

    private void createHealthWorkers() {
        System.out.println("Seeding health workers...");

        // Get all clinics and specialties for reference
        var clinics = clinicRepository.findAll();
        var specialties = specialtyRepository.findAll();

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
        hw1.setBloodType("O+");

        // Add specialties - Cardiology
        Set<Specialty> specialties1 = new HashSet<>();
        Specialty cardiology = specialties.stream()
                .filter(s -> "Cardiology".equals(s.getName()))
                .findFirst()
                .orElse(null);
        if (cardiology != null) {
            specialties1.add(cardiology);
        }
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
        hw2.setBloodType("A+");

        // Add specialties - General Medicine
        Set<Specialty> specialties2 = new HashSet<>();
        Specialty generalMedicine = specialties.stream()
                .filter(s -> "General Medicine".equals(s.getName()))
                .findFirst()
                .orElse(null);
        if (generalMedicine != null) {
            specialties2.add(generalMedicine);
        }
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
        hw3.setBloodType("B-");

        // Add specialties - Pediatrics
        Set<Specialty> specialties3 = new HashSet<>();
        Specialty pediatricsSpecialty = specialties.stream()
                .filter(s -> "Pediatrics".equals(s.getName()))
                .findFirst()
                .orElse(null);
        if (pediatricsSpecialty != null) {
            specialties3.add(pediatricsSpecialty);
        }
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
        hw4.setBloodType("AB+");

        // Add specialties - General Surgery
        Set<Specialty> specialties4 = new HashSet<>();
        Specialty generalSurgery = specialties.stream()
                .filter(s -> "General Surgery".equals(s.getName()))
                .findFirst()
                .orElse(null);
        if (generalSurgery != null) {
            specialties4.add(generalSurgery);
        }
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
        doc1.setHealthUser(healthUsers.get(0));

        Set<HealthWorker> healthWorkers1 = new HashSet<>();
        healthWorkers1.add(healthWorkers.get(0));
        doc1.setHealthWorkers(healthWorkers1);
        healthUsers.get(0).getClinicalDocuments().add(doc1);
        healthWorkers.get(0).getClinicalDocuments().add(doc1);

        clinicalDocumentRepository.add(doc1);

        // Document 2 - Prescription for Miguel
        ClinicalDocument doc2 = new ClinicalDocument();
        doc2.setTitle("Blood Pressure Medication Prescription");
        doc2.setContentUrl("https://health-records-bucket.s3.amazonaws.com/documents/miguel-prescription-2024.pdf");
        doc2.setHealthUser(healthUsers.get(1));

        Set<HealthWorker> healthWorkers2 = new HashSet<>();
        healthWorkers2.add(healthWorkers.get(1));
        doc2.setHealthWorkers(healthWorkers2);
        healthUsers.get(1).getClinicalDocuments().add(doc2);
        healthWorkers.get(1).getClinicalDocuments().add(doc2);

        clinicalDocumentRepository.add(doc2);

        // Document 3 - Pediatric Check for Sofia
        ClinicalDocument doc3 = new ClinicalDocument();
        doc3.setTitle("Well-child Visit - 8 Years Old");
        doc3.setContentUrl("https://health-records-bucket.s3.amazonaws.com/documents/sofia-pediatric-check-2024.pdf");
        doc3.setHealthUser(healthUsers.get(2));

        Set<HealthWorker> healthWorkers3 = new HashSet<>();
        healthWorkers3.add(healthWorkers.get(2));
        doc3.setHealthWorkers(healthWorkers3);
        healthUsers.get(2).getClinicalDocuments().add(doc3);
        healthWorkers.get(2).getClinicalDocuments().add(doc3);

        clinicalDocumentRepository.add(doc3);

        // Document 4 - Surgery Report for Roberto
        ClinicalDocument doc4 = new ClinicalDocument();
        doc4.setTitle("Appendectomy Surgery Report");
        doc4.setContentUrl(
                "https://health-records-bucket.s3.amazonaws.com/documents/roberto-appendectomy-report-2024.pdf");
        doc4.setHealthUser(healthUsers.get(3));
        Set<HealthWorker> healthWorkers4 = new HashSet<>();
        healthWorkers4.add(healthWorkers.get(3));
        doc4.setHealthWorkers(healthWorkers4);
        healthUsers.get(3).getClinicalDocuments().add(doc4);
        healthWorkers.get(3).getClinicalDocuments().add(doc4);

        clinicalDocumentRepository.add(doc4);
    }

    private void createAccessRequests() {
        System.out.println("Seeding access requests...");

        var healthUsers = healthUserRepository.findAll();
        var healthWorkers = healthWorkerRepository.findAll();
        var clinics = clinicRepository.findAll();
        var specialties = specialtyRepository.findAll();

        if (healthUsers.size() < 2 || healthWorkers.isEmpty() || clinics.isEmpty() || specialties.isEmpty()) {
            return;
        }

        // Get the first two health users (Lucía Silva and Miguel López)
        HealthUser user1 = healthUsers.get(0); // Lucía Silva
        HealthUser user2 = healthUsers.get(1); // Miguel López

        // Create access requests for Lucía Silva (user1)
        // Access request to cardiologist at her clinic - PENDING
        AccessRequest ar1 = new AccessRequest();
        ar1.setHealthUser(user1);
        ar1.setHealthWorker(healthWorkers.get(0)); // María Rodríguez (Cardiologist)
        ar1.setClinic(clinics.get(0)); // Hospital Central de Montevideo
        ar1.setSpecialty(specialties.stream().filter(s -> "Cardiology".equals(s.getName())).findFirst().orElse(null));
        ar1.setStatus(AccessRequestStatus.PENDING);
        accessRequestRepository.add(ar1);

        // Access request to neurologist at different clinic - PENDING
        AccessRequest ar2 = new AccessRequest();
        ar2.setHealthUser(user1);
        ar2.setHealthWorker(healthWorkers.get(1)); // Carlos Fernández (General Medicine)
        ar2.setClinic(clinics.get(1)); // Clínica Sanatorio Americano
        ar2.setSpecialty(
                specialties.stream().filter(s -> "General Medicine".equals(s.getName())).findFirst().orElse(null));
        ar2.setStatus(AccessRequestStatus.PENDING);
        accessRequestRepository.add(ar2);

        // Access request to pediatrician - PENDING
        AccessRequest ar3 = new AccessRequest();
        ar3.setHealthUser(user1);
        ar3.setHealthWorker(healthWorkers.get(2)); // Ana Gómez (Pediatrician)
        ar3.setClinic(clinics.get(2)); // Centro Médico Punta Gorda
        ar3.setSpecialty(specialties.stream().filter(s -> "Pediatrics".equals(s.getName())).findFirst().orElse(null));
        ar3.setStatus(AccessRequestStatus.PENDING);
        accessRequestRepository.add(ar3);

        // Create access requests for Miguel López (user2)
        // Access request to general medicine doctor at his clinic - PENDING
        AccessRequest ar4 = new AccessRequest();
        ar4.setHealthUser(user2);
        ar4.setHealthWorker(healthWorkers.get(1)); // Carlos Fernández (General Medicine)
        ar4.setClinic(clinics.get(1)); // Clínica Sanatorio Americano
        ar4.setSpecialty(
                specialties.stream().filter(s -> "General Medicine".equals(s.getName())).findFirst().orElse(null));
        ar4.setStatus(AccessRequestStatus.PENDING);
        accessRequestRepository.add(ar4);

        // Access request to surgeon at different clinic - PENDING
        AccessRequest ar5 = new AccessRequest();
        ar5.setHealthUser(user2);
        ar5.setHealthWorker(healthWorkers.get(3)); // José Martínez (Surgeon)
        ar5.setClinic(clinics.get(3)); // Policlínica Rivera
        ar5.setSpecialty(
                specialties.stream().filter(s -> "General Surgery".equals(s.getName())).findFirst().orElse(null));
        ar5.setStatus(AccessRequestStatus.PENDING);
        accessRequestRepository.add(ar5);

        // Access request to dermatologist - PENDING
        AccessRequest ar6 = new AccessRequest();
        ar6.setHealthUser(user2);
        ar6.setHealthWorker(healthWorkers.get(0)); // María Rodríguez (Cardiologist)
        ar6.setClinic(clinics.get(0)); // Hospital Central de Montevideo
        ar6.setSpecialty(specialties.stream().filter(s -> "Cardiology".equals(s.getName())).findFirst().orElse(null));
        ar6.setStatus(AccessRequestStatus.PENDING);
        accessRequestRepository.add(ar6);
    }
}
