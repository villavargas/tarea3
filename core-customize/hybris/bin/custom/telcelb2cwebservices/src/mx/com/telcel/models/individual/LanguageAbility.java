package mx.com.telcel.models.individual;

public class LanguageAbility
{

	private Boolean isFavouriteLanguage;
	private String languageCode;
	private String languageName;
	private String listeningProficiency;
	private String readingProficiency;
	private String speakingProficiency;
	private String writingProficiency;
	private TimePeriod validFor;

	/**
	 * @return the isFavouriteLanguage
	 */
	public Boolean getIsFavouriteLanguage()
	{
		return isFavouriteLanguage;
	}

	/**
	 * @param isFavouriteLanguage
	 *           the isFavouriteLanguage to set
	 */
	public void setIsFavouriteLanguage(final Boolean isFavouriteLanguage)
	{
		this.isFavouriteLanguage = isFavouriteLanguage;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode()
	{
		return languageCode;
	}

	/**
	 * @param languageCode
	 *           the languageCode to set
	 */
	public void setLanguageCode(final String languageCode)
	{
		this.languageCode = languageCode;
	}

	/**
	 * @return the languageName
	 */
	public String getLanguageName()
	{
		return languageName;
	}

	/**
	 * @param languageName
	 *           the languageName to set
	 */
	public void setLanguageName(final String languageName)
	{
		this.languageName = languageName;
	}

	/**
	 * @return the listeningProficiency
	 */
	public String getListeningProficiency()
	{
		return listeningProficiency;
	}

	/**
	 * @param listeningProficiency
	 *           the listeningProficiency to set
	 */
	public void setListeningProficiency(final String listeningProficiency)
	{
		this.listeningProficiency = listeningProficiency;
	}

	/**
	 * @return the readingProficiency
	 */
	public String getReadingProficiency()
	{
		return readingProficiency;
	}

	/**
	 * @param readingProficiency
	 *           the readingProficiency to set
	 */
	public void setReadingProficiency(final String readingProficiency)
	{
		this.readingProficiency = readingProficiency;
	}

	/**
	 * @return the speakingProficiency
	 */
	public String getSpeakingProficiency()
	{
		return speakingProficiency;
	}

	/**
	 * @param speakingProficiency
	 *           the speakingProficiency to set
	 */
	public void setSpeakingProficiency(final String speakingProficiency)
	{
		this.speakingProficiency = speakingProficiency;
	}

	/**
	 * @return the writingProficiency
	 */
	public String getWritingProficiency()
	{
		return writingProficiency;
	}

	/**
	 * @param writingProficiency
	 *           the writingProficiency to set
	 */
	public void setWritingProficiency(final String writingProficiency)
	{
		this.writingProficiency = writingProficiency;
	}

	/**
	 * @return the validFor
	 */
	public TimePeriod getValidFor()
	{
		return validFor;
	}

	/**
	 * @param validFor
	 *           the validFor to set
	 */
	public void setValidFor(final TimePeriod validFor)
	{
		this.validFor = validFor;
	}

}
