package com.malcom.library.android.utils;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.malcom.library.android.utils.ToolBox.HTTP_METHOD;
import com.malcom.library.android.utils.encoding.DigestUtils;
import com.malcom.library.android.utils.encoding.base64.Base64;

/**
 * Because all operations need the same headers, we centralize the
 * request by using this utility class.
 * 
 * 
 * @author Malcom Ventures S.L
 * @since  2012
 *
 */
public class MalcomHttpOperations {
	
	private static final String LOG_TAG = "Malcom HTTP Operation";

	/**
	 * A POST operation to Malcom API.
	 * 
	 * @param url			Url for the POST operation
	 * @param svcUrl		Service endpoint
	 * @param beaconData	JSON data
	 * @param appCode		Application Code
	 * @param appSecretKey	Application Secret key
	 * @throws Exception	In case of any error.
	 */
	public static void sendPostToMalcom(String url, String svcUrl, String beaconData, String appCode, String appSecretKey) throws Exception{
		
		sendRequestToMalcom(url, svcUrl, beaconData, appCode, appSecretKey, HTTP_METHOD.POST);
	}
	
	/**
	 * A operation to Malcom API.
	 * 
	 * @param url			Url for the POST operation
	 * @param svcUrl		Service endpoint
	 * @param json			JSON data
	 * @param appCode		Application Code
	 * @param appSecretKey	Application Secret key
	 * @param method		HTTP method
	 * @throws Exception	In case of any error.
	 */
	public static void sendRequestToMalcom(String url, String svcUrl, String json, String appCode, String appSecretKey, HTTP_METHOD method) throws Exception{
		
		String result = "";
		
		Log.d(LOG_TAG, ">>> sendRequestToMalcom appId: " + appCode);
		
		URL urlPath = null;
		try {
			String malcomDate = HttpDateUtils.formatDate(new Date());
			
			urlPath = new URL(url);
						  
			// Prepare required data for headers, these headers are requested by Malcom API.
			String headers = "x-mcm-date:" + malcomDate + "\n";
			String md5 = ToolBox.md5_calculateMD5(json);
			String password = ToolBox.deliveries_getDataToSign(headers, "application/json", null, method.name(), svcUrl, md5);
						
			password = DigestUtils.calculateRFC2104HMAC(password, appSecretKey);
			
			Map<String, String> headersData = new HashMap<String, String>();
			headersData.put("Authorization", "basic " + new String(Base64.encode(new String(appCode + ":" + password).getBytes())));
			headersData.put("Content-Type", "application/json");
			headersData.put("content-md5", md5);
			headersData.put("x-mcm-date", malcomDate);
			
			// Send request to Malcom and log it
			Log.d(LOG_TAG, ">>> sendRequestToMalcom " + method.name() + " " + url + " headers: " + headersData);
			result = ToolBox.net_httpclient_doAction(method, url.toString(), json, headersData);
			Log.d(LOG_TAG, "<<< sendRequestToMalcom result: " + result);

		} catch (Exception e) {
			Log.e(LOG_TAG, "<<< sendRequestToMalcom ERROR: " + result + " - " + e.getMessage(), e);
		}
	}
	
}
