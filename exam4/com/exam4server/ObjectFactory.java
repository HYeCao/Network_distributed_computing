
package com.exam4server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.exam4server package. 
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

    private final static QName _ParseException_QNAME = new QName("http://www.exam4server.com", "ParseException");
    private final static QName _QuerylistResponse_QNAME = new QName("http://www.exam4server.com", "querylistResponse");
    private final static QName _Register_QNAME = new QName("http://www.exam4server.com", "register");
    private final static QName _Querylist_QNAME = new QName("http://www.exam4server.com", "querylist");
    private final static QName _ClearlistResponse_QNAME = new QName("http://www.exam4server.com", "clearlistResponse");
    private final static QName _Deletelist_QNAME = new QName("http://www.exam4server.com", "deletelist");
    private final static QName _Isright_QNAME = new QName("http://www.exam4server.com", "isright");
    private final static QName _IsrightResponse_QNAME = new QName("http://www.exam4server.com", "isrightResponse");
    private final static QName _DeletelistResponse_QNAME = new QName("http://www.exam4server.com", "deletelistResponse");
    private final static QName _RegisterResponse_QNAME = new QName("http://www.exam4server.com", "registerResponse");
    private final static QName _Addlist_QNAME = new QName("http://www.exam4server.com", "addlist");
    private final static QName _Clearlist_QNAME = new QName("http://www.exam4server.com", "clearlist");
    private final static QName _AddlistResponse_QNAME = new QName("http://www.exam4server.com", "addlistResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.exam4server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Clearlist }
     * 
     */
    public Clearlist createClearlist() {
        return new Clearlist();
    }

    /**
     * Create an instance of {@link AddlistResponse }
     * 
     */
    public AddlistResponse createAddlistResponse() {
        return new AddlistResponse();
    }

    /**
     * Create an instance of {@link Addlist }
     * 
     */
    public Addlist createAddlist() {
        return new Addlist();
    }

    /**
     * Create an instance of {@link IsrightResponse }
     * 
     */
    public IsrightResponse createIsrightResponse() {
        return new IsrightResponse();
    }

    /**
     * Create an instance of {@link Deletelist }
     * 
     */
    public Deletelist createDeletelist() {
        return new Deletelist();
    }

    /**
     * Create an instance of {@link Isright }
     * 
     */
    public Isright createIsright() {
        return new Isright();
    }

    /**
     * Create an instance of {@link ClearlistResponse }
     * 
     */
    public ClearlistResponse createClearlistResponse() {
        return new ClearlistResponse();
    }

    /**
     * Create an instance of {@link RegisterResponse }
     * 
     */
    public RegisterResponse createRegisterResponse() {
        return new RegisterResponse();
    }

    /**
     * Create an instance of {@link DeletelistResponse }
     * 
     */
    public DeletelistResponse createDeletelistResponse() {
        return new DeletelistResponse();
    }

    /**
     * Create an instance of {@link ParseException }
     * 
     */
    public ParseException createParseException() {
        return new ParseException();
    }

    /**
     * Create an instance of {@link Querylist }
     * 
     */
    public Querylist createQuerylist() {
        return new Querylist();
    }

    /**
     * Create an instance of {@link QuerylistResponse }
     * 
     */
    public QuerylistResponse createQuerylistResponse() {
        return new QuerylistResponse();
    }

    /**
     * Create an instance of {@link Register }
     * 
     */
    public Register createRegister() {
        return new Register();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "ParseException")
    public JAXBElement<ParseException> createParseException(ParseException value) {
        return new JAXBElement<ParseException>(_ParseException_QNAME, ParseException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QuerylistResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "querylistResponse")
    public JAXBElement<QuerylistResponse> createQuerylistResponse(QuerylistResponse value) {
        return new JAXBElement<QuerylistResponse>(_QuerylistResponse_QNAME, QuerylistResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Register }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "register")
    public JAXBElement<Register> createRegister(Register value) {
        return new JAXBElement<Register>(_Register_QNAME, Register.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Querylist }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "querylist")
    public JAXBElement<Querylist> createQuerylist(Querylist value) {
        return new JAXBElement<Querylist>(_Querylist_QNAME, Querylist.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClearlistResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "clearlistResponse")
    public JAXBElement<ClearlistResponse> createClearlistResponse(ClearlistResponse value) {
        return new JAXBElement<ClearlistResponse>(_ClearlistResponse_QNAME, ClearlistResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Deletelist }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "deletelist")
    public JAXBElement<Deletelist> createDeletelist(Deletelist value) {
        return new JAXBElement<Deletelist>(_Deletelist_QNAME, Deletelist.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Isright }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "isright")
    public JAXBElement<Isright> createIsright(Isright value) {
        return new JAXBElement<Isright>(_Isright_QNAME, Isright.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsrightResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "isrightResponse")
    public JAXBElement<IsrightResponse> createIsrightResponse(IsrightResponse value) {
        return new JAXBElement<IsrightResponse>(_IsrightResponse_QNAME, IsrightResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletelistResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "deletelistResponse")
    public JAXBElement<DeletelistResponse> createDeletelistResponse(DeletelistResponse value) {
        return new JAXBElement<DeletelistResponse>(_DeletelistResponse_QNAME, DeletelistResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "registerResponse")
    public JAXBElement<RegisterResponse> createRegisterResponse(RegisterResponse value) {
        return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME, RegisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Addlist }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "addlist")
    public JAXBElement<Addlist> createAddlist(Addlist value) {
        return new JAXBElement<Addlist>(_Addlist_QNAME, Addlist.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Clearlist }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "clearlist")
    public JAXBElement<Clearlist> createClearlist(Clearlist value) {
        return new JAXBElement<Clearlist>(_Clearlist_QNAME, Clearlist.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddlistResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.exam4server.com", name = "addlistResponse")
    public JAXBElement<AddlistResponse> createAddlistResponse(AddlistResponse value) {
        return new JAXBElement<AddlistResponse>(_AddlistResponse_QNAME, AddlistResponse.class, null, value);
    }

}
