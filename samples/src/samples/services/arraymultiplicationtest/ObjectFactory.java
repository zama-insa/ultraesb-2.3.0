
package samples.services.arraymultiplicationtest;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the samples.services.arraymultiplicationtest package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArrayMultiplicationResponse_QNAME = new QName("http://multiplicationstresstest.services.samples/", "arrayMultiplicationResponse");
    private final static QName _ArrayMultiplication_QNAME = new QName("http://multiplicationstresstest.services.samples/", "arrayMultiplication");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: samples.services.arraymultiplicationtest
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArrayMultiplication_Type }
     * 
     */
    public ArrayMultiplication_Type createArrayMultiplication_Type() {
        return new ArrayMultiplication_Type();
    }

    /**
     * Create an instance of {@link ArrayMultiplicationResponse }
     * 
     */
    public ArrayMultiplicationResponse createArrayMultiplicationResponse() {
        return new ArrayMultiplicationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayMultiplicationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://multiplicationstresstest.services.samples/", name = "arrayMultiplicationResponse")
    public JAXBElement<ArrayMultiplicationResponse> createArrayMultiplicationResponse(ArrayMultiplicationResponse value) {
        return new JAXBElement<ArrayMultiplicationResponse>(_ArrayMultiplicationResponse_QNAME, ArrayMultiplicationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayMultiplication_Type }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://multiplicationstresstest.services.samples/", name = "arrayMultiplication")
    public JAXBElement<ArrayMultiplication_Type> createArrayMultiplication(ArrayMultiplication_Type value) {
        return new JAXBElement<ArrayMultiplication_Type>(_ArrayMultiplication_QNAME, ArrayMultiplication_Type.class, null, value);
    }

}
