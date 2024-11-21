
package com.amx.telcel.di.sds.esb.logistica.fedex.rate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Data for a shipment's total/summary rates, as calculated
 * 				per a specific rate type. The "total..." fields may
 * 				differ from the sum of corresponding package data for
 * 				Multiweight or Express MPS.
 * 
 * <p>Java class for ShipmentRateDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipmentRateDetail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RateType" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}ReturnedRateType" minOccurs="0"/&gt;
 *         &lt;element name="RateScale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RateZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PricingCode" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}PricingCodeType" minOccurs="0"/&gt;
 *         &lt;element name="RatedWeightMethod" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}RatedWeightMethod" minOccurs="0"/&gt;
 *         &lt;element name="CurrencyExchangeRate" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}CurrencyExchangeRate" minOccurs="0"/&gt;
 *         &lt;element name="DimDivisor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="FuelSurchargePercent" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="TotalBillingWeight" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Weight" minOccurs="0"/&gt;
 *         &lt;element name="TotalBaseCharge" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalFreightDiscounts" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalNetFreight" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalSurcharges" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalNetFedExCharge" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalTaxes" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalNetCharge" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalRebates" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalDutiesAndTaxes" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="TotalNetChargeWithDutiesAndTaxes" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Money" minOccurs="0"/&gt;
 *         &lt;element name="Surcharges" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Surcharge" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Taxes" type="{http://www.amx.com/telcel/di/sds/esb/logistica/fedex/rate}Tax" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentRateDetail", propOrder = {
    "rateType",
    "rateScale",
    "rateZone",
    "pricingCode",
    "ratedWeightMethod",
    "currencyExchangeRate",
    "dimDivisor",
    "fuelSurchargePercent",
    "totalBillingWeight",
    "totalBaseCharge",
    "totalFreightDiscounts",
    "totalNetFreight",
    "totalSurcharges",
    "totalNetFedExCharge",
    "totalTaxes",
    "totalNetCharge",
    "totalRebates",
    "totalDutiesAndTaxes",
    "totalNetChargeWithDutiesAndTaxes",
    "surcharges",
    "taxes"
})
public class ShipmentRateDetail {

    @XmlElement(name = "RateType")
    @XmlSchemaType(name = "string")
    protected ReturnedRateType rateType;
    @XmlElement(name = "RateScale")
    protected String rateScale;
    @XmlElement(name = "RateZone")
    protected String rateZone;
    @XmlElement(name = "PricingCode")
    @XmlSchemaType(name = "string")
    protected PricingCodeType pricingCode;
    @XmlElement(name = "RatedWeightMethod")
    @XmlSchemaType(name = "string")
    protected RatedWeightMethod ratedWeightMethod;
    @XmlElement(name = "CurrencyExchangeRate")
    protected CurrencyExchangeRate currencyExchangeRate;
    @XmlElement(name = "DimDivisor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger dimDivisor;
    @XmlElement(name = "FuelSurchargePercent")
    protected BigDecimal fuelSurchargePercent;
    @XmlElement(name = "TotalBillingWeight")
    protected Weight totalBillingWeight;
    @XmlElement(name = "TotalBaseCharge")
    protected Money totalBaseCharge;
    @XmlElement(name = "TotalFreightDiscounts")
    protected Money totalFreightDiscounts;
    @XmlElement(name = "TotalNetFreight")
    protected Money totalNetFreight;
    @XmlElement(name = "TotalSurcharges")
    protected Money totalSurcharges;
    @XmlElement(name = "TotalNetFedExCharge")
    protected Money totalNetFedExCharge;
    @XmlElement(name = "TotalTaxes")
    protected Money totalTaxes;
    @XmlElement(name = "TotalNetCharge")
    protected Money totalNetCharge;
    @XmlElement(name = "TotalRebates")
    protected Money totalRebates;
    @XmlElement(name = "TotalDutiesAndTaxes")
    protected Money totalDutiesAndTaxes;
    @XmlElement(name = "TotalNetChargeWithDutiesAndTaxes")
    protected Money totalNetChargeWithDutiesAndTaxes;
    @XmlElement(name = "Surcharges")
    protected List<Surcharge> surcharges;
    @XmlElement(name = "Taxes")
    protected List<Tax> taxes;

    /**
     * Gets the value of the rateType property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnedRateType }
     *     
     */
    public ReturnedRateType getRateType() {
        return rateType;
    }

    /**
     * Sets the value of the rateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnedRateType }
     *     
     */
    public void setRateType(ReturnedRateType value) {
        this.rateType = value;
    }

    /**
     * Gets the value of the rateScale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateScale() {
        return rateScale;
    }

    /**
     * Sets the value of the rateScale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateScale(String value) {
        this.rateScale = value;
    }

    /**
     * Gets the value of the rateZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateZone() {
        return rateZone;
    }

    /**
     * Sets the value of the rateZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateZone(String value) {
        this.rateZone = value;
    }

    /**
     * Gets the value of the pricingCode property.
     * 
     * @return
     *     possible object is
     *     {@link PricingCodeType }
     *     
     */
    public PricingCodeType getPricingCode() {
        return pricingCode;
    }

    /**
     * Sets the value of the pricingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PricingCodeType }
     *     
     */
    public void setPricingCode(PricingCodeType value) {
        this.pricingCode = value;
    }

    /**
     * Gets the value of the ratedWeightMethod property.
     * 
     * @return
     *     possible object is
     *     {@link RatedWeightMethod }
     *     
     */
    public RatedWeightMethod getRatedWeightMethod() {
        return ratedWeightMethod;
    }

    /**
     * Sets the value of the ratedWeightMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link RatedWeightMethod }
     *     
     */
    public void setRatedWeightMethod(RatedWeightMethod value) {
        this.ratedWeightMethod = value;
    }

    /**
     * Gets the value of the currencyExchangeRate property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyExchangeRate }
     *     
     */
    public CurrencyExchangeRate getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }

    /**
     * Sets the value of the currencyExchangeRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyExchangeRate }
     *     
     */
    public void setCurrencyExchangeRate(CurrencyExchangeRate value) {
        this.currencyExchangeRate = value;
    }

    /**
     * Gets the value of the dimDivisor property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDimDivisor() {
        return dimDivisor;
    }

    /**
     * Sets the value of the dimDivisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDimDivisor(BigInteger value) {
        this.dimDivisor = value;
    }

    /**
     * Gets the value of the fuelSurchargePercent property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFuelSurchargePercent() {
        return fuelSurchargePercent;
    }

    /**
     * Sets the value of the fuelSurchargePercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFuelSurchargePercent(BigDecimal value) {
        this.fuelSurchargePercent = value;
    }

    /**
     * Gets the value of the totalBillingWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getTotalBillingWeight() {
        return totalBillingWeight;
    }

    /**
     * Sets the value of the totalBillingWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setTotalBillingWeight(Weight value) {
        this.totalBillingWeight = value;
    }

    /**
     * Gets the value of the totalBaseCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalBaseCharge() {
        return totalBaseCharge;
    }

    /**
     * Sets the value of the totalBaseCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalBaseCharge(Money value) {
        this.totalBaseCharge = value;
    }

    /**
     * Gets the value of the totalFreightDiscounts property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalFreightDiscounts() {
        return totalFreightDiscounts;
    }

    /**
     * Sets the value of the totalFreightDiscounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalFreightDiscounts(Money value) {
        this.totalFreightDiscounts = value;
    }

    /**
     * Gets the value of the totalNetFreight property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetFreight() {
        return totalNetFreight;
    }

    /**
     * Sets the value of the totalNetFreight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetFreight(Money value) {
        this.totalNetFreight = value;
    }

    /**
     * Gets the value of the totalSurcharges property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalSurcharges() {
        return totalSurcharges;
    }

    /**
     * Sets the value of the totalSurcharges property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalSurcharges(Money value) {
        this.totalSurcharges = value;
    }

    /**
     * Gets the value of the totalNetFedExCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetFedExCharge() {
        return totalNetFedExCharge;
    }

    /**
     * Sets the value of the totalNetFedExCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetFedExCharge(Money value) {
        this.totalNetFedExCharge = value;
    }

    /**
     * Gets the value of the totalTaxes property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalTaxes() {
        return totalTaxes;
    }

    /**
     * Sets the value of the totalTaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalTaxes(Money value) {
        this.totalTaxes = value;
    }

    /**
     * Gets the value of the totalNetCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetCharge() {
        return totalNetCharge;
    }

    /**
     * Sets the value of the totalNetCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetCharge(Money value) {
        this.totalNetCharge = value;
    }

    /**
     * Gets the value of the totalRebates property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalRebates() {
        return totalRebates;
    }

    /**
     * Sets the value of the totalRebates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalRebates(Money value) {
        this.totalRebates = value;
    }

    /**
     * Gets the value of the totalDutiesAndTaxes property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalDutiesAndTaxes() {
        return totalDutiesAndTaxes;
    }

    /**
     * Sets the value of the totalDutiesAndTaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalDutiesAndTaxes(Money value) {
        this.totalDutiesAndTaxes = value;
    }

    /**
     * Gets the value of the totalNetChargeWithDutiesAndTaxes property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetChargeWithDutiesAndTaxes() {
        return totalNetChargeWithDutiesAndTaxes;
    }

    /**
     * Sets the value of the totalNetChargeWithDutiesAndTaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetChargeWithDutiesAndTaxes(Money value) {
        this.totalNetChargeWithDutiesAndTaxes = value;
    }

    /**
     * Gets the value of the surcharges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the surcharges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSurcharges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Surcharge }
     * 
     * 
     */
    public List<Surcharge> getSurcharges() {
        if (surcharges == null) {
            surcharges = new ArrayList<Surcharge>();
        }
        return this.surcharges;
    }

    /**
     * Gets the value of the taxes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taxes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaxes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tax }
     * 
     * 
     */
    public List<Tax> getTaxes() {
        if (taxes == null) {
            taxes = new ArrayList<Tax>();
        }
        return this.taxes;
    }

}
