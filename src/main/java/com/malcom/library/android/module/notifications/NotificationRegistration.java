package com.malcom.library.android.module.notifications;

/**
 * 
 * Class that holds the Malcom device registration information.
 * 
 *	{"NotificationRegistration":
 *   
 *		{
 *		 "udid":""
 *		 "token":"", 
 *	     "applicationCode":"",
 *	     "devicePlatform":"ANDROID",
 *	     "environment":""
 *	     }
 *	}
 *
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class NotificationRegistration {
	
	private String udid;
    private String token;
    private String applicationCode;
    
    private String devicePlatform;
    
    private String environment;

	public NotificationRegistration() {
		devicePlatform = "ANDROID";
	}

	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public String getApplicationCode() {
		return applicationCode;
	}
	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getDevicePlatform() {
		return devicePlatform;
	}
	public void setDevicePlatform(String devicePlatform) {
		this.devicePlatform = devicePlatform;
	}

	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	
}
