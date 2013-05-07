package com.malcom.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.malcom.library.android.utils.HttpDateUtils;
import com.malcom.library.android.utils.ToolBox;
import com.malcom.library.android.utils.ToolBox.HTTP_METHOD;
import com.malcom.library.android.utils.encoding.DigestUtils;
import com.malcom.library.android.utils.encoding.base64.Base64;

public class SendBeacon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			String verb = "POST";
			String contentType = "application/json";
			String applicationCode = "6839ef63-73c8-4d7f-b7d9-4b51b4738377";
			String applicationSecretKey = "V6FjARwoKYFtasvEvmyU4g==";
			String resource = "/malcom-api/v1/beacon";
			String malcomDate = HttpDateUtils.formatDate(new Date());
			String theJSON = "{\"beacon\":{\"app_version\":1,\"application_code\":\"6839ef63-73c8-4d7f-b7d9-4b51b4738377\",\"device_model\":\"i386\",\"device_os\":3,\"device_platform\":\"iOS\",\"city\":\"barcelona\",\"tags\":[\"málagagoles\",\"tag2\",\"tag3\"],\"location\":{\"accuracy\":0,\"latitude\":41.389939,\"longitude\":2.177603,\"timestamp\":\"2009-09-18T14:49:29.500+02:00\"},\"started_on\":1.253278154054249E9,\"stopped_on\":1.253278169438035E9,\"subbeacons\":[{\"name\":\"categoria uno\",\"started_on\":0.22,\"stopped_on\":0.33},{\"name\":\"categoria dos\",\"started_on\":0.22,\"stopped_on\":0.33},{\"name\":\"categoria tres\",\"started_on\":0.22,\"stopped_on\":0.33},{\"name\":\"categoria cuatro\",\"started_on\":0.22,\"stopped_on\":0.33}],\"udid\":\"7AA06235-E805-542B-8410-7DABA1726018\"}}";;
			String endpoint = "http://localhost:8081" + resource;		
			String md5  = ToolBox.md5_calculateMD5(theJSON);
			
			Map<String, String> headersData = new HashMap<String, String>();
			headersData.put("Content-Type", "application/json");
			headersData.put("content-md5", md5);
			headersData.put("x-mcm-date", malcomDate);
		
			String dataToSign = ToolBox.deliveries_getDataToSign(ToolBox.getCanonicalizedMalcomHeaders(headersData), contentType, null, verb, resource, md5);	
			String password = DigestUtils.calculateRFC2104HMAC(dataToSign, applicationSecretKey);
		
			// Complete headers with authorization
			headersData.put("Authorization", "basic " + new String(Base64.encode(applicationCode + ":" + password).getBytes()));
		
		
			System.err.println(endpoint);
			ToolBox.net_httpclient_doAction(HTTP_METHOD.POST,endpoint, theJSON, headersData);
		
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
