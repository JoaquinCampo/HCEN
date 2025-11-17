package grupo12.practico.pdimock.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Mensaje", propOrder = {
    "codMensaje",
    "descripcion",
    "datoExtra"
})
public class Mensaje {

    @XmlElement(name = "CodMensaje")
    private Integer codMensaje;

    @XmlElement(name = "Descripcion")
    private String descripcion;

    @XmlElement(name = "DatoExtra")
    private String datoExtra;

    public Mensaje() {
    }

    public Mensaje(Integer codMensaje, String descripcion, String datoExtra) {
        this.codMensaje = codMensaje;
        this.descripcion = descripcion;
        this.datoExtra = datoExtra;
    }

    public Integer getCodMensaje() {
        return codMensaje;
    }

    public void setCodMensaje(Integer codMensaje) {
        this.codMensaje = codMensaje;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDatoExtra() {
        return datoExtra;
    }

    public void setDatoExtra(String datoExtra) {
        this.datoExtra = datoExtra;
    }
}

