package grupo12.practico.pdimock.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objParamAutenticacion", propOrder = {
    "organizacion",
    "passwordEntidad"
})
public class ObjParamAutenticacion {

    @XmlElement(name = "Organizacion")
    private String organizacion;

    @XmlElement(name = "PasswordEntidad")
    private String passwordEntidad;

    public ObjParamAutenticacion() {
    }

    public String getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(String organizacion) {
        this.organizacion = organizacion;
    }

    public String getPasswordEntidad() {
        return passwordEntidad;
    }

    public void setPasswordEntidad(String passwordEntidad) {
        this.passwordEntidad = passwordEntidad;
    }
}

