package grupo12.practico.dtos.User;

public class AddUserDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
