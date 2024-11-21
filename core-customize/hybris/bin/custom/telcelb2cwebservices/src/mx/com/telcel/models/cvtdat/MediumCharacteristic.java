package mx.com.telcel.models.cvtdat;

import java.util.List;


public class MediumCharacteristic
{

	private String email;
	private String faxNumber;
	private String mobileNumber;
	private String phoneNumber;
	private String phoneNumberExtension;
	private String type;
	private List<Characteristic> characteristic;

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}

	/**
	 * @return the faxNumber
	 */
	public String getFaxNumber()
	{
		return faxNumber;
	}

	/**
	 * @param faxNumber
	 *           the faxNumber to set
	 */
	public void setFaxNumber(final String faxNumber)
	{
		this.faxNumber = faxNumber;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber()
	{
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 *           the mobileNumber to set
	 */
	public void setMobileNumber(final String mobileNumber)
	{
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *           the phoneNumber to set
	 */
	public void setPhoneNumber(final String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the phoneNumberExtension
	 */
	public String getPhoneNumberExtension()
	{
		return phoneNumberExtension;
	}

	/**
	 * @param phoneNumberExtension
	 *           the phoneNumberExtension to set
	 */
	public void setPhoneNumberExtension(final String phoneNumberExtension)
	{
		this.phoneNumberExtension = phoneNumberExtension;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *           the type to set
	 */
	public void setType(final String type)
	{
		this.type = type;
	}

	/**
	 * @return the characteristic
	 */
	public List<Characteristic> getCharacteristic()
	{
		return characteristic;
	}

	/**
	 * @param characteristic
	 *           the characteristic to set
	 */
	public void setCharacteristic(final List<Characteristic> characteristic)
	{
		this.characteristic = characteristic;
	}

}
