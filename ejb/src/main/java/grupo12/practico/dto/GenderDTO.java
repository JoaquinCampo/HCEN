package grupo12.practico.dto;

import java.io.Serializable;

import grupo12.practico.models.Gender;

public class GenderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Gender gender;

    public GenderDTO() {
    }

    public GenderDTO(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
