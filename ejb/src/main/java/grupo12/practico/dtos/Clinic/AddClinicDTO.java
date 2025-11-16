package grupo12.practico.dtos.Clinic;

public class AddClinicDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String providerName;  
    private ClinicAdminDTO clinicAdmin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public ClinicAdminDTO getClinicAdmin() {
        return clinicAdmin;
    }

    public void setClinicAdmin(ClinicAdminDTO clinicAdmin) {
        this.clinicAdmin = clinicAdmin;
    }
}
