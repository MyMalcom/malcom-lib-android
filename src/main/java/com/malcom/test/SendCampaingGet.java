package com.malcom.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.malcom.library.android.utils.HttpDateUtils;
import com.malcom.library.android.utils.ToolBox;
import com.malcom.library.android.utils.ToolBox.HTTP_METHOD;
import com.malcom.library.android.utils.encoding.DigestUtils;
import com.malcom.library.android.utils.encoding.base64.Base64;

public class SendCampaingGet {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String applicationCode = "6839ef63-73c8-4d7f-b7d9-4b51b4738377";
		String applicationSecretKey = "V6FjARwoKYFtasvEvmyU4g==";
		String resource = "/malcom-api/v1/campaigns/application/" + applicationCode + "/udid/udid";
		String malcomDate = HttpDateUtils.formatDate(new Date());
		String verb = "GET";
		String contentType = "application/json";
		// TODO Auto-generated method stub
		String endpoint = "http://localhost:8081" + resource;
		String md5 = ToolBox.md5_calculateMD5("");
	
		Map<String, String> headersData = new HashMap<String, String>();
		headersData.put("Content-Type", "application/json");
		headersData.put("content-md5", md5);
		headersData.put("x-mcm-date", malcomDate);
	
		String dataToSign = ToolBox.deliveries_getDataToSign(ToolBox.getCanonicalizedMalcomHeaders(headersData), contentType, null, verb, resource, md5);	
		String password = DigestUtils.calculateRFC2104HMAC(dataToSign, applicationSecretKey);
		
		// Complete headers with authorization
		headersData.put("Authorization", "basic " + new String(Base64.encode(applicationCode + ":" + password).getBytes()));
		
		ToolBox.net_httpclient_doAction(HTTP_METHOD.GET, endpoint, "", headersData);
		
		
	}
	
	
	
}
