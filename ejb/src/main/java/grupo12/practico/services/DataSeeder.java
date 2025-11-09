package grupo12.practico.services;

import grupo12.practico.models.Gender;
import grupo12.practico.models.HcenAdmin;
import grupo12.practico.models.User;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Data seeding service that creates initial admin users on application startup.
 * Only runs when SEED environment variable is set to "true".
 */
@Singleton
@Startup
public class DataSeeder {

    private static final Logger LOGGER = Logger.getLogger(DataSeeder.class.getName());

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @PostConstruct
    public void init() {
        String seedEnabled = System.getenv("SEED");
        if (!"true".equalsIgnoreCase(seedEnabled)) {
            LOGGER.info("Data seeding disabled (SEED != true)");
            return;
        }

        LOGGER.info("Starting data seeding...");

        try {
            createHcenAdminIfNotExists(
                    "54053584",
                    "Francisco",
                    "Simonelli",
                    Gender.MALE,
                    "francisco.simonelli@hcen.uy",
                    "+59899333456",
                    LocalDate.of(2000, 10, 20),
                    "Av. 18 de Julio 1234, Montevideo");

            createHcenAdminIfNotExists(
                    "12345678",
                    "Juan",
                    "Pérez",
                    Gender.MALE,
                    "juan.perez@hcen.uy",
                    "+59899123456",
                    LocalDate.of(1980, 1, 15),
                    "Av. 18 de Julio 1234, Montevideo");

            LOGGER.info("Data seeding completed successfully");
        } catch (Exception e) {
            LOGGER.severe("Error during data seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createHcenAdminIfNotExists(String ci, String firstName, String lastName,
            Gender gender, String email, String phone,
            LocalDate dateOfBirth, String address) {

        // Check if user already exists by CI
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.ci = :ci", User.class);
        query.setParameter("ci", ci);

        try {
            query.getSingleResult();
            LOGGER.info("HcenAdmin with CI " + ci + " already exists, skipping creation");
            return;
        } catch (NoResultException e) {
            // User doesn't exist, proceed with creation
        }

        // Check if email already exists
        TypedQuery<User> emailQuery = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        emailQuery.setParameter("email", email);

        try {
            emailQuery.getSingleResult();
            LOGGER.info("User with email " + email + " already exists, skipping HcenAdmin creation");
            return;
        } catch (NoResultException e) {
            // Email doesn't exist, proceed with creation
        }

        // Create new HcenAdmin
        HcenAdmin admin = new HcenAdmin();
        admin.setCi(ci);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setGender(gender);
        admin.setEmail(email);
        admin.setPhone(phone);
        admin.setDateOfBirth(dateOfBirth);
        admin.setAddress(address);

        em.persist(admin);
        LOGGER.info("Created HcenAdmin: " + firstName + " " + lastName + " (CI: " + ci + ")");
    }
}