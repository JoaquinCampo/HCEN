package grupo12.practico.dtos.Clinic;

public class AddClinicDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private ClinicAdminDTO clinicAdmin = new ClinicAdminDTO();

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

    public ClinicAdminDTO getClinicAdmin() {
        return clinicAdmin;
    }

    public void setClinicAdmin(ClinicAdminDTO clinicAdmin) {
        this.clinicAdmin = clinicAdmin != null ? clinicAdmin : new ClinicAdminDTO();
    }
}
