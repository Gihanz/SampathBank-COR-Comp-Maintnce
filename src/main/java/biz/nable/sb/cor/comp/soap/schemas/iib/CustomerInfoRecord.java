//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.26 at 02:41:12 PM IST 
//


package biz.nable.sb.cor.comp.soap.schemas.iib;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerInfoRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerInfoRecord"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AcctNo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AcctType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AvailableBalance" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="JointSerial" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="JointRecType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerInfoRecord", propOrder = {
    "acctNo",
    "acctType",
    "availableBalance",
    "currencyCode",
    "jointSerial",
    "jointRecType"
})
public class CustomerInfoRecord {

    @XmlElement(name = "AcctNo", required = true)
    protected String acctNo;
    @XmlElement(name = "AcctType", required = true)
    protected String acctType;
    @XmlElement(name = "AvailableBalance")
    protected double availableBalance;
    @XmlElement(name = "CurrencyCode", required = true)
    protected String currencyCode;
    @XmlElement(name = "JointSerial", required = true)
    protected String jointSerial;
    @XmlElement(name = "JointRecType", required = true)
    protected String jointRecType;

    /**
     * Gets the value of the acctNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcctNo() {
        return acctNo;
    }

    /**
     * Sets the value of the acctNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcctNo(String value) {
        this.acctNo = value;
    }

    /**
     * Gets the value of the acctType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcctType() {
        return acctType;
    }

    /**
     * Sets the value of the acctType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcctType(String value) {
        this.acctType = value;
    }

    /**
     * Gets the value of the availableBalance property.
     * 
     */
    public double getAvailableBalance() {
        return availableBalance;
    }

    /**
     * Sets the value of the availableBalance property.
     * 
     */
    public void setAvailableBalance(double value) {
        this.availableBalance = value;
    }

    /**
     * Gets the value of the currencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyCode(String value) {
        this.currencyCode = value;
    }

    /**
     * Gets the value of the jointSerial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJointSerial() {
        return jointSerial;
    }

    /**
     * Sets the value of the jointSerial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJointSerial(String value) {
        this.jointSerial = value;
    }

    /**
     * Gets the value of the jointRecType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJointRecType() {
        return jointRecType;
    }

    /**
     * Sets the value of the jointRecType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJointRecType(String value) {
        this.jointRecType = value;
    }

}
