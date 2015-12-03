
package samples.services.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getSimpleQuote", namespace = "http://soap.services.samples/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSimpleQuote", namespace = "http://soap.services.samples/")
public class GetSimpleQuote {

    @XmlElement(name = "request", namespace = "")
    private String request;

    /**
     * 
     * @return
     *     returns String
     */
    public String getRequest() {
        return this.request;
    }

    /**
     * 
     * @param request
     *     the value for the request property
     */
    public void setRequest(String request) {
        this.request = request;
    }

}
