
package samples.services.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getQuote", namespace = "http://soap.services.samples/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getQuote", namespace = "http://soap.services.samples/")
public class GetQuote {

    @XmlElement(name = "request", namespace = "")
    private samples.services.soap.GQ request;

    /**
     * 
     * @return
     *     returns GQ
     */
    public samples.services.soap.GQ getRequest() {
        return this.request;
    }

    /**
     * 
     * @param request
     *     the value for the request property
     */
    public void setRequest(samples.services.soap.GQ request) {
        this.request = request;
    }

}
