package grupo12.practico.services.Provider;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.dtos.Provider.ProviderDTO;
import grupo12.practico.models.Provider;
import grupo12.practico.repositories.Provider.ProviderRepositoryLocal;
import grupo12.practico.utils.HttpClientUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
public class ProviderServiceBean implements ProviderServiceLocal {

    // Sé que no es lo más prolijo y debería ir en un mini refactor, TODO para dsp
    private static final String BASE_URL = getEnvOrDefault("app.external.clinicApiUrl",
            "http://host.docker.internal:3000/api/clinics");

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getProperty(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    @EJB
    private ProviderRepositoryLocal providerRepository;

    @Override
    public ProviderDTO create(AddProviderDTO addProviderDTO) {
        validateAddProviderDTO(addProviderDTO);

        Provider provider = new Provider();
        provider.setName(addProviderDTO.getProviderName());

        Provider createdProvider = providerRepository.create(provider);
        return createdProvider.toDTO();
    }

    @Override
    public List<ProviderDTO> findAll() {
        List<Provider> providers = providerRepository.findAll();
        return providers.stream()
                .map(Provider::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderDTO findByName(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            return null;
        }

        Provider provider = providerRepository.findByName(providerName.trim());
        return provider != null ? provider.toDTO() : null;
    }

    @Override
    public List<ClinicDTO> fetchClinicsByProvider(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String url = BASE_URL + "?providerName=" + HttpClientUtil.encodeParam(providerName.trim());
            String jsonResponse = HttpClientUtil.get(url);
            return parseClinicArray(jsonResponse);
        } catch (Exception e) {
            System.err.println("Failed to fetch clinics from external API: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Simple JSON parser for clinic array response.
     * Expects format:
     * [{"id":"...","name":"...","email":"...","phone":"...","address":"...","createdAt":"...","updatedAt":"..."}]
     */
    private List<ClinicDTO> parseClinicArray(String json) {
        List<ClinicDTO> clinics = new ArrayList<>();

        if (json == null) {
            return clinics;
        }

        String trimmed = json.trim();
        if (trimmed.isEmpty() || trimmed.equals("[]")) {
            return clinics;
        }

        // Remove the outer array brackets
        String content = trimmed;
        if (content.startsWith("[")) {
            content = content.substring(1);
        }
        if (content.endsWith("]")) {
            content = content.substring(0, content.length() - 1);
        }

        // Split by },{
        String[] objects = content.split("\\},\\s*\\{");

        for (String obj : objects) {
            // Clean up the object string
            obj = obj.trim();
            if (obj.startsWith("{")) {
                obj = obj.substring(1);
            }
            if (obj.endsWith("}")) {
                obj = obj.substring(0, obj.length() - 1);
            }

            ClinicDTO clinic = parseClinicObject(obj);
            if (clinic != null) {
                clinics.add(clinic);
            }
        }

        return clinics;
    }

    /**
     * Parse a single clinic object from JSON string
     */
    private ClinicDTO parseClinicObject(String json) {
        try {
            ClinicDTO clinic = new ClinicDTO();

            clinic.setId(extractJsonValue(json, "id"));
            clinic.setName(extractJsonValue(json, "name"));
            clinic.setEmail(extractJsonValue(json, "email"));
            clinic.setPhone(extractJsonValue(json, "phone"));
            clinic.setAddress(extractJsonValue(json, "address"));

            String createdAtStr = extractJsonValue(json, "createdAt");
            if (createdAtStr != null && !createdAtStr.isEmpty()) {
                try {
                    // Extract date part from ISO timestamp (e.g., "2025-11-10T03:22:51.526Z" ->
                    // "2025-11-10")
                    String datePart = createdAtStr.length() >= 10 ? createdAtStr.substring(0, 10) : createdAtStr;
                    clinic.setCreatedAt(LocalDate.parse(datePart));
                } catch (Exception e) {
                    System.err.println("Failed to parse createdAt date: " + createdAtStr);
                }
            }

            String updatedAtStr = extractJsonValue(json, "updatedAt");
            if (updatedAtStr != null && !updatedAtStr.isEmpty()) {
                try {
                    // Extract date part from ISO timestamp
                    String datePart = updatedAtStr.length() >= 10 ? updatedAtStr.substring(0, 10) : updatedAtStr;
                    clinic.setUpdatedAt(LocalDate.parse(datePart));
                } catch (Exception e) {
                    System.err.println("Failed to parse updatedAt date: " + updatedAtStr);
                }
            }

            return clinic;
        } catch (Exception e) {
            System.err.println("Failed to parse clinic object: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract a value from JSON string by key
     */
    private String extractJsonValue(String json, String key) {
        try {
            // Match "key":"value" or "key":null
            Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(json);

            if (matcher.find()) {
                return matcher.group(1);
            }

            // Try to match null value
            Pattern nullPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*null");
            Matcher nullMatcher = nullPattern.matcher(json);

            if (nullMatcher.find()) {
                return null;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void validateAddProviderDTO(AddProviderDTO addProviderDTO) {
        if (addProviderDTO == null) {
            throw new ValidationException("Provider data must not be null");
        }
        if (addProviderDTO.getProviderName() == null || addProviderDTO.getProviderName().trim().isEmpty()) {
            throw new ValidationException("Provider name is required");
        }
    }
}