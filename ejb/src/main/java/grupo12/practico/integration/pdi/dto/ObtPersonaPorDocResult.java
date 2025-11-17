package grupo12.practico.integration.pdi.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtPersonaPorDocResult", propOrder = {
    "errores",
    "warnings",
    "objPersona"
})
public class ObtPersonaPorDocResult {

    @XmlElement(name = "Errores")
    private ArrayOfMensaje errores;

    @XmlElement(name = "Warnings")
    private ArrayOfMensaje warnings;

    @XmlElement(name = "ObjPersona")
    private ObjPersona objPersona;

    public ObtPersonaPorDocResult() {
        this.errores = new ArrayOfMensaje();
        this.warnings = new ArrayOfMensaje();
    }

    public ArrayOfMensaje getErrores() {
        return errores;
    }

    public void setErrores(ArrayOfMensaje errores) {
        this.errores = errores;
    }

    public ArrayOfMensaje getWarnings() {
        return warnings;
    }

    public void setWarnings(ArrayOfMensaje warnings) {
        this.warnings = warnings;
    }

    public ObjPersona getObjPersona() {
        return objPersona;
    }

    public void setObjPersona(ObjPersona objPersona) {
        this.objPersona = objPersona;
    }
}

