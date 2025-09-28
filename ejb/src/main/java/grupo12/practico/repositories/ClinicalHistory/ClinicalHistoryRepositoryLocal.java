package grupo12.practico.repositories.ClinicalHistory;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.ClinicalHistory;

@Local
public interface ClinicalHistoryRepositoryLocal {
    ClinicalHistory add(ClinicalHistory doc);

    List<ClinicalHistory> findAll();

    ClinicalHistory findById(String id);
}
