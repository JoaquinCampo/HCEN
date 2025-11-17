package grupo12.practico.integration.pdi;

import java.time.LocalDate;

public class PersonaInfo {

    private String ci;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Integer sexo;
    private Integer codNacionalidad;

    public PersonaInfo() {
    }

    public PersonaInfo(String ci, String nombreCompleto, LocalDate fechaNacimiento, Integer sexo, Integer codNacionalidad) {
        this.ci = ci;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.codNacionalidad = codNacionalidad;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getSexo() {
        return sexo;
    }

    public void setSexo(Integer sexo) {
        this.sexo = sexo;
    }

    public Integer getCodNacionalidad() {
        return codNacionalidad;
    }

    public void setCodNacionalidad(Integer codNacionalidad) {
        this.codNacionalidad = codNacionalidad;
    }
}

