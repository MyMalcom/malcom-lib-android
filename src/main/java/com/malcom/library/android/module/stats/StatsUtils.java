package com.malcom.library.android.module.stats;

import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.utils.MalcomHttpOperations;

public class StatsUtils
{
	public final static String beaconURL = "/v1/beacon";

	public static void sendBeaconToMalcom(String beaconData) throws Exception
	{

		String url = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL) + beaconURL;
		String appCode = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID);
		String appSecretKey = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);

		MalcomHttpOperations.sendPostToMalcom(url, beaconURL, beaconData, appCode, appSecretKey);
	}
}
