package mx.com.telcel.services;

public interface CustomValidationService
{

	boolean is10DigitPhoneNumber(final String phoneNumber);

	boolean validatePasswordSSO(final String password);
	

	boolean validateIsAnEmail(final String userID);
	

	boolean validateIsANumber(final String userID);


	boolean is12DigitPhoneNumber(final String userID);
	
}
