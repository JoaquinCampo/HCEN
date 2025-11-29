package grupo12.practico.web.jsf.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocalDateConverter Tests")
class LocalDateConverterTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private UIComponent uiComponent;

    private LocalDateConverter converter;

    @BeforeEach
    void setUp() {
        converter = new LocalDateConverter();
    }

    // getAsObject tests

    @Test
    @DisplayName("getAsObject - Should convert valid date string to LocalDate")
    void getAsObject_ShouldConvertValidDateStringToLocalDate() {
        String value = "2024-05-15";

        LocalDate result = converter.getAsObject(facesContext, uiComponent, value);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 5, 15), result);
    }

    @Test
    @DisplayName("getAsObject - Should return null for null value")
    void getAsObject_ShouldReturnNullForNullValue() {
        LocalDate result = converter.getAsObject(facesContext, uiComponent, null);

        assertNull(result);
    }

    @Test
    @DisplayName("getAsObject - Should return null for empty string")
    void getAsObject_ShouldReturnNullForEmptyString() {
        LocalDate result = converter.getAsObject(facesContext, uiComponent, "");

        assertNull(result);
    }

    @Test
    @DisplayName("getAsObject - Should return null for whitespace string")
    void getAsObject_ShouldReturnNullForWhitespaceString() {
        LocalDate result = converter.getAsObject(facesContext, uiComponent, "   ");

        assertNull(result);
    }

    @Test
    @DisplayName("getAsObject - Should throw ConverterException for invalid date format")
    void getAsObject_ShouldThrowConverterExceptionForInvalidDateFormat() {
        String invalidValue = "15/05/2024"; // Wrong format

        ConverterException exception = assertThrows(ConverterException.class, () -> {
            converter.getAsObject(facesContext, uiComponent, invalidValue);
        });

        assertTrue(exception.getMessage().contains("Invalid date format"));
    }

    @Test
    @DisplayName("getAsObject - Should throw ConverterException for date with wrong separator")
    void getAsObject_ShouldThrowConverterExceptionForDateWithWrongSeparator() {
        String invalidValue = "2024/05/15"; // Wrong separator

        ConverterException exception = assertThrows(ConverterException.class, () -> {
            converter.getAsObject(facesContext, uiComponent, invalidValue);
        });

        assertTrue(exception.getMessage().contains("Invalid date format"));
    }

    @Test
    @DisplayName("getAsObject - Should throw ConverterException for non-date string")
    void getAsObject_ShouldThrowConverterExceptionForNonDateString() {
        String invalidValue = "not-a-date";

        ConverterException exception = assertThrows(ConverterException.class, () -> {
            converter.getAsObject(facesContext, uiComponent, invalidValue);
        });

        assertTrue(exception.getMessage().contains("Invalid date format"));
    }

    @Test
    @DisplayName("getAsObject - Should parse first day of month")
    void getAsObject_ShouldParseFirstDayOfMonth() {
        String value = "2024-01-01";

        LocalDate result = converter.getAsObject(facesContext, uiComponent, value);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 1, 1), result);
    }

    @Test
    @DisplayName("getAsObject - Should parse last day of month")
    void getAsObject_ShouldParseLastDayOfMonth() {
        String value = "2024-12-31";

        LocalDate result = converter.getAsObject(facesContext, uiComponent, value);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 12, 31), result);
    }

    @Test
    @DisplayName("getAsObject - Should parse leap year date")
    void getAsObject_ShouldParseLeapYearDate() {
        String value = "2024-02-29"; // 2024 is a leap year

        LocalDate result = converter.getAsObject(facesContext, uiComponent, value);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 2, 29), result);
    }

    @Test
    @DisplayName("getAsObject - Should throw exception for invalid month")
    void getAsObject_ShouldThrowExceptionForInvalidMonth() {
        String invalidValue = "2024-13-15"; // Invalid month

        assertThrows(ConverterException.class, () -> {
            converter.getAsObject(facesContext, uiComponent, invalidValue);
        });
    }

    @Test
    @DisplayName("getAsObject - Should throw exception for month zero")
    void getAsObject_ShouldThrowExceptionForMonthZero() {
        String invalidValue = "2024-00-15"; // Invalid month (zero)

        assertThrows(ConverterException.class, () -> {
            converter.getAsObject(facesContext, uiComponent, invalidValue);
        });
    }

    @Test
    @DisplayName("getAsObject - Should throw exception for day zero")
    void getAsObject_ShouldThrowExceptionForDayZero() {
        String invalidValue = "2024-05-00"; // Invalid day (zero)

        assertThrows(ConverterException.class, () -> {
            converter.getAsObject(facesContext, uiComponent, invalidValue);
        });
    }

    @Test
    @DisplayName("getAsObject - Should parse historical date")
    void getAsObject_ShouldParseHistoricalDate() {
        String value = "1900-01-01";

        LocalDate result = converter.getAsObject(facesContext, uiComponent, value);

        assertNotNull(result);
        assertEquals(LocalDate.of(1900, 1, 1), result);
    }

    @Test
    @DisplayName("getAsObject - Should parse future date")
    void getAsObject_ShouldParseFutureDate() {
        String value = "2050-12-31";

        LocalDate result = converter.getAsObject(facesContext, uiComponent, value);

        assertNotNull(result);
        assertEquals(LocalDate.of(2050, 12, 31), result);
    }

    // getAsString tests

    @Test
    @DisplayName("getAsString - Should convert LocalDate to formatted string")
    void getAsString_ShouldConvertLocalDateToFormattedString() {
        LocalDate date = LocalDate.of(2024, 5, 15);

        String result = converter.getAsString(facesContext, uiComponent, date);

        assertEquals("2024-05-15", result);
    }

    @Test
    @DisplayName("getAsString - Should return empty string for null value")
    void getAsString_ShouldReturnEmptyStringForNullValue() {
        String result = converter.getAsString(facesContext, uiComponent, null);

        assertEquals("", result);
    }

    @Test
    @DisplayName("getAsString - Should format single digit month with leading zero")
    void getAsString_ShouldFormatSingleDigitMonthWithLeadingZero() {
        LocalDate date = LocalDate.of(2024, 1, 15);

        String result = converter.getAsString(facesContext, uiComponent, date);

        assertEquals("2024-01-15", result);
    }

    @Test
    @DisplayName("getAsString - Should format single digit day with leading zero")
    void getAsString_ShouldFormatSingleDigitDayWithLeadingZero() {
        LocalDate date = LocalDate.of(2024, 5, 5);

        String result = converter.getAsString(facesContext, uiComponent, date);

        assertEquals("2024-05-05", result);
    }

    @Test
    @DisplayName("getAsString - Should format historical date correctly")
    void getAsString_ShouldFormatHistoricalDateCorrectly() {
        LocalDate date = LocalDate.of(1900, 1, 1);

        String result = converter.getAsString(facesContext, uiComponent, date);

        assertEquals("1900-01-01", result);
    }

    @Test
    @DisplayName("getAsString - Should format leap year date correctly")
    void getAsString_ShouldFormatLeapYearDateCorrectly() {
        LocalDate date = LocalDate.of(2024, 2, 29);

        String result = converter.getAsString(facesContext, uiComponent, date);

        assertEquals("2024-02-29", result);
    }

    @Test
    @DisplayName("getAsString - Should format last day of year correctly")
    void getAsString_ShouldFormatLastDayOfYearCorrectly() {
        LocalDate date = LocalDate.of(2024, 12, 31);

        String result = converter.getAsString(facesContext, uiComponent, date);

        assertEquals("2024-12-31", result);
    }

    // Round-trip tests

    @Test
    @DisplayName("roundtrip - getAsObject and getAsString should be consistent")
    void roundtrip_GetAsObjectAndGetAsStringShouldBeConsistent() {
        String originalString = "2024-05-15";

        LocalDate date = converter.getAsObject(facesContext, uiComponent, originalString);
        String resultString = converter.getAsString(facesContext, uiComponent, date);

        assertEquals(originalString, resultString);
    }

    @Test
    @DisplayName("roundtrip - getAsString and getAsObject should be consistent")
    void roundtrip_GetAsStringAndGetAsObjectShouldBeConsistent() {
        LocalDate originalDate = LocalDate.of(2024, 5, 15);

        String stringValue = converter.getAsString(facesContext, uiComponent, originalDate);
        LocalDate resultDate = converter.getAsObject(facesContext, uiComponent, stringValue);

        assertEquals(originalDate, resultDate);
    }

    @Test
    @DisplayName("getAsObject - Should handle date at year boundary")
    void getAsObject_ShouldHandleDateAtYearBoundary() {
        String newYearsEve = "2024-12-31";
        String newYearsDay = "2025-01-01";

        LocalDate eveResult = converter.getAsObject(facesContext, uiComponent, newYearsEve);
        LocalDate dayResult = converter.getAsObject(facesContext, uiComponent, newYearsDay);

        assertEquals(LocalDate.of(2024, 12, 31), eveResult);
        assertEquals(LocalDate.of(2025, 1, 1), dayResult);
        assertEquals(1, dayResult.toEpochDay() - eveResult.toEpochDay());
    }
}
