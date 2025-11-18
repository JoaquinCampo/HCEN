package grupo12.practico.pdimock.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObtPersonaPorDocRequest", namespace = "http://wsDNIC/", propOrder = {
    "paramObtPersonaPorDoc"
})
public class ObtPersonaPorDocRequest {

    @XmlElement(name = "paramObtPersonaPorDoc", namespace = "http://wsDNIC/")
    private ObjParamObtPersonaPorDoc paramObtPersonaPorDoc;

    public ObtPersonaPorDocRequest() {
    }

    public ObjParamObtPersonaPorDoc getParamObtPersonaPorDoc() {
        return paramObtPersonaPorDoc;
    }

    public void setParamObtPersonaPorDoc(ObjParamObtPersonaPorDoc paramObtPersonaPorDoc) {
        this.paramObtPersonaPorDoc = paramObtPersonaPorDoc;
    }
}

