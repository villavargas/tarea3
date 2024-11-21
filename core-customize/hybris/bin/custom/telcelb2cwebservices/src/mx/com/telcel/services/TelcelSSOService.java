package mx.com.telcel.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import mx.com.telcel.constants.AuthorizationType;
import mx.com.telcel.models.Authorize;
import mx.com.telcel.models.Token;
import mx.com.telcel.request.models.ClientRequest;
import mx.com.telcel.request.models.UserRequest;
import mx.com.telcel.response.models.AuthorizationResponse;
import mx.com.telcel.response.models.AuthorizeErrorResponse;
import mx.com.telcel.response.models.ClientResponse;
import mx.com.telcel.response.models.ErrorResponse;
import mx.com.telcel.response.models.MessageResponse;
import mx.com.telcel.response.models.RevokeResponse;
import mx.com.telcel.response.models.TokenNipResponse;
import mx.com.telcel.response.models.TokenResponse;
import mx.com.telcel.response.models.UserInfoResponse;
import mx.com.telcel.response.models.UserResponse;



public interface TelcelSSOService
{

	UserResponse addUser(UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;


	UserResponse lookupUser(String userId) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException,
			URISyntaxException, CertificateException;

	UserResponse modifyUser(UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	UserResponse modifyUserSendMail(UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	UserResponse deleteUser(String userId, UserRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	ErrorResponse password1stUser(String userId, String clientId)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException;

	ClientResponse otpCreate(String userId, String clientId, ClientRequest request)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	AuthorizationResponse authorize(String username, String password, String otp, String msisdn, Authorize authorize,
			AuthorizationType authType) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException,
			URISyntaxException, CertificateException;

	MessageResponse passwordRecoveryUser(String msisdn, String packageId)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	TokenResponse token(Token token, String authorization) throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, IOException, URISyntaxException, CertificateException;

	UserInfoResponse userInfo(String accessToken)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException;

	AuthorizeErrorResponse password2stUser(String msisdn, String clientId, String redirectUri)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException;

	TokenNipResponse sendTokenNip(String username)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException;

	TokenNipResponse sendTokenNipRC(String username)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException;

	TokenNipResponse validateTokenNip(String username, String requestId, String otpCode)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException;

	RevokeResponse revoke(String accessToken) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			IOException, CertificateException, URISyntaxException;

	MessageResponse resendKeyEmail(String email)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

	MessageResponse activateEmail(String activationKey)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException;

}
