package mx.com.telcel.models.fedex;

import java.util.List;


public class Rate
{

	private String rateType;
	private String rateScale;
	private String rateZone;
	private String pricingCode;
	private String ratedWeightMethod;
	private Currency currencyExchange;
	private List<Characteristic> characteristics;
	private List<Amount> charges;
	private List<Amount> discounts;
	private Quantity weight;
	private List<Amount> surcharges;
	private List<Amount> taxes;

	/**
	 * @return the rateType
	 */
	public String getRateType()
	{
		return rateType;
	}

	/**
	 * @param rateType
	 *           the rateType to set
	 */
	public void setRateType(final String rateType)
	{
		this.rateType = rateType;
	}

	/**
	 * @return the rateScale
	 */
	public String getRateScale()
	{
		return rateScale;
	}

	/**
	 * @param rateScale
	 *           the rateScale to set
	 */
	public void setRateScale(final String rateScale)
	{
		this.rateScale = rateScale;
	}

	/**
	 * @return the rateZone
	 */
	public String getRateZone()
	{
		return rateZone;
	}

	/**
	 * @param rateZone
	 *           the rateZone to set
	 */
	public void setRateZone(final String rateZone)
	{
		this.rateZone = rateZone;
	}

	/**
	 * @return the pricingCode
	 */
	public String getPricingCode()
	{
		return pricingCode;
	}

	/**
	 * @param pricingCode
	 *           the pricingCode to set
	 */
	public void setPricingCode(final String pricingCode)
	{
		this.pricingCode = pricingCode;
	}

	/**
	 * @return the ratedWeightMethod
	 */
	public String getRatedWeightMethod()
	{
		return ratedWeightMethod;
	}

	/**
	 * @param ratedWeightMethod
	 *           the ratedWeightMethod to set
	 */
	public void setRatedWeightMethod(final String ratedWeightMethod)
	{
		this.ratedWeightMethod = ratedWeightMethod;
	}

	/**
	 * @return the currencyExchange
	 */
	public Currency getCurrencyExchange()
	{
		return currencyExchange;
	}

	/**
	 * @param currencyExchange
	 *           the currencyExchange to set
	 */
	public void setCurrencyExchange(final Currency currencyExchange)
	{
		this.currencyExchange = currencyExchange;
	}

	/**
	 * @return the characteristics
	 */
	public List<Characteristic> getCharacteristics()
	{
		return characteristics;
	}

	/**
	 * @param characteristics
	 *           the characteristics to set
	 */
	public void setCharacteristics(final List<Characteristic> characteristics)
	{
		this.characteristics = characteristics;
	}

	/**
	 * @return the charges
	 */
	public List<Amount> getCharges()
	{
		return charges;
	}

	/**
	 * @param charges
	 *           the charges to set
	 */
	public void setCharges(final List<Amount> charges)
	{
		this.charges = charges;
	}

	/**
	 * @return the discounts
	 */
	public List<Amount> getDiscounts()
	{
		return discounts;
	}

	/**
	 * @param discounts
	 *           the discounts to set
	 */
	public void setDiscounts(final List<Amount> discounts)
	{
		this.discounts = discounts;
	}

	/**
	 * @return the weight
	 */
	public Quantity getWeight()
	{
		return weight;
	}

	/**
	 * @param weight
	 *           the weight to set
	 */
	public void setWeight(final Quantity weight)
	{
		this.weight = weight;
	}

	/**
	 * @return the surcharges
	 */
	public List<Amount> getSurcharges()
	{
		return surcharges;
	}

	/**
	 * @param surcharges
	 *           the surcharges to set
	 */
	public void setSurcharges(final List<Amount> surcharges)
	{
		this.surcharges = surcharges;
	}

	/**
	 * @return the taxes
	 */
	public List<Amount> getTaxes()
	{
		return taxes;
	}

	/**
	 * @param taxes
	 *           the taxes to set
	 */
	public void setTaxes(final List<Amount> taxes)
	{
		this.taxes = taxes;
	}

}
