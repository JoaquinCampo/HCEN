package grupo12.practico.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "health_users")
public class HealthUser extends User {
    @ElementCollection
    @CollectionTable(name = "health_user_clinic", joinColumns = @JoinColumn(name = "health_user_id"))
    @Column(name = "clinic_names")
    private Set<String> clinicNames;

    public HealthUser() {
        super();
        this.clinicNames = new HashSet<>();
    }

    public Set<String> getClinicNames() {
        return clinicNames;
    }

    public void setClinicNames(Set<String> clinicNames) {
        this.clinicNames = clinicNames;
    }
}
