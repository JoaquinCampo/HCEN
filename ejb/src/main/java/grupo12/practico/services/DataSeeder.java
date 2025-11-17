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
                    "52537059",
                    "Xavier",
                    "Iribarnegaray",
                    Gender.MALE,
                    "xiribarnegara@hcen.uy",
                    "+59899333456",
                    LocalDate.of(2002, 10, 24),
                    "Rio Po 1234, Canelones");

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
        TypedQuery<User> ciQuery = em.createQuery(
                "SELECT u FROM User u WHERE u.ci = :ci", User.class);
        ciQuery.setParameter("ci", ci);

        try {
            ciQuery.getSingleResult();
            LOGGER.info(String.format("HcenAdmin with CI %s already exists, skipping creation", ci));
            return;
        } catch (NoResultException e) {
            // User doesn't exist by CI, continue checking
        }

        // Check if user already exists by email
        TypedQuery<User> emailQuery = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        emailQuery.setParameter("email", email);

        try {
            emailQuery.getSingleResult();
            LOGGER.info(String.format("HcenAdmin with email %s already exists, skipping creation", email));
            return;
        } catch (NoResultException e) {
            // User doesn't exist by email, proceed with creation
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
        LOGGER.info(String.format("Created HcenAdmin: %s %s (CI: %s)", firstName, lastName, ci));
    }
}