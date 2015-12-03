
package samples.services.soap.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getMarketActivityResponse", namespace = "http://soap.services.samples/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMarketActivityResponse", namespace = "http://soap.services.samples/")
public class GetMarketActivityResponse {

    @XmlElement(name = "return", namespace = "")
    private samples.services.soap.GMAResponse _return;

    /**
     * 
     * @return
     *     returns GMAResponse
     */
    public samples.services.soap.GMAResponse getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(samples.services.soap.GMAResponse _return) {
        this._return = _return;
    }

}
