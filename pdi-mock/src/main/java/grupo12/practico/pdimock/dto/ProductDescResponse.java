package grupo12.practico.pdimock.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductDescResponseType", namespace = "http://wsDNIC/", propOrder = {
    "productDescResult"
})
public class ProductDescResponse {

    @XmlElement(name = "ProductDescResult", namespace = "http://wsDNIC/")
    private ObtProductInfo productDescResult;

    public ProductDescResponse() {
    }

    public ObtProductInfo getProductDescResult() {
        return productDescResult;
    }

    public void setProductDescResult(ObtProductInfo productDescResult) {
        this.productDescResult = productDescResult;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "obtProductInfo", propOrder = {
        "modalidad",
        "version",
        "descripcion"
    })
    public static class ObtProductInfo {

        @XmlElement(name = "modalidad")
        private String modalidad;

        @XmlElement(name = "version")
        private String version;

        @XmlElement(name = "descripcion")
        private String descripcion;

        public ObtProductInfo() {
        }

        public String getModalidad() {
            return modalidad;
        }

        public void setModalidad(String modalidad) {
            this.modalidad = modalidad;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }
}

