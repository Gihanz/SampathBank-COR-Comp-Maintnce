package biz.nable.sb.cor.comp.soap.schemas.iib;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetLoanInquiryRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetLoanInquiryRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="APPCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Controller" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CDCICode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AccountNo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetLoanInquiryRequestType", propOrder = {
    "appCode",
    "controller",
    "cdciCode",
    "accountNo"
})
public class GetLoanInquiryRequestType {

    @XmlElement(name = "APPCode", required = true)
    protected String appCode;
    @XmlElement(name = "Controller", required = true)
    protected String controller;
    @XmlElement(name = "CDCICode", required = true)
    protected String cdciCode;
    @XmlElement(name = "AccountNo", required = true)
    protected String accountNo;

    /**
     * Gets the value of the appCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAPPCode() {
        return appCode;
    }

    /**
     * Sets the value of the appCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAPPCode(String value) {
        this.appCode = value;
    }

    /**
     * Gets the value of the controller property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getController() {
        return controller;
    }

    /**
     * Sets the value of the controller property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setController(String value) {
        this.controller = value;
    }

    /**
     * Gets the value of the cdciCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCDCICode() {
        return cdciCode;
    }

    /**
     * Sets the value of the cdciCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCDCICode(String value) {
        this.cdciCode = value;
    }

    /**
     * Gets the value of the accountNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * Sets the value of the accountNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNo(String value) {
        this.accountNo = value;
    }

}
