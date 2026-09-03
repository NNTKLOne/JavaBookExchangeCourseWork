package coursework.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates one EntityManagerFactory for the application and allows local
 * database credentials to be supplied without committing them to source code.
 */
public final class Database {
    private static final String PERSISTENCE_UNIT = "coursework";

    private Database() {
    }

    public static EntityManagerFactory entityManagerFactory() {
        return Holder.ENTITY_MANAGER_FACTORY;
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, Object> overrides = new HashMap<>();
        putIfPresent(overrides, "jakarta.persistence.jdbc.url", setting(
                "coursework.db.url", "COURSEWORK_DB_URL"));
        putIfPresent(overrides, "jakarta.persistence.jdbc.user", setting(
                "coursework.db.user", "COURSEWORK_DB_USER"));
        putIfPresent(overrides, "jakarta.persistence.jdbc.password", setting(
                "coursework.db.password", "COURSEWORK_DB_PASSWORD"));

        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, overrides);
    }

    private static String setting(String systemProperty, String environmentVariable) {
        String value = System.getProperty(systemProperty);
        return value == null || value.isBlank() ? System.getenv(environmentVariable) : value;
    }

    private static void putIfPresent(Map<String, Object> properties, String key, String value) {
        if (value != null && !value.isBlank()) {
            properties.put(key, value);
        }
    }

    private static final class Holder {
        private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
                createEntityManagerFactory();
    }
}
