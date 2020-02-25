//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.26 at 02:19:29 AM IST 
//


package biz.nable.sb.cor.comp.soap.schemas.iib;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StopChequeRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StopChequeRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="APPCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Controller" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CDCICode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AccountNo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ChequeNo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="NoCheques" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ReasonCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StopChequeRequestType", propOrder = {
    "appCode",
    "controller",
    "cdciCode",
    "accountNo",
    "chequeNo",
    "noCheques",
    "reasonCode"
})
public class StopChequeRequestType {

    @XmlElement(name = "APPCode", required = true)
    protected String appCode;
    @XmlElement(name = "Controller", required = true)
    protected String controller;
    @XmlElement(name = "CDCICode", required = true)
    protected String cdciCode;
    @XmlElement(name = "AccountNo", required = true)
    protected String accountNo;
    @XmlElement(name = "ChequeNo", required = true)
    protected String chequeNo;
    @XmlElement(name = "NoCheques", required = true)
    protected String noCheques;
    @XmlElement(name = "ReasonCode", required = true)
    protected String reasonCode;

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
     * Gets the value of the chequeNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChequeNo() {
        return chequeNo;
    }

    /**
     * Sets the value of the chequeNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChequeNo(String value) {
        this.chequeNo = value;
    }

    /**
     * Gets the value of the noCheques property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoCheques() {
        return noCheques;
    }

    /**
     * Sets the value of the noCheques property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoCheques(String value) {
        this.noCheques = value;
    }

    /**
     * Gets the value of the reasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * Sets the value of the reasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonCode(String value) {
        this.reasonCode = value;
    }

}
