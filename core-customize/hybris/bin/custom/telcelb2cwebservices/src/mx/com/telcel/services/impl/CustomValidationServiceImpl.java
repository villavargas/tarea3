package mx.com.telcel.services.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mx.com.telcel.services.CustomValidationService;


public class CustomValidationServiceImpl implements CustomValidationService
{

	private static final String ONLY_NUMBERS_PATTERN = "\\d+";
	private static final String EMAIL_PATTERN = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@" + "[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";

	@Override
	public boolean is10DigitPhoneNumber(final String phoneNumber)
	{
		final String regex = "^[0-9]{10}$";
		return phoneNumber.matches(regex);
	}

	@Override
	public boolean validatePasswordSSO(final String password)
	{
		/*
		 * 1.- Length 7 - 12 characters 2.- At least one letter and one number 3.- Without special characters
		 */
		final String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{7,12}$";
		return password.matches(regex);
	}
	
	@Override
	public boolean validateIsANumber(final String userID)
	{
		try
		{
			final Pattern pattern = Pattern.compile(ONLY_NUMBERS_PATTERN);
			final Matcher matcher = pattern.matcher(userID);
			if (matcher.matches())
			{
				return true;
			}
		}
		catch (final NumberFormatException nfe)
		{
		}
		return false;
	}

	@Override
	public boolean validateIsAnEmail(final String userID)
	{
		try
		{
			final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			final Matcher matcher = pattern.matcher(userID);
			if (matcher.matches())
			{
				return true;
			}
		}
		catch (final NumberFormatException nfe)
		{
		}
		return false;
	}

	@Override
		public boolean is12DigitPhoneNumber(final String userID)
	{
		final String regex = "^[0-9]{12}$";
		return userID.matches(regex);

	}

}
