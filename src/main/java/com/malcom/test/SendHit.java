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

public class SendHit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
try {
			
			String malcomDate = HttpDateUtils.formatDate(new Date());

			URL url = new URL("http://malcom-api-dev.elasticbeanstalk.com/v1/campaigns/7/hit/IMPRESSION/application/cef7c5ed-7bda-462a-a554-e8c32c607b27/udid/1");

			String password = "GET\n" +
				"\n" +
		 		"\n" +
		 		"\n" +
		 		"x-mcm-date:"+malcomDate+"\n"+
		 		"/v1/campaigns/7/hit/IMPRESSION/application/cef7c5ed-7bda-462a-a554-e8c32c607b27/udid/1";
					
			System.out.println("password: "+password);
			
			password = DigestUtils.calculateRFC2104HMAC(password, "tBksMRVJEVCzBu6+INF6Ww==");
			
			System.out.println("password: "+password);
			
			Map<String, String> headersData = new HashMap<String, String>();
			headersData.put("Authorization", "basic " + new String(Base64.encode("cef7c5ed-7bda-462a-a554-e8c32c607b27" + ":" + password).getBytes()));
			
			headersData.put("x-mcm-date", malcomDate);
			
			System.out.println("header: "+headersData);
			
			ToolBox.net_httpclient_doAction(HTTP_METHOD.GET, url.toString(), "", headersData);
		
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
