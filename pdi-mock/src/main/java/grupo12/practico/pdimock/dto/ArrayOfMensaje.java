package grupo12.practico.pdimock.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfMensaje", propOrder = {
    "mensaje"
})
public class ArrayOfMensaje {

    @XmlElement(name = "Mensaje", nillable = true)
    private List<Mensaje> mensaje;

    public ArrayOfMensaje() {
        this.mensaje = new ArrayList<>();
    }

    public List<Mensaje> getMensaje() {
        return mensaje;
    }

    public void setMensaje(List<Mensaje> mensaje) {
        this.mensaje = mensaje;
    }
}

