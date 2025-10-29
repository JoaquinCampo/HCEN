package grupo12.practico.dtos.Auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for OIDC user info response from gub.uy
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OidcUserInfoDTO {

    @JsonProperty("sub")
    private String subject;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    // gub.uy specific fields
    @JsonProperty("nombre_completo")
    private String fullName;

    @JsonProperty("primer_nombre")
    private String firstName;

    @JsonProperty("segundo_nombre")
    private String secondName;

    @JsonProperty("primer_apellido")
    private String firstLastName;

    @JsonProperty("segundo_apellido")
    private String secondLastName;

    @JsonProperty("numero_documento")
    private String id;

    public OidcUserInfoDTO() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getFullName() {
        return fullName;
    }

    public void seFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
