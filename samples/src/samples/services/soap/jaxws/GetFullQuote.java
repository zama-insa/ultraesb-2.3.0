
package samples.services.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getFullQuote", namespace = "http://soap.services.samples/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFullQuote", namespace = "http://soap.services.samples/")
public class GetFullQuote {

    @XmlElement(name = "request", namespace = "")
    private samples.services.soap.GFQ request;

    /**
     * 
     * @return
     *     returns GFQ
     */
    public samples.services.soap.GFQ getRequest() {
        return this.request;
    }

    /**
     * 
     * @param request
     *     the value for the request property
     */
    public void setRequest(samples.services.soap.GFQ request) {
        this.request = request;
    }

}
