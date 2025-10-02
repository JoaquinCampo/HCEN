package grupo12.practico.soap;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class ClinicSoapService {

    @EJB
    private ClinicServiceLocal clinicService;

    public List<ClinicDTO> getAllClinics() {
        return clinicService.findAll();
    }

    public ClinicDTO getClinicById(String id) {
        return clinicService.findById(id);
    }

    public List<ClinicDTO> searchClinicsByName(String name) {
        return clinicService.findByName(name);
    }

    public ClinicDTO createClinic(AddClinicDTO clinicData) {
        return clinicService.addClinic(clinicData);
    }
}