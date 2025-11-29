package grupo12.practico.integration.pdi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PersonaInfo Tests")
class PersonaInfoTest {

    @Test
    @DisplayName("Constructor - Should create PersonaInfo with all parameters")
    void constructor_ShouldCreatePersonaInfoWithAllParameters() {
        LocalDate fechaNacimiento = LocalDate.of(1990, 5, 15);
        PersonaInfo persona = new PersonaInfo("12345678", "Juan Pérez", fechaNacimiento, 1, 858);

        assertEquals("12345678", persona.getCi());
        assertEquals("Juan Pérez", persona.getNombreCompleto());
        assertEquals(fechaNacimiento, persona.getFechaNacimiento());
        assertEquals(1, persona.getSexo());
        assertEquals(858, persona.getCodNacionalidad());
    }

    @Test
    @DisplayName("Default Constructor - Should create PersonaInfo with null values")
    void defaultConstructor_ShouldCreatePersonaInfoWithNullValues() {
        PersonaInfo persona = new PersonaInfo();

        assertNull(persona.getCi());
        assertNull(persona.getNombreCompleto());
        assertNull(persona.getFechaNacimiento());
        assertNull(persona.getSexo());
        assertNull(persona.getCodNacionalidad());
    }

    @Test
    @DisplayName("Setters - Should set CI correctly")
    void setters_ShouldSetCiCorrectly() {
        PersonaInfo persona = new PersonaInfo();
        persona.setCi("87654321");

        assertEquals("87654321", persona.getCi());
    }

    @Test
    @DisplayName("Setters - Should set nombreCompleto correctly")
    void setters_ShouldSetNombreCompletoCorrectly() {
        PersonaInfo persona = new PersonaInfo();
        persona.setNombreCompleto("María García López");

        assertEquals("María García López", persona.getNombreCompleto());
    }

    @Test
    @DisplayName("Setters - Should set fechaNacimiento correctly")
    void setters_ShouldSetFechaNacimientoCorrectly() {
        PersonaInfo persona = new PersonaInfo();
        LocalDate fecha = LocalDate.of(1985, 12, 25);
        persona.setFechaNacimiento(fecha);

        assertEquals(fecha, persona.getFechaNacimiento());
    }

    @Test
    @DisplayName("Setters - Should set sexo correctly")
    void setters_ShouldSetSexoCorrectly() {
        PersonaInfo persona = new PersonaInfo();
        persona.setSexo(2);

        assertEquals(2, persona.getSexo());
    }

    @Test
    @DisplayName("Setters - Should set codNacionalidad correctly")
    void setters_ShouldSetCodNacionalidadCorrectly() {
        PersonaInfo persona = new PersonaInfo();
        persona.setCodNacionalidad(32);

        assertEquals(32, persona.getCodNacionalidad());
    }

    @Test
    @DisplayName("Constructor - Should handle null values in parameters")
    void constructor_ShouldHandleNullValuesInParameters() {
        PersonaInfo persona = new PersonaInfo(null, null, null, null, null);

        assertNull(persona.getCi());
        assertNull(persona.getNombreCompleto());
        assertNull(persona.getFechaNacimiento());
        assertNull(persona.getSexo());
        assertNull(persona.getCodNacionalidad());
    }

    @Test
    @DisplayName("Constructor - Should handle empty string CI")
    void constructor_ShouldHandleEmptyStringCi() {
        PersonaInfo persona = new PersonaInfo("", "Test Name", LocalDate.now(), 1, 858);

        assertEquals("", persona.getCi());
    }

    @Test
    @DisplayName("Setters - Should allow updating all fields")
    void setters_ShouldAllowUpdatingAllFields() {
        PersonaInfo persona = new PersonaInfo("12345678", "Original Name", LocalDate.of(1990, 1, 1), 1, 858);

        persona.setCi("87654321");
        persona.setNombreCompleto("Updated Name");
        persona.setFechaNacimiento(LocalDate.of(2000, 6, 15));
        persona.setSexo(2);
        persona.setCodNacionalidad(32);

        assertEquals("87654321", persona.getCi());
        assertEquals("Updated Name", persona.getNombreCompleto());
        assertEquals(LocalDate.of(2000, 6, 15), persona.getFechaNacimiento());
        assertEquals(2, persona.getSexo());
        assertEquals(32, persona.getCodNacionalidad());
    }

    @Test
    @DisplayName("Constructor - Should handle sexo value for male (1)")
    void constructor_ShouldHandleSexoValueForMale() {
        PersonaInfo persona = new PersonaInfo("12345678", "Test", LocalDate.now(), 1, 858);

        assertEquals(1, persona.getSexo());
    }

    @Test
    @DisplayName("Constructor - Should handle sexo value for female (2)")
    void constructor_ShouldHandleSexoValueForFemale() {
        PersonaInfo persona = new PersonaInfo("12345678", "Test", LocalDate.now(), 2, 858);

        assertEquals(2, persona.getSexo());
    }

    @Test
    @DisplayName("Constructor - Should handle various CI formats")
    void constructor_ShouldHandleVariousCiFormats() {
        PersonaInfo persona7Digits = new PersonaInfo("1234567", "Test", LocalDate.now(), 1, 858);
        PersonaInfo persona8Digits = new PersonaInfo("12345678", "Test", LocalDate.now(), 1, 858);

        assertEquals("1234567", persona7Digits.getCi());
        assertEquals("12345678", persona8Digits.getCi());
    }

    @Test
    @DisplayName("Constructor - Should handle Uruguayan nationality code (858)")
    void constructor_ShouldHandleUruguayanNationalityCode() {
        PersonaInfo persona = new PersonaInfo("12345678", "Test", LocalDate.now(), 1, 858);

        assertEquals(858, persona.getCodNacionalidad());
    }

    @Test
    @DisplayName("Constructor - Should handle long full names")
    void constructor_ShouldHandleLongFullNames() {
        String longName = "Juan Carlos María José de los Santos Rodríguez García López Fernández";
        PersonaInfo persona = new PersonaInfo("12345678", longName, LocalDate.now(), 1, 858);

        assertEquals(longName, persona.getNombreCompleto());
    }

    @Test
    @DisplayName("Setters - Should allow null values after construction")
    void setters_ShouldAllowNullValuesAfterConstruction() {
        PersonaInfo persona = new PersonaInfo("12345678", "Test", LocalDate.now(), 1, 858);

        persona.setCi(null);
        persona.setNombreCompleto(null);
        persona.setFechaNacimiento(null);
        persona.setSexo(null);
        persona.setCodNacionalidad(null);

        assertNull(persona.getCi());
        assertNull(persona.getNombreCompleto());
        assertNull(persona.getFechaNacimiento());
        assertNull(persona.getSexo());
        assertNull(persona.getCodNacionalidad());
    }

    @Test
    @DisplayName("Constructor - Should handle historical birth dates")
    void constructor_ShouldHandleHistoricalBirthDates() {
        LocalDate historicalDate = LocalDate.of(1920, 3, 10);
        PersonaInfo persona = new PersonaInfo("12345678", "Test", historicalDate, 1, 858);

        assertEquals(historicalDate, persona.getFechaNacimiento());
    }

    @Test
    @DisplayName("Constructor - Should handle recent birth dates")
    void constructor_ShouldHandleRecentBirthDates() {
        LocalDate recentDate = LocalDate.of(2023, 11, 15);
        PersonaInfo persona = new PersonaInfo("12345678", "Test", recentDate, 1, 858);

        assertEquals(recentDate, persona.getFechaNacimiento());
    }
}
