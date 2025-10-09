package grupo12.practico.services;

import jakarta.ejb.Local;
import java.util.Map;

@Local
public interface ExternalDataServiceLocal {

    Map<String, Object> geocodeAddress(String address);

    Map<String, Object> getDemographicData(String firstName);
}
