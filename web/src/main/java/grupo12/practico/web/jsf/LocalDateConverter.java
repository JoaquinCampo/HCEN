package grupo12.practico.web.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.application.FacesMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@FacesConverter(value = "localDateConverter", managed = true)
public class LocalDateConverter implements Converter<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    @Override
    public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException ex) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid date format", "Use yyyy-MM-dd (e.g., 1990-05-30)");
            throw new ConverterException(msg);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
        return value == null ? "" : value.format(FORMATTER);
    }
}
