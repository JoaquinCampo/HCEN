package grupo12.practico.repositories.ClinicalHistory;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import grupo12.practico.models.ClinicalHistory;

@Singleton
@Startup
@Local(ClinicalHistoryRepositoryLocal.class)
@Remote(ClinicalHistoryRepositoryRemote.class)
public class ClinicalHistoryRepositoryBean implements ClinicalHistoryRepositoryRemote {

    private final Map<String, ClinicalHistory> clinicalHistoryMap = new HashMap<>();

    @Override
    public List<ClinicalHistory> findAll() {
        return new ArrayList<>(clinicalHistoryMap.values());
    }

    @Override
    public ClinicalHistory findById(String id) {
        if (id == null || id.trim().isEmpty())
            return null;
        return clinicalHistoryMap.get(id);
    }

    @Override
    public ClinicalHistory add(ClinicalHistory clinicalHistory) {
        if (clinicalHistory == null || clinicalHistory.getId() == null)
            return clinicalHistory;
        clinicalHistoryMap.put(clinicalHistory.getId(), clinicalHistory);
        return clinicalHistory;
    }

}
