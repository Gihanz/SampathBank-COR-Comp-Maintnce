//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.05.24 at 12:57:59 PM IST 
//


package biz.nable.sb.cor.comp.soap.schemas.iib;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetOperativeAccountDataRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetOperativeAccountDataRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="APPCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Controller" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CDCICode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AccountNo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="solId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CDCI043" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetOperativeAccountDataRequestType", propOrder = {
    "appCode",
    "controller",
    "cdciCode",
    "accountNo",
    "solId",
    "cdci043"
})
public class GetOperativeAccountDataRequestType {

    @XmlElement(name = "APPCode", required = true)
    protected String appCode;
    @XmlElement(name = "Controller", required = true)
    protected String controller;
    @XmlElement(name = "CDCICode", required = true)
    protected String cdciCode;
    @XmlElement(name = "AccountNo", required = true)
    protected String accountNo;
    @XmlElement(required = true)
    protected String solId;
    @XmlElement(name = "CDCI043", required = true)
    protected String cdci043;

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

    /**
     * Gets the value of the solId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolId() {
        return solId;
    }

    /**
     * Sets the value of the solId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolId(String value) {
        this.solId = value;
    }

    /**
     * Gets the value of the cdci043 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCDCI043() {
        return cdci043;
    }

    /**
     * Sets the value of the cdci043 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCDCI043(String value) {
        this.cdci043 = value;
    }

}
