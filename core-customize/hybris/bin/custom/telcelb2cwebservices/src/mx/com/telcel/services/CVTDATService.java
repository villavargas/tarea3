package mx.com.telcel.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import mx.com.telcel.facades.cvtdat.data.UserInfoCVTData;
import mx.com.telcel.facades.cvtdat.data.UserInfoDATData;
import mx.com.telcel.models.cvtdat.UserInfo;
import mx.com.telcel.models.cvtdat.UserInfoCVTDATResponse;


public interface CVTDATService
{

	public UserInfoCVTDATResponse userInfoCVT(final String accesoSeguro, final String password)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException;

	public UserInfoDATData saveUserInfoDAT(String base64DAT);

	public UserInfoCVTData saveUserInfoCVT(UserInfo userInfo);

	public UserInfoDATData existUserDAT(String username);

	public UserInfoCVTData existUserCVT(String username);

	public UserInfoDATData decodeUserDAT(String base64DAT);

	public UserInfoCVTData decodeUserCVT(UserInfo userInfo);

}
