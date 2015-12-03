
package samples.services.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getQuoteResponse", namespace = "http://soap.services.samples/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getQuoteResponse", namespace = "http://soap.services.samples/")
public class GetQuoteResponse {

    @XmlElement(name = "return", namespace = "")
    private samples.services.soap.GQResponse _return;

    /**
     * 
     * @return
     *     returns GQResponse
     */
    public samples.services.soap.GQResponse getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(samples.services.soap.GQResponse _return) {
        this._return = _return;
    }

}
