package grupo12.practico.services;

import jakarta.ejb.Stateless;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Stateless
public class ExternalDataServiceBean implements ExternalDataServiceLocal {

    private static final Logger logger = Logger.getLogger(ExternalDataServiceBean.class.getName());

    private final HttpClient httpClient;

    public ExternalDataServiceBean() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public Map<String, Object> geocodeAddress(String address) {
        Map<String, Object> result = new HashMap<>();

        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=jsonv2&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("User-Agent", "HealthClinic/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                if (responseBody.startsWith("[") && responseBody.length() > 2) {
                    Pattern latPattern = Pattern.compile("\"lat\":\"([^\"]+)\"");
                    Pattern lonPattern = Pattern.compile("\"lon\":\"([^\"]+)\"");

                    Matcher latMatcher = latPattern.matcher(responseBody);
                    Matcher lonMatcher = lonPattern.matcher(responseBody);

                    if (latMatcher.find() && lonMatcher.find()) {
                        result.put("latitude", Double.parseDouble(latMatcher.group(1)));
                        result.put("longitude", Double.parseDouble(lonMatcher.group(1)));
                        result.put("address", address);
                        result.put("geocoded", true);
                    }
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error geocoding address: " + address, e);
            result.put("error", "Geocoding failed: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> getDemographicData(String firstName) {
        Map<String, Object> demographics = new HashMap<>();

        try {
            String encodedName = URLEncoder.encode(firstName, StandardCharsets.UTF_8);
            String url = "https://api.genderize.io/?name=" + encodedName;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("User-Agent", "HealthClinic/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                Pattern genderPattern = Pattern.compile("\"gender\":\"([^\"]+)\"");
                Pattern probabilityPattern = Pattern.compile("\"probability\":([0-9.]+)");

                Matcher genderMatcher = genderPattern.matcher(responseBody);
                Matcher probabilityMatcher = probabilityPattern.matcher(responseBody);

                if (genderMatcher.find()) {
                    demographics.put("predictedGender", genderMatcher.group(1));
                }
                if (probabilityMatcher.find()) {
                    demographics.put("genderConfidence", Double.parseDouble(probabilityMatcher.group(1)));
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error retrieving demographic data for: " + firstName, e);
        }

        return demographics;
    }
}