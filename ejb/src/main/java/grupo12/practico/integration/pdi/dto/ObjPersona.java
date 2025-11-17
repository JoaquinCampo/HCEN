package grupo12.practico.integration.pdi.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objPersona", propOrder = {
    "codTipoDocumento",
    "nroDocumento",
    "nombre1",
    "nombre2",
    "apellido1",
    "apellido2",
    "apellidoAdoptivo1",
    "apellidoAdoptivo2",
    "sexo",
    "fechaNacimiento",
    "codNacionalidad",
    "nombreEnCedula"
})
public class ObjPersona {

    @XmlElement(name = "CodTipoDocumento")
    private String codTipoDocumento;

    @XmlElement(name = "NroDocumento")
    private String nroDocumento;

    @XmlElement(name = "Nombre1")
    private String nombre1;

    @XmlElement(name = "Nombre2")
    private String nombre2;

    @XmlElement(name = "Apellido1")
    private String apellido1;

    @XmlElement(name = "Apellido2")
    private String apellido2;

    @XmlElement(name = "ApellidoAdoptivo1")
    private String apellidoAdoptivo1;

    @XmlElement(name = "ApellidoAdoptivo2")
    private String apellidoAdoptivo2;

    @XmlElement(name = "Sexo")
    private Integer sexo;

    @XmlElement(name = "FechaNacimiento")
    private String fechaNacimiento;

    @XmlElement(name = "CodNacionalidad")
    private Integer codNacionalidad;

    @XmlElement(name = "NombreEnCedula")
    private String nombreEnCedula;

    public ObjPersona() {
    }

    public String getCodTipoDocumento() {
        return codTipoDocumento;
    }

    public void setCodTipoDocumento(String codTipoDocumento) {
        this.codTipoDocumento = codTipoDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getApellidoAdoptivo1() {
        return apellidoAdoptivo1;
    }

    public void setApellidoAdoptivo1(String apellidoAdoptivo1) {
        this.apellidoAdoptivo1 = apellidoAdoptivo1;
    }

    public String getApellidoAdoptivo2() {
        return apellidoAdoptivo2;
    }

    public void setApellidoAdoptivo2(String apellidoAdoptivo2) {
        this.apellidoAdoptivo2 = apellidoAdoptivo2;
    }

    public Integer getSexo() {
        return sexo;
    }

    public void setSexo(Integer sexo) {
        this.sexo = sexo;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getCodNacionalidad() {
        return codNacionalidad;
    }

    public void setCodNacionalidad(Integer codNacionalidad) {
        this.codNacionalidad = codNacionalidad;
    }

    public String getNombreEnCedula() {
        return nombreEnCedula;
    }

    public void setNombreEnCedula(String nombreEnCedula) {
        this.nombreEnCedula = nombreEnCedula;
    }
}

