
package samples.services.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getFullQuoteResponse", namespace = "http://soap.services.samples/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFullQuoteResponse", namespace = "http://soap.services.samples/")
public class GetFullQuoteResponse {

    @XmlElement(name = "return", namespace = "")
    private samples.services.soap.GFQResponse _return;

    /**
     * 
     * @return
     *     returns GFQResponse
     */
    public samples.services.soap.GFQResponse getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(samples.services.soap.GFQResponse _return) {
        this._return = _return;
    }

}
