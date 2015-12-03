
package samples.services.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "placeOrder", namespace = "http://soap.services.samples/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "placeOrder", namespace = "http://soap.services.samples/")
public class PlaceOrder {

    @XmlElement(name = "request", namespace = "")
    private samples.services.soap.PO request;

    /**
     * 
     * @return
     *     returns PO
     */
    public samples.services.soap.PO getRequest() {
        return this.request;
    }

    /**
     * 
     * @param request
     *     the value for the request property
     */
    public void setRequest(samples.services.soap.PO request) {
        this.request = request;
    }

}
