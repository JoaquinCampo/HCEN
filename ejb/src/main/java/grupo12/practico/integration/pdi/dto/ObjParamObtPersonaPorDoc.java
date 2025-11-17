package grupo12.practico.integration.pdi.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objParamObtPersonaPorDoc", propOrder = {
    "organizacion",
    "passwordEntidad",
    "nrodocumento",
    "tipoDocumento"
})
public class ObjParamObtPersonaPorDoc extends ObjParamAutenticacion {

    @XmlElement(name = "Nrodocumento")
    private String nrodocumento;

    @XmlElement(name = "TipoDocumento")
    private String tipoDocumento;

    public ObjParamObtPersonaPorDoc() {
    }

    public String getNrodocumento() {
        return nrodocumento;
    }

    public void setNrodocumento(String nrodocumento) {
        this.nrodocumento = nrodocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}

