package grupo12.practico.dtos;

import java.io.Serializable;
import java.time.LocalDate;

public class SpecialtyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
