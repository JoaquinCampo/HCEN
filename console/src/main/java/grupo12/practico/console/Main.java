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
            while (true) {
                System.out.println();
                System.out.println("-- Users --");
                System.out.println("1) Add user");
                System.out.println("2) List users");
                System.out.println("3) Search by name");
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
                        u.setDocumentType(DocumentType.ID);

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
                            HealthUserDTO addedUser = userService.add(u);
                            System.out.println("User added successfully with ID: " + addedUser.getId());
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
                                users.forEach(x -> System.out
                                        .println(x.getId() + " | " + x.getFirstName() + " " + x.getLastName()));
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
                                searchResults.forEach(x -> System.out
                                        .println(x.getId() + " | " + x.getFirstName() + " " + x.getLastName()));
                            }
                        } catch (Exception ex) {
                            System.out.println("Failed to search users: " + ex.getMessage());
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
            while (true) {
                System.out.println();
                System.out.println("-- Health Workers --");
                System.out.println("1) Add health worker");
                System.out.println("2) List health workers");
                System.out.println("3) Search by name");
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
                        hw.setDocumentType(DocumentType.ID); // Set default document type

                        System.out.print("Password: ");
                        String password = in.nextLine().trim();
                        if (password.isEmpty()) {
                            System.out.println("Error: Password is required");
                            continue;
                        }
                        hw.setPassword(password);

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
                            HealthWorkerDTO addedHw = hwService.addHealthWorker(hw);
                            System.out.println("Health worker added successfully with ID: " + addedHw.getId());
                        } catch (Exception ex) {
                            System.out.println("Failed to add health worker: " + ex.getMessage());
                            System.out.println("Please check your input and try again.");
                        }
                        break;
                    case "2":
                        hwService.getAllHealthWorkers().forEach(
                                x -> System.out.println(x.getId() + " | " + x.getFirstName() + " " + x.getLastName()));
                        break;
                    case "3":
                        System.out.print("Query: ");
                        String q = in.nextLine();
                        hwService.findHealthWorkersByName(q).forEach(
                                x -> System.out.println(x.getId() + " | " + x.getFirstName() + " " + x.getLastName()));
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
            while (true) {
                System.out.println();
                System.out.println("-- Clinics --");
                System.out.println("1) Add clinic");
                System.out.println("2) List clinics");
                System.out.println("3) Search by name");
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
                            ClinicDTO addedHp = clinicService.addClinic(hp);
                            System.out.println("Clinic added successfully with ID: " + addedHp.getId());
                        } catch (Exception ex) {
                            System.out.println("Failed to add clinic: " + ex.getMessage());
                            System.out.println("Please check your input and try again.");
                        }
                        break;
                    case "2":
                        clinicService.findAll().forEach(x -> System.out.println(x.getId() + " | " + x.getName()));
                        break;
                    case "3":
                        System.out.print("Query: ");
                        String q = in.nextLine();
                        clinicService.findByName(q).forEach(x -> System.out.println(x.getId() + " | " + x.getName()));
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
                System.out.println("3) Search by patient/author/provider name");
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

                        System.out.print("Author ID: ");
                        String authorId = in.nextLine().trim();
                        if (authorId.isEmpty()) {
                            System.out.println("Error: Author ID is required");
                            continue;
                        }
                        // Verify author exists
                        HealthWorkerDTO author = hwService.findById(authorId);
                        if (author == null) {
                            System.out.println("Error: Author not found with ID: " + authorId);
                            continue;
                        }
                        doc.setHealthWorkerIds(java.util.Set.of(authorId));

                        try {
                            ClinicalDocumentDTO addedDoc = docService.add(doc);
                            System.out.println("Clinical document added successfully with ID: " + addedDoc.getId());
                        } catch (Exception ex) {
                            System.out.println("Failed to add clinical document: " + ex.getMessage());
                            System.out.println("Please check your input and try again.");
                        }
                        break;
                    case "2":
                        docService.findAll().forEach(x -> System.out.println(x.getId() + " | " + x.getTitle()));
                        break;
                    case "3":
                        System.out.println("Search functionality not available in simplified service.");
                        System.out.println("Showing all documents instead:");
                        docService.findAll().forEach(x -> System.out.println(x.getId() + " | " + x.getTitle()));
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
