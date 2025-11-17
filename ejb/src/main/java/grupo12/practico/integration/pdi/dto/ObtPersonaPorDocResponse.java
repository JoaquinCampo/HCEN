package grupo12.practico.integration.pdi.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObtPersonaPorDocResponse", namespace = "http://wsDNIC/", propOrder = {
    "obtPersonaPorDocResult"
})
public class ObtPersonaPorDocResponse {

    @XmlElement(name = "ObtPersonaPorDocResult", namespace = "http://wsDNIC/")
    private ObtPersonaPorDocResult obtPersonaPorDocResult;

    public ObtPersonaPorDocResponse() {
    }

    public ObtPersonaPorDocResult getObtPersonaPorDocResult() {
        return obtPersonaPorDocResult;
    }

    public void setObtPersonaPorDocResult(ObtPersonaPorDocResult obtPersonaPorDocResult) {
        this.obtPersonaPorDocResult = obtPersonaPorDocResult;
    }
}

