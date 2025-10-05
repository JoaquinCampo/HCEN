package grupo12.practico.console;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import javax.naming.Context;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerRemote;
import grupo12.practico.messaging.HealthWorker.HealthWorkerRegistrationProducerRemote;
import grupo12.practico.messaging.Clinic.ClinicRegistrationProducerRemote;
import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.models.ClinicType;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceRemote;
import grupo12.practico.services.Clinic.ClinicServiceRemote;
import grupo12.practico.services.HealthUser.HealthUserServiceRemote;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceRemote;

public class Main {

    public static void main(String[] args) throws Exception {
        String host = System.getProperty("app.host", "localhost");
        int port = Integer.getInteger("app.port", 8080);
        String appName = System.getProperty("app.name", "practico");
        String moduleName = System.getProperty("app.ejb.module", "practico-ejb");

        try (Scanner in = new Scanner(System.in)) {
            Context ctx = EjbLookup.createRemoteContext(host, port);
            ServiceLocator locator = new ServiceLocator(ctx, appName, moduleName);

            while (true) {
                System.out.println();
                System.out.println("=== Practico Console ===");
                System.out.println("1) Users");
                System.out.println("2) Health Workers");
                System.out.println("3) Clinics");
                System.out.println("4) Clinical Documents");
                // System.out.println("5) Access Grants"); // COMMENTED OUT: AccessGrantService
                // does not exist
                System.out.println("0) Exit");
                System.out.print("Choose: ");
                String choice = in.nextLine();
                if ("0".equals(choice))
                    break;
                switch (choice) {
                    case "1":
                        usersMenu(in, locator);
                        break;
                    case "2":
                        healthWorkersMenu(in, locator);
                        break;
                    case "3":
                        clinicsMenu(in, locator);
                        break;
                    case "4":
                        documentsMenu(in, locator);
                        break;
                    default:
                        System.out.println("Unknown option");
                }
            }
        }
    }

    private static void usersMenu(Scanner in, ServiceLocator locator) {
        try {
            HealthUserServiceRemote userService = locator.userService();
            HealthUserRegistrationProducerRemote registrationProducer = locator.healthUserRegistrationProducer();
            while (true) {
                System.out.println();
                System.out.println("-- Users --");
                System.out.println("1) Add user");
                System.out.println("2) List users");
                System.out.println("3) Search by name");
                System.out.println("4) Find by ID");
                System.out.println("0) Back");
                System.out.print("Choose: ");
                String c = in.nextLine();
                if ("0".equals(c))
                    return;
                switch (c) {
                    case "1":
                        AddHealthUserDTO u = new AddHealthUserDTO();
                        System.out.print("First name: ");
                        String firstName = in.nextLine().trim();
                        if (firstName.isEmpty()) {
                            System.out.println("Error: First name is required");
                            continue;
                        }
                        u.setFirstName(firstName);

                        System.out.print("Last name: ");
                        String lastName = in.nextLine().trim();
                        if (lastName.isEmpty()) {
                            System.out.println("Error: Last name is required");
                            continue;
                        }
                        u.setLastName(lastName);

                        System.out.print("DNI: ");
                        String dni = in.nextLine().trim();
                        if (dni.isEmpty()) {
                            System.out.println("Error: DNI is required");
                            continue;
                        }
                        u.setDocument(dni);

                        // Document type selection
                        System.out.print("Document type (ID/PASSPORT/DRIVER_LICENSE) [ID]: ");
                        String docTypeStr = in.nextLine().trim().toUpperCase();
                        if (docTypeStr.isEmpty()) {
                            u.setDocumentType(DocumentType.ID);
                        } else {
                            try {
                                u.setDocumentType(DocumentType.valueOf(docTypeStr));
                            } catch (Exception e) {
                                System.out.println("Invalid document type, using ID as default");
                                u.setDocumentType(DocumentType.ID);
                            }
                        }

                        System.out.print("Email: ");
                        u.setEmail(in.nextLine());

                        System.out.print("Password: ");
                        String password = in.nextLine().trim();
                        if (password.isEmpty()) {
                            System.out.println("Error: Password is required");
                            continue;
                        }
                        u.setPassword(password);

                        System.out.print("Phone: ");
                        u.setPhone(in.nextLine());
                        System.out.print("Address: ");
                        u.setAddress(in.nextLine());
                        System.out.print("Gender (MALE/FEMALE/OTHER) or empty: ");
                        String g = in.nextLine();
                        if (!g.isBlank()) {
                            try {
                                u.setGender(Gender.valueOf(g.trim().toUpperCase()));
                            } catch (Exception ignore) {
                            }
                        }
                        System.out.print("Date of birth (YYYY-MM-DD): ");
                        String dob = in.nextLine();
                        if (!dob.isBlank()) {
                            try {
                                LocalDate dateOfBirth = LocalDate.parse(dob.trim());
                                // Validate age requirement
                                if (java.time.Period.between(dateOfBirth, LocalDate.now()).getYears() < 18) {
                                    System.out.println("Error: User must be at least 18 years old");
                                    continue;
                                }
                                u.setDateOfBirth(dateOfBirth);
                            } catch (Exception e) {
                                System.out.println("Error: Invalid date format. Please use YYYY-MM-DD format");
                                continue;
                            }
                        } else {
                            System.out.println("Error: Date of birth is required for users");
                            continue;
                        }

                        // Note: Clinic associations are now handled via clinicIds in the DTO
                        System.out.print("Add clinic ID (empty to skip): ");
                        String clinicId = in.nextLine();
                        if (!clinicId.isBlank()) {
                            u.setClinicIds(java.util.Set.of(clinicId.trim()));
                        }

                        try {
                            registrationProducer.enqueue(u);
                            System.out.println(
                                    "Request accepted; the user will be created asynchronously.");
                        } catch (Exception ex) {
                            System.out.println("Failed to add user: " + ex.getMessage());
                            System.out.println("Please check your input and try again.");
                        }
                        break;
                    case "2":
                        try {
                            List<HealthUserDTO> users = userService.findAll();
                            if (users.isEmpty()) {
                                System.out.println("No users found.");
                            } else {
                                System.out.println("\n--- Users List ---");
                                users.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Name: " + x.getFirstName() + " " + x.getLastName());
                                    System.out
                                            .println("Document: " + x.getDocument() + " (" + x.getDocumentType() + ")");
                                    System.out.println(
                                            "Gender: " + (x.getGender() != null ? x.getGender() : "Not specified"));
                                    System.out.println(
                                            "Email: " + (x.getEmail() != null ? x.getEmail() : "Not provided"));
                                    System.out.println(
                                            "Phone: " + (x.getPhone() != null ? x.getPhone() : "Not provided"));
                                    System.out.println(
                                            "Created: " + (x.getCreatedAt() != null ? x.getCreatedAt() : "Unknown"));
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to retrieve users: " + ex.getMessage());
                        }
                        break;
                    case "3":
                        System.out.print("Query: ");
                        String q = in.nextLine();
                        try {
                            List<HealthUserDTO> searchResults = userService.findByName(q);
                            if (searchResults.isEmpty()) {
                                System.out.println("No users found matching: " + q);
                            } else {
                                System.out.println("\n--- Search Results ---");
                                searchResults.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Name: " + x.getFirstName() + " " + x.getLastName());
                                    System.out
                                            .println("Document: " + x.getDocument() + " (" + x.getDocumentType() + ")");
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to search users: " + ex.getMessage());
                        }
                        break;
                    case "4":
                        System.out.print("User ID: ");
                        String userId = in.nextLine().trim();
                        if (userId.isEmpty()) {
                            System.out.println("Error: User ID is required");
                            continue;
                        }
                        try {
                            HealthUserDTO user = userService.findById(userId);
                            if (user == null) {
                                System.out.println("No user found with ID: " + userId);
                            } else {
                                System.out.println("\n--- User Details ---");
                                System.out.println("ID: " + user.getId());
                                System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
                                System.out.println(
                                        "Document: " + user.getDocument() + " (" + user.getDocumentType() + ")");
                                System.out.println(
                                        "Gender: " + (user.getGender() != null ? user.getGender() : "Not specified"));
                                System.out.println("Date of Birth: "
                                        + (user.getDateOfBirth() != null ? user.getDateOfBirth() : "Not provided"));
                                System.out.println(
                                        "Email: " + (user.getEmail() != null ? user.getEmail() : "Not provided"));
                                System.out.println(
                                        "Phone: " + (user.getPhone() != null ? user.getPhone() : "Not provided"));
                                System.out.println(
                                        "Address: " + (user.getAddress() != null ? user.getAddress() : "Not provided"));
                                System.out.println("Clinical History ID: "
                                        + (user.getClinicalHistoryId() != null ? user.getClinicalHistoryId()
                                                : "Not assigned"));
                                System.out.println(
                                        "Created: " + (user.getCreatedAt() != null ? user.getCreatedAt() : "Unknown"));
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to find user: " + ex.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Unknown option");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void healthWorkersMenu(Scanner in, ServiceLocator locator) {
        try {
            HealthWorkerServiceRemote hwService = locator.healthWorkerService();
            HealthWorkerRegistrationProducerRemote registrationProducer = locator.healthWorkerRegistrationProducer();
            while (true) {
                System.out.println();
                System.out.println("-- Health Workers --");
                System.out.println("1) Add health worker");
                System.out.println("2) List health workers");
                System.out.println("3) Search by name");
                System.out.println("4) Find by ID");
                System.out.println("0) Back");
                System.out.print("Choose: ");
                String c = in.nextLine();
                if ("0".equals(c))
                    return;
                switch (c) {
                    case "1":
                        AddHealthWorkerDTO hw = new AddHealthWorkerDTO();
                        System.out.print("First name: ");
                        String hwFirstName = in.nextLine().trim();
                        if (hwFirstName.isEmpty()) {
                            System.out.println("Error: First name is required");
                            continue;
                        }
                        hw.setFirstName(hwFirstName);

                        System.out.print("Last name: ");
                        String hwLastName = in.nextLine().trim();
                        if (hwLastName.isEmpty()) {
                            System.out.println("Error: Last name is required");
                            continue;
                        }
                        hw.setLastName(hwLastName);

                        System.out.print("DNI: ");
                        String dni = in.nextLine().trim();
                        if (dni.isEmpty()) {
                            System.out.println("Error: DNI is required");
                            continue;
                        }
                        hw.setDocument(dni);

                        // Document type selection
                        System.out.print("Document type (ID/PASSPORT/DRIVER_LICENSE) [ID]: ");
                        String docTypeStr = in.nextLine().trim().toUpperCase();
                        if (docTypeStr.isEmpty()) {
                            hw.setDocumentType(DocumentType.ID);
                        } else {
                            try {
                                hw.setDocumentType(DocumentType.valueOf(docTypeStr));
                            } catch (Exception e) {
                                System.out.println("Invalid document type, using ID as default");
                                hw.setDocumentType(DocumentType.ID);
                            }
                        }

                        System.out.print("Password: ");
                        String password = in.nextLine().trim();
                        if (password.isEmpty()) {
                            System.out.println("Error: Password is required");
                            continue;
                        }
                        hw.setPassword(password);

                        System.out.print("Email: ");
                        hw.setEmail(in.nextLine());

                        System.out.print("Phone: ");
                        hw.setPhone(in.nextLine());

                        System.out.print("Address: ");
                        hw.setAddress(in.nextLine());

                        System.out.print("Date of birth (YYYY-MM-DD): ");
                        String dobStr = in.nextLine().trim();
                        if (dobStr.isEmpty()) {
                            System.out.println("Error: Date of birth is required");
                            continue;
                        }
                        try {
                            hw.setDateOfBirth(java.time.LocalDate.parse(dobStr));
                        } catch (Exception e) {
                            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD");
                            continue;
                        }

                        System.out.print("Gender (MALE/FEMALE/OTHER) or empty: ");
                        String g = in.nextLine();
                        if (!g.isBlank()) {
                            try {
                                hw.setGender(Gender.valueOf(g.trim().toUpperCase()));
                            } catch (Exception ignore) {
                            }
                        }
                        System.out.print("License number: ");
                        String licenseNumber = in.nextLine().trim();
                        if (licenseNumber.isEmpty()) {
                            System.out.println("Error: License number is required");
                            continue;
                        }
                        hw.setLicenseNumber(licenseNumber);

                        System.out.print("Add clinic ID (empty to skip): ");
                        String clinicId = in.nextLine();
                        if (!clinicId.isBlank()) {
                            hw.setClinicIds(java.util.Set.of(clinicId.trim()));
                        }

                        try {
                            registrationProducer.enqueue(hw);
                            System.out.println("Health worker registration request queued successfully for document: "
                                    + hw.getDocument());
                        } catch (Exception ex) {
                            System.out.println("Failed to queue health worker registration: " + ex.getMessage());
                            System.out.println("Please check your input and try again.");
                        }
                        break;
                    case "2":
                        try {
                            List<HealthWorkerDTO> workers = hwService.findAll();
                            if (workers.isEmpty()) {
                                System.out.println("No health workers found.");
                            } else {
                                System.out.println("\n--- Health Workers List ---");
                                workers.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Name: " + x.getFirstName() + " " + x.getLastName());
                                    System.out
                                            .println("Document: " + x.getDocument() + " (" + x.getDocumentType() + ")");
                                    System.out.println(
                                            "Gender: " + (x.getGender() != null ? x.getGender() : "Not specified"));
                                    System.out.println("License: " + x.getLicenseNumber());
                                    System.out.println(
                                            "Clinic IDs: " + (x.getClinicIds() != null && !x.getClinicIds().isEmpty()
                                                    ? String.join(", ", x.getClinicIds())
                                                    : "None"));
                                    System.out.println(
                                            "Created: " + (x.getCreatedAt() != null ? x.getCreatedAt() : "Unknown"));
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to retrieve health workers: " + ex.getMessage());
                        }
                        break;
                    case "3":
                        System.out.print("Query: ");
                        String q = in.nextLine();
                        try {
                            List<HealthWorkerDTO> searchResults = hwService.findByName(q);
                            if (searchResults.isEmpty()) {
                                System.out.println("No health workers found matching: " + q);
                            } else {
                                System.out.println("\n--- Search Results ---");
                                searchResults.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Name: " + x.getFirstName() + " " + x.getLastName());
                                    System.out
                                            .println("Document: " + x.getDocument() + " (" + x.getDocumentType() + ")");
                                    System.out.println("License: " + x.getLicenseNumber());
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to search health workers: " + ex.getMessage());
                        }
                        break;
                    case "4":
                        System.out.print("Health Worker ID: ");
                        String hwId = in.nextLine().trim();
                        if (hwId.isEmpty()) {
                            System.out.println("Error: Health Worker ID is required");
                            continue;
                        }
                        try {
                            HealthWorkerDTO worker = hwService.findById(hwId);
                            if (worker == null) {
                                System.out.println("No health worker found with ID: " + hwId);
                            } else {
                                System.out.println("\n--- Health Worker Details ---");
                                System.out.println("ID: " + worker.getId());
                                System.out.println("Name: " + worker.getFirstName() + " " + worker.getLastName());
                                System.out.println(
                                        "Document: " + worker.getDocument() + " (" + worker.getDocumentType() + ")");
                                System.out.println("Gender: "
                                        + (worker.getGender() != null ? worker.getGender() : "Not specified"));
                                System.out.println("License Number: " + worker.getLicenseNumber());
                                System.out.println("Clinic IDs: "
                                        + (worker.getClinicIds() != null && !worker.getClinicIds().isEmpty()
                                                ? String.join(", ", worker.getClinicIds())
                                                : "None"));
                                System.out.println("Created: "
                                        + (worker.getCreatedAt() != null ? worker.getCreatedAt() : "Unknown"));
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to find health worker: " + ex.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Unknown option");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void clinicsMenu(Scanner in, ServiceLocator locator) {
        try {
            ClinicServiceRemote clinicService = locator.clinicService();
            ClinicRegistrationProducerRemote registrationProducer = locator.clinicRegistrationProducer();
            while (true) {
                System.out.println();
                System.out.println("-- Clinics --");
                System.out.println("1) Add clinic");
                System.out.println("2) List clinics");
                System.out.println("3) Search by name");
                System.out.println("4) Find by ID");
                System.out.println("0) Back");
                System.out.print("Choose: ");
                String c = in.nextLine();
                if ("0".equals(c))
                    return;
                switch (c) {
                    case "1":
                        AddClinicDTO hp = new AddClinicDTO();
                        System.out.print("Name: ");
                        String hpName = in.nextLine().trim();
                        if (hpName.isEmpty()) {
                            System.out.println("Error: Name is required");
                            continue;
                        }
                        hp.setName(hpName);

                        System.out.print("Address: ");
                        String hpAddress = in.nextLine().trim();
                        if (hpAddress.isEmpty()) {
                            System.out.println("Error: Address is required");
                            continue;
                        }
                        hp.setAddress(hpAddress);
                        System.out.print("Phone: ");
                        hp.setPhone(in.nextLine());
                        System.out.print("Email: ");
                        hp.setEmail(in.nextLine());
                        System.out.print("Domain: ");
                        hp.setDomain(in.nextLine());
                        System.out.print(
                                "Clinic type (HOSPITAL/POLYCLINIC/PRIVATE_PRACTICE/LABORATORY/DIAGNOSTIC_CENTER/SPECIALTY_CLINIC/EMERGENCY_ROOM/REHABILITATION_CENTER/NURSING_HOME/PHARMACY): ");
                        String typeStr = in.nextLine().trim().toUpperCase();
                        if (!typeStr.isEmpty()) {
                            try {
                                hp.setType(ClinicType.valueOf(typeStr).toString());
                            } catch (Exception e) {
                                System.out.println("Invalid clinic type, setting to HOSPITAL as default");
                                hp.setType(ClinicType.HOSPITAL.toString());
                            }
                        } else {
                            hp.setType(ClinicType.HOSPITAL.toString());
                        }

                        try {
                            registrationProducer.enqueue(hp);
                            System.out.println("Request accepted; the clinic will be created asynchronously.");
                        } catch (Exception ex) {
                            System.out.println("Failed to add clinic: " + ex.getMessage());
                            System.out.println("Please check your input and try again.");
                        }
                        break;
                    case "2":
                        try {
                            List<ClinicDTO> clinics = clinicService.findAll();
                            if (clinics.isEmpty()) {
                                System.out.println("No clinics found.");
                            } else {
                                System.out.println("\n--- Clinics List ---");
                                clinics.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Name: " + x.getName());
                                    System.out
                                            .println("Type: " + (x.getType() != null ? x.getType() : "Not specified"));
                                    System.out.println(
                                            "Address: " + (x.getAddress() != null ? x.getAddress() : "Not provided"));
                                    System.out.println(
                                            "Phone: " + (x.getPhone() != null ? x.getPhone() : "Not provided"));
                                    System.out.println(
                                            "Email: " + (x.getEmail() != null ? x.getEmail() : "Not provided"));
                                    System.out.println(
                                            "Domain: " + (x.getDomain() != null ? x.getDomain() : "Not provided"));
                                    System.out.println(
                                            "Created: " + (x.getCreatedAt() != null ? x.getCreatedAt() : "Unknown"));
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to retrieve clinics: " + ex.getMessage());
                        }
                        break;
                    case "3":
                        System.out.print("Query: ");
                        String q = in.nextLine();
                        try {
                            List<ClinicDTO> searchResults = clinicService.findByName(q);
                            if (searchResults.isEmpty()) {
                                System.out.println("No clinics found matching: " + q);
                            } else {
                                System.out.println("\n--- Search Results ---");
                                searchResults.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Name: " + x.getName());
                                    System.out
                                            .println("Type: " + (x.getType() != null ? x.getType() : "Not specified"));
                                    System.out.println(
                                            "Address: " + (x.getAddress() != null ? x.getAddress() : "Not provided"));
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to search clinics: " + ex.getMessage());
                        }
                        break;
                    case "4":
                        System.out.print("Clinic ID: ");
                        String clinicId = in.nextLine().trim();
                        if (clinicId.isEmpty()) {
                            System.out.println("Error: Clinic ID is required");
                            continue;
                        }
                        try {
                            ClinicDTO clinic = clinicService.findById(clinicId);
                            if (clinic == null) {
                                System.out.println("No clinic found with ID: " + clinicId);
                            } else {
                                System.out.println("\n--- Clinic Details ---");
                                System.out.println("ID: " + clinic.getId());
                                System.out.println("Name: " + clinic.getName());
                                System.out.println(
                                        "Type: " + (clinic.getType() != null ? clinic.getType() : "Not specified"));
                                System.out.println("Address: "
                                        + (clinic.getAddress() != null ? clinic.getAddress() : "Not provided"));
                                System.out.println(
                                        "Phone: " + (clinic.getPhone() != null ? clinic.getPhone() : "Not provided"));
                                System.out.println(
                                        "Email: " + (clinic.getEmail() != null ? clinic.getEmail() : "Not provided"));
                                System.out.println("Domain: "
                                        + (clinic.getDomain() != null ? clinic.getDomain() : "Not provided"));
                                System.out.println("Created: "
                                        + (clinic.getCreatedAt() != null ? clinic.getCreatedAt() : "Unknown"));
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to find clinic: " + ex.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Unknown option");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void documentsMenu(Scanner in, ServiceLocator locator) {
        try {
            ClinicalDocumentServiceRemote docService = locator.clinicalDocumentService();
            HealthUserServiceRemote userService = locator.userService();
            HealthWorkerServiceRemote hwService = locator.healthWorkerService();
            while (true) {
                System.out.println();
                System.out.println("-- Clinical Documents --");
                System.out.println("1) Add document");
                System.out.println("2) List documents");
                System.out.println("3) Search by title");
                System.out.println("4) Find by ID");
                System.out.println("0) Back");
                System.out.print("Choose: ");
                String c = in.nextLine();
                if ("0".equals(c))
                    return;
                switch (c) {
                    case "1":
                        AddClinicalDocumentDTO doc = new AddClinicalDocumentDTO();
                        System.out.print("Title: ");
                        String title = in.nextLine().trim();
                        if (title.isEmpty()) {
                            System.out.println("Error: Title is required");
                            continue;
                        }
                        doc.setTitle(title);

                        System.out.print("Content URL (S3 URL): ");
                        String contentUrl = in.nextLine().trim();
                        if (contentUrl.isEmpty()) {
                            System.out.println("Error: Content URL is required");
                            continue;
                        }
                        doc.setContentUrl(contentUrl);

                        System.out.print("Patient ID: ");
                        String userId = in.nextLine().trim();
                        if (userId.isEmpty()) {
                            System.out.println("Error: Patient ID is required");
                            continue;
                        }
                        // Verify patient exists
                        HealthUserDTO patient = userService.findById(userId);
                        if (patient == null) {
                            System.out.println("Error: Patient not found with ID: " + userId);
                            continue;
                        }
                        doc.setClinicalHistoryId(userId); // Use patient ID as clinical history ID

                        // Allow multiple health workers
                        System.out.println("Health Workers (enter IDs separated by commas, or press Enter to skip): ");
                        String healthWorkerIds = in.nextLine().trim();
                        if (!healthWorkerIds.isEmpty()) {
                            java.util.Set<String> workerIds = new java.util.HashSet<>();
                            String[] ids = healthWorkerIds.split(",");
                            boolean allValid = true;

                            for (String id : ids) {
                                String trimmedId = id.trim();
                                if (!trimmedId.isEmpty()) {
                                    // Verify health worker exists
                                    try {
                                        HealthWorkerDTO worker = hwService.findById(trimmedId);
                                        if (worker == null) {
                                            System.out
                                                    .println("Warning: Health worker not found with ID: " + trimmedId);
                                            allValid = false;
                                        } else {
                                            workerIds.add(trimmedId);
                                        }
                                    } catch (Exception e) {
                                        System.out.println(
                                                "Error checking health worker ID " + trimmedId + ": " + e.getMessage());
                                        allValid = false;
                                    }
                                }
                            }

                            if (!allValid) {
                                System.out.println("Some health worker IDs were invalid. Continue anyway? (y/n): ");
                                String confirm = in.nextLine().trim().toLowerCase();
                                if (!"y".equals(confirm) && !"yes".equals(confirm)) {
                                    continue;
                                }
                            }

                            if (!workerIds.isEmpty()) {
                                doc.setHealthWorkerIds(workerIds);
                            }
                        }

                        try {
                            ClinicalDocumentDTO addedDoc = docService.add(doc);
                            System.out.println("Clinical document added successfully with ID: " + addedDoc.getId());
                        } catch (Exception ex) {
                            System.out.println("Failed to add clinical document: " + ex.getMessage());
                            System.out.println("Please check your input and try again.");
                        }
                        break;
                    case "2":
                        try {
                            List<ClinicalDocumentDTO> documents = docService.findAll();
                            if (documents.isEmpty()) {
                                System.out.println("No clinical documents found.");
                            } else {
                                System.out.println("\n--- Clinical Documents List ---");
                                documents.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Title: " + x.getTitle());
                                    System.out.println("Patient ID: "
                                            + (x.getClinicalHistoryId() != null ? x.getClinicalHistoryId()
                                                    : "Not assigned"));
                                    System.out.println("Health Worker IDs: "
                                            + (x.getHealthWorkerIds() != null && !x.getHealthWorkerIds().isEmpty()
                                                    ? String.join(", ", x.getHealthWorkerIds())
                                                    : "None"));
                                    System.out.println("Content URL: "
                                            + (x.getContentUrl() != null ? x.getContentUrl() : "Not provided"));
                                    System.out.println(
                                            "Issued: " + (x.getIssuedAt() != null ? x.getIssuedAt() : "Unknown"));
                                    System.out.println(
                                            "Created: " + (x.getCreatedAt() != null ? x.getCreatedAt() : "Unknown"));
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to retrieve clinical documents: " + ex.getMessage());
                        }
                        break;
                    case "3":
                        System.out.print("Search by title: ");
                        String titleQuery = in.nextLine().trim();
                        if (titleQuery.isEmpty()) {
                            System.out.println("Error: Search query is required");
                            continue;
                        }
                        try {
                            List<ClinicalDocumentDTO> searchResults = docService.findByTitle(titleQuery);
                            if (searchResults.isEmpty()) {
                                System.out.println("No clinical documents found matching title: " + titleQuery);
                            } else {
                                System.out.println("\n--- Search Results ---");
                                searchResults.forEach(x -> {
                                    System.out.println("ID: " + x.getId());
                                    System.out.println("Title: " + x.getTitle());
                                    System.out.println("Patient ID: "
                                            + (x.getClinicalHistoryId() != null ? x.getClinicalHistoryId()
                                                    : "Not assigned"));
                                    System.out.println("Content URL: "
                                            + (x.getContentUrl() != null ? x.getContentUrl() : "Not provided"));
                                    System.out.println("---");
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to search clinical documents: " + ex.getMessage());
                        }
                        break;
                    case "4":
                        System.out.print("Document ID: ");
                        String docId = in.nextLine().trim();
                        if (docId.isEmpty()) {
                            System.out.println("Error: Document ID is required");
                            continue;
                        }
                        try {
                            ClinicalDocumentDTO document = docService.findById(docId);
                            if (document == null) {
                                System.out.println("No clinical document found with ID: " + docId);
                            } else {
                                System.out.println("\n--- Clinical Document Details ---");
                                System.out.println("ID: " + document.getId());
                                System.out.println("Title: " + document.getTitle());
                                System.out.println("Patient ID: "
                                        + (document.getClinicalHistoryId() != null ? document.getClinicalHistoryId()
                                                : "Not assigned"));
                                System.out.println("Health Worker IDs: " + (document.getHealthWorkerIds() != null
                                        && !document.getHealthWorkerIds().isEmpty()
                                                ? String.join(", ", document.getHealthWorkerIds())
                                                : "None"));
                                System.out.println(
                                        "Content URL: " + (document.getContentUrl() != null ? document.getContentUrl()
                                                : "Not provided"));
                                System.out.println("Issued: "
                                        + (document.getIssuedAt() != null ? document.getIssuedAt() : "Unknown"));
                                System.out.println("Created: "
                                        + (document.getCreatedAt() != null ? document.getCreatedAt() : "Unknown"));
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to find clinical document: " + ex.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Unknown option");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // COMMENTED OUT: AccessGrantService does not exist
    /*
     * private static void accessGrantsMenu(Scanner in, ServiceLocator locator) {
     * try {
     * // Note: AccessGrant service would need to be added to ServiceLocator
     * // For now, just show a placeholder
     * System.out.println("Access Grants management coming soon...");
     * System.out.
     * println("This feature will allow managing clinical data access permissions."
     * );
     * } catch (Exception e) {
     * System.out.println("Error: " + e.getMessage());
     * }
     * }
     */
}
